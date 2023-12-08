package com.nasller.requestmapper

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor
import com.intellij.ide.actions.searcheverywhere.PersistentSearchEverywhereContributorFilter
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.nasller.requestmapper.config.RequestMapperGotoUrlConfiguration
import com.nasller.requestmapper.contributors.RequestMappingContributor

class RequestMappingGotoSEContributor(event: AnActionEvent) : AbstractGotoSEContributor(event) {
    private val myFilter: PersistentSearchEverywhereContributorFilter<LanguageRef> = PersistentSearchEverywhereContributorFilter(
        RequestMappingContributor.getExtensions().map(RequestMappingContributor::getLanguageRef),
        RequestMapperGotoUrlConfiguration.getInstance(myProject),
        LanguageRef::displayName, LanguageRef::icon)

    override fun getSearchProviderId() = SEARCH_PROVIDER_ID

    override fun createModel(project: Project) = RequestMappingModel(project).apply {
        setFilterItems(myFilter.selectedElements)
    }

    override fun getSortWeight() = 1000

    override fun getGroupName() = "UrlMapping"

    override fun showInFindResults() = true

    override fun getActions(onChanged: Runnable): List<AnAction> = doGetActions(myFilter, null, onChanged)

    override fun getElementsRenderer() = MySearchEverywherePsiRenderer(this)

    companion object{
        const val SEARCH_PROVIDER_ID = "SearchEverywhere.RequestMapping"
    }

    class Factory : SearchEverywhereContributorFactory<Any> {
        override fun createContributor(initEvent: AnActionEvent) = RequestMappingGotoSEContributor(initEvent)
    }
}