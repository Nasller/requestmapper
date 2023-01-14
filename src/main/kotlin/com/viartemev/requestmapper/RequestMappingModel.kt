package com.viartemev.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.NavigationItemListCellRenderer
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.lang.LangBundle
import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.problems.WolfTheProblemSolver
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.ui.*
import com.intellij.ui.speedSearch.SpeedSearchUtil
import com.intellij.util.IconUtil
import com.intellij.util.text.Matcher
import com.intellij.util.text.MatcherHolder
import com.intellij.util.ui.UIUtil
import com.viartemev.requestmapper.annotations.MappingAnnotation
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.SwingConstants

class RequestMappingModel(project: Project, contributors: List<ChooseByNameContributorEx>) : FilteringGotoByModel<LanguageRef>(project, contributors), DumbAware {

    override fun getItemProvider(context: PsiElement?): ChooseByNameItemProvider = RequestMappingItemProvider()

    override fun filterValueFor(item: NavigationItem): LanguageRef? = (item as? RequestMappingItem)?.let { item.targetElement.language.let { LanguageRef.forLanguage(it) } }

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

    override fun willOpenEditor(): Boolean = true

    override fun getListCellRenderer(): ListCellRenderer<*> {
        return object : NavigationItemListCellRenderer() {
            override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                removeAll()
                val component = MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list)).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                add(component, BorderLayout.WEST)
                background = component.background
                add(JLabel(value.presentation.locationString, value.targetElement.getIcon(Iconable.ICON_FLAG_READ_STATUS), SwingConstants.RIGHT).apply {
                    horizontalTextPosition = SwingConstants.LEFT
                    foreground = if (isSelected) UIUtil.getListSelectionForeground(true) else UIUtil.getInactiveTextColor()
                }, BorderLayout.EAST)
                return this
            }
        }
    }

    class MyLeftRenderer(private val myMatcher: Matcher?) : ColoredListCellRenderer<Any>() {
        override fun customizeCellRenderer(list: JList<*>, value: Any, index: Int, selected: Boolean, hasFocus: Boolean) {
            var bgColor = UIUtil.getListBackground()
            if (value is PsiElement && !value.isValid) {
                icon = IconUtil.getEmptyIcon(false)
                append(LangBundle.message("label.invalid"), SimpleTextAttributes.ERROR_ATTRIBUTES)
            } else if (value is PsiElementNavigationItem) {
                val presentation = value.presentation ?: error("PSI elements displayed in choose by name lists must return a non-null value from getPresentation(): element $value, class ${value.javaClass.name}")
                val name = presentation.presentableText ?: error("PSI elements displayed in choose by name lists must return a non-null value from getPresentation().getPresentableName: element $value, class ${value.javaClass.name}")
                val textAttributes = NodeRenderer.getSimpleTextAttributes(presentation).toTextAttributes()
                val psiElement = value.targetElement
                if (psiElement != null && psiElement.isValid) {
                    val project = psiElement.project
                    PsiUtilCore.getVirtualFile(psiElement)?.let {
                        VfsPresentationUtil.getFileBackgroundColor(project, it)?.apply { bgColor = this }
                        if (WolfTheProblemSolver.getInstance(project).isProblemFile(it)) {
                            textAttributes.effectType = EffectType.WAVE_UNDERSCORE
                            textAttributes.effectColor = JBColor.red
                        }
                    }
                }
                val status = NavigationItemFileStatus.get(value)
                textAttributes.foregroundColor = if (status !== FileStatus.NOT_CHANGED) status.color else list.foreground
                if(presentation is RequestMappingItem.RequestMappingItemPresentation) {
                    append("${presentation.getRequestMethod()}  ",getMethodSimpleTextAttributes(presentation.getRequestMethod(),textAttributes))
                }
                SpeedSearchUtil.appendColoredFragmentForMatcher(name, this, SimpleTextAttributes.fromTextAttributes(textAttributes), myMatcher, bgColor, selected)
                icon = presentation.getIcon(false)
            } else {
                icon = IconUtil.getEmptyIcon(false)
                append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground))
            }
            setPaintFocusBorder(false)
            background = if (selected) UIUtil.getListSelectionBackground(true) else bgColor
        }
    }

    private companion object{
        private val DELETE = ColorUtil.fromHex("#F93E3E")
        private val GET = ColorUtil.fromHex("#61AFFE")
        private val PUT = ColorUtil.fromHex("#FCA130")
        private val POST = ColorUtil.fromHex("#49CC90")

        private fun getMethodSimpleTextAttributes(method: String,textAttributes: TextAttributes) : SimpleTextAttributes {
            val attributes = TextAttributes()
            attributes.copyFrom(textAttributes)
            attributes.fontType = Font.BOLD
            attributes.foregroundColor = when(method){
                MappingAnnotation.DELETE_METHOD -> DELETE
                MappingAnnotation.GET_METHOD -> GET
                MappingAnnotation.PUT_METHOD -> PUT
                MappingAnnotation.POST_METHOD -> POST
                else -> attributes.foregroundColor
            }
            return SimpleTextAttributes.fromTextAttributes(attributes)
        }
    }
}
