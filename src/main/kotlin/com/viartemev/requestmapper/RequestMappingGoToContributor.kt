package com.viartemev.requestmapper

import com.intellij.ide.actions.SearchEverywherePsiRenderer
import com.intellij.ide.actions.searcheverywhere.*
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.GotoClassSymbolConfiguration
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.impl.EditorTabPresentationUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.speedSearch.SpeedSearchUtil
import com.intellij.util.ui.UIUtil
import com.viartemev.requestmapper.extensions.Extensions
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.SwingConstants

class RequestMappingGoToContributor(event: AnActionEvent) : AbstractGotoSEContributor(event) {
    val project: Project = myProject
    private val myFilter: PersistentSearchEverywhereContributorFilter<LanguageRef>
    init {
        val items = LanguageRef.forAllLanguages()
        val persistentConfig = GotoClassSymbolConfiguration.getInstance(myProject)
        myFilter = PersistentSearchEverywhereContributorFilter(items, persistentConfig, LanguageRef::displayName, LanguageRef::icon)
    }

    override fun createModel(project: Project): FilteringGotoByModel<*> {
        val model = RequestMappingModel(project, Extensions.getExtensions())
        model.setFilterItems(myFilter.selectedElements)
        return model
    }

    override fun getSortWeight(): Int {
        return 1000
    }

    override fun getGroupName(): String {
        return "Request Mapping"
    }

    override fun showInFindResults(): Boolean {
        return false
    }

    override fun getActions(onChanged: Runnable): List<AnAction> {
        return doGetActions(myFilter, SearchEverywhereFiltersStatisticsCollector.LangFilterCollector(), onChanged)
    }

    override fun getElementsRenderer(): ListCellRenderer<Any> {
        return object : SearchEverywherePsiRenderer(this) {
            override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                removeAll()
                val matchers = getItemMatchers(list, value)
                val presentation = value.presentation
                val containingFile = value.targetElement.containingFile
                val bgColor = if (isSelected) UIUtil.getListSelectionBackground(true) else EditorTabPresentationUtil.getFileBackgroundColor(containingFile.project, containingFile.virtualFile)
                val locationLabel = JLabel(presentation.locationString, value.targetElement.getIcon(Iconable.ICON_FLAG_READ_STATUS), SwingConstants.RIGHT)
                locationLabel.horizontalTextPosition = SwingConstants.LEFT
                locationLabel.foreground = if (isSelected) UIUtil.getListSelectionForeground(true) else UIUtil.getInactiveTextColor()
                add(locationLabel, BorderLayout.EAST)
                background = bgColor
                val leftRenderer = object : ColoredListCellRenderer<Any>() {
                    override fun customizeCellRenderer(list: JList<*>, value: Any, index: Int, selected: Boolean, hasFocus: Boolean) {
                        icon = presentation.getIcon(false)
                        val nameAttributes = SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, null)
                        SpeedSearchUtil.appendColoredFragmentForMatcher(presentation.presentableText!!, this, nameAttributes, matchers.nameMatcher, bgColor, selected)
                    }
                }
                add(leftRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus), BorderLayout.WEST)
                return this
            }
        }
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return RequestMappingGoToContributor(initEvent)
        }
    }
}
