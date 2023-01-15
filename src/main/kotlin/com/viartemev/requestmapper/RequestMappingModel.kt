package com.viartemev.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.NavigationItemListCellRenderer
import com.intellij.ide.util.PropertiesComponent
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
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.problems.WolfTheProblemSolver
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
import javax.swing.*

class RequestMappingModel(project: Project, contributors: List<ChooseByNameContributorEx>) : FilteringGotoByModel<LanguageRef>(project, contributors), DumbAware {

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
                val component = MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list))
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                add(component, BorderLayout.WEST)
                addLocationLabel(value, list, isSelected)
                background = component.background
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
                val psiElement = value.targetElement
                if (psiElement.isValid) {
                    PsiUtilCore.getVirtualFile(psiElement)?.let {
                        val project = psiElement.project
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
                append("${presentation.getRequestMethod()}  ",getMethodSimpleTextAttributes(presentation.getRequestMethod(),textAttributes))
                SpeedSearchUtil.appendColoredFragmentForMatcher(presentation.presentableText, this, urlPathTextAttributes, myMatcher, bgColor, selected)
                val appendInfo = (if(presentation.getUrl().isNotBlank()) "url=${presentation.getUrl()}" else "" +
                        if(presentation.getParams().isNotBlank()) " params=${presentation.getUrl()}" else "").trim()
                if(appendInfo.isNotBlank()) append("  $appendInfo", urlPathTextAttributes)
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
        private val ANY = ColorUtil.fromHex("#00FACE")

        fun JComponent.addLocationLabel(value: RequestMappingItem, list: JList<*>, isSelected: Boolean) {
            add(JLabel(value.presentation.locationString, value.targetElement.getIcon(Iconable.ICON_FLAG_READ_STATUS), SwingConstants.RIGHT).apply {
                horizontalTextPosition = SwingConstants.LEFT
                foreground = if (isSelected) list.foreground else UIUtil.getInactiveTextColor()
            }, BorderLayout.EAST)
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
                MappingAnnotation.ANY_METHOD -> ANY
                else -> attributes.foregroundColor
            }
            return SimpleTextAttributes.fromTextAttributes(attributes)
        }
    }
}