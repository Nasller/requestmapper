package com.nasller.requestmapper

import com.intellij.ide.actions.SearchEverywherePsiRenderer
import com.intellij.ide.util.ModuleRendererFactory
import com.intellij.ide.util.NavigationItemListCellRenderer
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.problems.WolfTheProblemSolver
import com.intellij.psi.PsiFile
import com.intellij.ui.ColorUtil
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.speedSearch.SpeedSearchUtil
import com.intellij.util.IconUtil
import com.intellij.util.text.Matcher
import com.intellij.util.text.MatcherHolder
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.nasller.requestmapper.MyLeftRenderer.Companion.addRightModuleComponent
import com.nasller.requestmapper.annotations.MappingAnnotation
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.SwingConstants

class MyNavigationItemListCellRenderer : NavigationItemListCellRenderer() {
    override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        return runReadAction {
            removeAll()
            addRightModuleComponent(value, list, isSelected)
            val leftRenderer = MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list))
                .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            add(leftRenderer, BorderLayout.WEST)
            accessibleContext = leftRenderer.accessibleContext
            background = leftRenderer.background
            border = MyLeftRenderer.customBorder
            return@runReadAction this
        }
    }
}

class MySearchEverywherePsiRenderer(disposable: Disposable) : SearchEverywherePsiRenderer(disposable) {
    override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        return runReadAction {
            removeAll()
            addRightModuleComponent(value, list, isSelected)
            val leftRenderer = MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list)).apply {
                ipad = JBInsets.create(1, 0)
            }
            add(leftRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus), BorderLayout.WEST)
            accessibleContext = leftRenderer.accessibleContext
            background = leftRenderer.background
            return@runReadAction this
        }
    }
}

class MyLeftRenderer(private val myMatcher: Matcher?) : ColoredListCellRenderer<Any>() {
    override fun customizeCellRenderer(list: JList<*>, value: Any, index: Int, selected: Boolean, hasFocus: Boolean) {
        var bgColor = UIUtil.getListBackground()
        if (value is RequestMappingItem) {
            val presentation = value.presentation as RequestMappingItem.RequestMappingItemPresentation
            icon = presentation.getIcon(false)
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
        } else {
            icon = IconUtil.getEmptyIcon(false)
            append(value.toString(), SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground))
        }
        setPaintFocusBorder(false)
        background = if (selected) UIUtil.getListSelectionBackground(true) else bgColor
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
        val customBorder = JBUI.Borders.empty(2,0)

        fun JComponent.addRightModuleComponent(value: RequestMappingItem, list: JList<*>, isSelected: Boolean) {
            ModuleRendererFactory.findInstance(value).getModuleTextWithIcon(value.targetElement)?.let{
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