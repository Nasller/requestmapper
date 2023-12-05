package com.nasller.requestmapper

import com.intellij.ide.actions.SearchEverywherePsiRenderer
import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.GotoClassSymbolConfiguration
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.util.text.MatcherHolder
import com.intellij.util.ui.JBInsets
import com.nasller.requestmapper.RequestMappingModel.Companion.addRightModuleComponent
import com.nasller.requestmapper.contributors.RequestMappingContributor
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JList
import javax.swing.ListCellRenderer

class RequestMappingGotoSEContributor(event: AnActionEvent) : AbstractGotoSEContributor(event) {
    val project: Project = myProject
    private val myFilter: PersistentSearchEverywhereContributorFilter<LanguageRef>
    init {
        val items = RequestMappingContributor.getExtensions().map(RequestMappingContributor::getLanguageRef)
        val persistentConfig = GotoClassSymbolConfiguration.getInstance(myProject)
        myFilter = PersistentSearchEverywhereContributorFilter(items, persistentConfig, LanguageRef::displayName, LanguageRef::icon)
    }

    override fun getSearchProviderId(): String = SEARCH_PROVIDER_ID

    override fun createModel(project: Project): FilteringGotoByModel<*> {
        val model = RequestMappingModel(project, RequestMappingContributor.getExtensions())
        model.setFilterItems(myFilter.selectedElements)
        return model
    }

    override fun getSortWeight() = 1000

    override fun getGroupName() = "UrlMapping"

    override fun showInFindResults() = true

    override fun getActions(onChanged: Runnable): List<AnAction> = doGetActions(myFilter, null, onChanged)

    override fun getElementsRenderer(): ListCellRenderer<Any> {
        return object : SearchEverywherePsiRenderer(this) {
            override fun getListCellRendererComponent(list: JList<*>, value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                if (value !is RequestMappingItem) return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                removeAll()
                addRightModuleComponent(value, list, isSelected)
                val leftRenderer = RequestMappingModel.MyLeftRenderer(MatcherHolder.getAssociatedMatcher(list)).apply {
                    ipad = JBInsets.create(1, 0)
                }
                add(leftRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus), BorderLayout.WEST)
                accessibleContext = leftRenderer.accessibleContext
                background = leftRenderer.background
                return this
            }
        }
    }

    companion object{
        const val SEARCH_PROVIDER_ID = "SearchEverywhere.RequestMapping"
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent) = RequestMappingGotoSEContributor(initEvent)
    }
}