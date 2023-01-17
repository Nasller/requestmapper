package com.viartemev.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.NavigationItemListCellRenderer
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.problems.WolfTheProblemSolver
import com.intellij.psi.PsiFile
import com.intellij.ui.*
import com.intellij.ui.speedSearch.SpeedSearchUtil
import com.intellij.util.IconUtil
import com.intellij.util.SlowOperations
import com.intellij.util.text.Matcher
import com.intellij.util.text.MatcherHolder
import com.intellij.util.ui.JBUI.Borders
import com.intellij.util.ui.UIUtil
import com.viartemev.requestmapper.annotations.MappingAnnotation
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import javax.swing.*

class RequestMappingModel(project: Project, contributors: List<ChooseByNameContributorEx>) : FilteringGotoByModel<LanguageRef>(project, contributors), DumbAware {

    override fun filterValueFor(item: NavigationItem): LanguageRef? = (item as? RequestMappingItem)?.run { targetElement.language.let { LanguageRef.forLanguage(it) } }

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
                val component = MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list))
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                add(component, BorderLayout.WEST)
                background = component.background
                border = customBorder
                addRightModuleComponent(value, list, isSelected)
                return this
            }
        }
    }

    class MyLeftRenderer(private val myMatcher: Matcher?) : ColoredListCellRenderer<Any>() {
        override fun customizeCellRenderer(list: JList<*>, value: Any, index: Int, selected: Boolean, hasFocus: Boolean) {
            var bgColor = UIUtil.getListBackground()
            if (value is RequestMappingItem) {
                val presentation = value.presentation as RequestMappingItem.RequestMappingItemPresentation
                val textAttributes = NodeRenderer.getSimpleTextAttributes(presentation).toTextAttributes()
                val containingFile = presentation.containingFile
                if (containingFile?.isValid == true) {
                    getVirtualFile(containingFile)?.let {
                        val project = value.targetElement.project
                        VfsPresentationUtil.getFileBackgroundColor(project, it)?.apply { bgColor = this }
                        if (WolfTheProblemSolver.getInstance(project).isProblemFile(it)) {
                            textAttributes.effectType = EffectType.WAVE_UNDERSCORE
                            textAttributes.effectColor = JBColor.red
                        }
                    }
                }
                val status = NavigationItemFileStatus.get(value)
                if(status !== FileStatus.NOT_CHANGED) textAttributes.foregroundColor = status.color
                val urlPathTextAttributes = SimpleTextAttributes.fromTextAttributes(textAttributes)
                presentation.getRequestMethod().splitToSequence(" ").forEach {
                    append("$it ",getMethodSimpleTextAttributes(it,textAttributes))
                }
                SpeedSearchUtil.appendColoredFragmentForMatcher(" ${presentation.presentableText} ", this, urlPathTextAttributes, myMatcher, bgColor, selected)
                val appendInfo = (if(presentation.getUrl().isNotBlank()) "url=${presentation.getUrl()}" else "" +
                        if(presentation.getParams().isNotBlank()) " params=${presentation.getUrl()}" else "").trim()
                if(appendInfo.isNotBlank()) append("$appendInfo ", urlPathTextAttributes)
                append(presentation.locationString, SimpleTextAttributes.GRAYED_ATTRIBUTES)
                icon = presentation.getIcon(false)
            } else {
                icon = IconUtil.getEmptyIcon(false)
                append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground))
            }
            setPaintFocusBorder(false)
            background = if (selected) UIUtil.getListSelectionBackground(true) else bgColor
        }
    }

    companion object{
        private val GET = ColorUtil.fromHex("#61AFFE")
        private val POST = ColorUtil.fromHex("#49CC90")
        private val PUT = ColorUtil.fromHex("#FCA130")
        private val DELETE = ColorUtil.fromHex("#F93E3E")
        private val HEAD = ColorUtil.fromHex("#9012FE")
        private val PATCH = ColorUtil.fromHex("#50E3C2")
        private val OPTIONS = ColorUtil.fromHex("#0D5AA7")
        private val ANY = ColorUtil.fromHex("#E3FA00")
        private val customBorder = Borders.empty(2,0)

        fun JComponent.addRightModuleComponent(value: RequestMappingItem,list: JList<*>, isSelected: Boolean) {
            SlowOperations.allowSlowOperations(SlowOperations.RENDERING).use {
                PsiElementListCellRenderer.getModuleTextWithIcon(value.targetElement)
            }?.let{
                add(JLabel(it.text, it.icon, SwingConstants.RIGHT).apply {
                    horizontalTextPosition = SwingConstants.LEFT
                    foreground = if (isSelected) list.foreground else UIUtil.getInactiveTextColor()
                }, BorderLayout.EAST)
            }
        }

        private fun getVirtualFile(containingFile: PsiFile): VirtualFile? {
            var virtualFile = containingFile.virtualFile
            if (virtualFile == null) {
                val originalFile = containingFile.originalFile
                if (originalFile !== containingFile && originalFile.isValid) {
                    virtualFile = originalFile.virtualFile
                }
            }
            return virtualFile
        }

        private fun getMethodSimpleTextAttributes(method: String,textAttributes: TextAttributes) : SimpleTextAttributes {
            val attributes = TextAttributes()
            attributes.copyFrom(textAttributes)
            attributes.fontType = Font.BOLD
            attributes.foregroundColor = when(method){
                MappingAnnotation.GET_METHOD -> GET
                MappingAnnotation.POST_METHOD -> POST
                MappingAnnotation.PUT_METHOD -> PUT
                MappingAnnotation.DELETE_METHOD -> DELETE
                MappingAnnotation.HEAD_METHOD -> HEAD
                MappingAnnotation.PATCH_METHOD -> PATCH
                MappingAnnotation.OPTIONS_METHOD -> OPTIONS
                MappingAnnotation.ANY_METHOD -> ANY
                else -> attributes.foregroundColor
            }
            return SimpleTextAttributes.fromTextAttributes(attributes)
        }
    }
}