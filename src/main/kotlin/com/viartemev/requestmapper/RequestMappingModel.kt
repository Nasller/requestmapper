package com.viartemev.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.ModuleRendererFactory
import com.intellij.ide.util.NavigationItemListCellRenderer
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.lang.LangBundle
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.problems.WolfTheProblemSolver
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.IdeUICustomization
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.speedSearch.SpeedSearchUtil
import com.intellij.util.IconUtil
import com.intellij.util.text.Matcher
import com.intellij.util.text.MatcherHolder
import com.intellij.util.ui.UIUtil
import com.viartemev.requestmapper.RequestMappingModel.LeftRenderer
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*

class RequestMappingModel(project: Project, contributors: List<ChooseByNameContributor>) : FilteringGotoByModel<LanguageRef>(project, contributors), DumbAware {

    override fun getItemProvider(context: PsiElement?): ChooseByNameItemProvider = RequestMappingItemProvider()

    override fun filterValueFor(item: NavigationItem): LanguageRef? = LanguageRef.forNavigationitem(item)

    override fun getPromptText(): String = "Enter mapping url"

    override fun getNotInMessage(): String = IdeUICustomization.getInstance().projectMessage("label.no.matches.found.in.project")

    override fun getNotFoundMessage(): String = IdeBundle.message("label.no.matches.found")

    override fun getCheckBoxName(): String = IdeUICustomization.getInstance().projectMessage("checkbox.include.non.project.items")

    override fun loadInitialCheckBoxState(): Boolean = PropertiesComponent.getInstance(myProject).getBoolean("RequestMapping.includeLibraries")

    override fun saveInitialCheckBoxState(state: Boolean) {
        PropertiesComponent.getInstance(myProject).setValue("RequestMapping.includeLibraries",state)
    }

    override fun getSeparators(): Array<String> = emptyArray()

    override fun getFullName(element: Any): String? = getElementName(element)

    override fun willOpenEditor(): Boolean = false

    override fun getListCellRenderer(): ListCellRenderer<*> {
        return object : NavigationItemListCellRenderer() {
            override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                removeAll()
                val factory = ModuleRendererFactory.findInstance(value)
                val left = LeftRenderer(!factory.rendersLocationString(), MatcherHolder.getAssociatedMatcher(list))
                val leftCellRendererComponent = left.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                (leftCellRendererComponent as JComponent).isOpaque = false
                add(leftCellRendererComponent, BorderLayout.WEST)
                background = leftCellRendererComponent.background
                val locationLabel = JLabel(value.psiElement.getIcon(Iconable.ICON_FLAG_READ_STATUS), SwingConstants.RIGHT)
                locationLabel.horizontalTextPosition = SwingConstants.LEFT
                locationLabel.foreground = leftCellRendererComponent.background
                add(locationLabel, BorderLayout.EAST)
                return this
            }
        }
    }

    private class LeftRenderer(val myRenderLocation: Boolean, private val myMatcher: Matcher?) : ColoredListCellRenderer<Any>() {
        override fun customizeCellRenderer(list: JList<*>, value: Any, index: Int, selected: Boolean, hasFocus: Boolean) {
            var bgColor = UIUtil.getListBackground()
            if (value is PsiElement && !value.isValid) {
                icon = IconUtil.getEmptyIcon(false)
                append(LangBundle.message("label.invalid"), SimpleTextAttributes.ERROR_ATTRIBUTES)
            } else if (value is NavigationItem) {
                val presentation = value.presentation ?: error(
                    "PSI elements displayed in choose by name lists must return a non-null value from getPresentation(): element " +
                            value + ", class " + value.javaClass.name
                )
                val name = presentation.presentableText ?: error(
                    "PSI elements displayed in choose by name lists must return a non-null value from getPresentation().getPresentableName: element " +
                            value + ", class " + value.javaClass.name
                )
                var color = list.foreground
                var isProblemFile = if (value is PsiElement) {
                    val project = (value as PsiElement).project
                    val virtualFile = PsiUtilCore.getVirtualFile(value as PsiElement)
                    virtualFile != null && WolfTheProblemSolver.getInstance(project).isProblemFile(virtualFile)
                } else false
                val psiElement = getPsiElement(value)
                if (psiElement != null && psiElement.isValid) {
                    val project = psiElement.project
                    val virtualFile = PsiUtilCore.getVirtualFile(psiElement)
                    isProblemFile = virtualFile != null && WolfTheProblemSolver.getInstance(project).isProblemFile(virtualFile)
                    val fileColor = if (virtualFile == null) null else VfsPresentationUtil.getFileBackgroundColor(project, virtualFile)
                    if (fileColor != null) {
                        bgColor = fileColor
                    }
                }
                val status = NavigationItemFileStatus.get(value)
                if (status !== FileStatus.NOT_CHANGED) {
                    color = status.color
                }
                val textAttributes = NodeRenderer.getSimpleTextAttributes(presentation).toTextAttributes()
                if (isProblemFile) {
                    textAttributes.effectType = EffectType.WAVE_UNDERSCORE
                    textAttributes.effectColor = JBColor.red
                }
                textAttributes.foregroundColor = color
                val nameAttributes = SimpleTextAttributes.fromTextAttributes(textAttributes)
                SpeedSearchUtil.appendColoredFragmentForMatcher(name, this, nameAttributes, myMatcher, bgColor, selected)
                icon = presentation.getIcon(false)
                if (myRenderLocation) {
                    val containerText = presentation.locationString
                    if (!containerText.isNullOrEmpty()) {
                        append(" $containerText", SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY))
                    }
                }
            } else {
                icon = IconUtil.getEmptyIcon(false)
                append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground))
            }
            setPaintFocusBorder(false)
            background = if (selected) UIUtil.getListSelectionBackground(true) else bgColor
        }
    }

    private companion object{
        @JvmStatic
        private fun getPsiElement(o: Any?): PsiElement? {
            return if (o is PsiElement) o else if (o is PsiElementNavigationItem) o.targetElement else null
        }
    }
}
