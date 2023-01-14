package com.viartemev.requestmapper

import com.intellij.ide.actions.SearchEverywherePsiRenderer
import com.intellij.ide.actions.searcheverywhere.*
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.GotoClassSymbolConfiguration
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.util.text.MatcherHolder
import com.viartemev.requestmapper.RequestMappingModel.Companion.addLocationLabel
import com.viartemev.requestmapper.extensions.Extensions
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class RequestMappingSearchEverywhereContributor(event: AnActionEvent) : AbstractGotoSEContributor(event) {
    val project: Project = myProject
    private val myFilter: PersistentSearchEverywhereContributorFilter<LanguageRef>
    init {
        val items = LanguageRef.forAllLanguages()
        val persistentConfig = GotoClassSymbolConfiguration.getInstance(myProject)
        myFilter = PersistentSearchEverywhereContributorFilter(items, persistentConfig, LanguageRef::displayName, LanguageRef::icon)
    }

    override fun getSearchProviderId(): String = SearchProviderId

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
                val component = RequestMappingModel.MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list))
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                add(component, BorderLayout.WEST)
                addLocationLabel(value, list, isSelected)
                background = component.background
                return this
            }
        }
    }

    companion object{
        const val SearchProviderId = "SearchEverywhere.RequestMapping"
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent): SearchEverywhereContributor<Any> {
            return RequestMappingSearchEverywhereContributor(initEvent)
        }
    }
}