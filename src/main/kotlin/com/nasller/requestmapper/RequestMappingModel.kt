package com.nasller.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.ui.IdeUICustomization
import com.nasller.requestmapper.contributors.RequestMappingContributor

class RequestMappingModel(project: Project) : FilteringGotoByModel<LanguageRef>(project, RequestMappingContributor.getExtensions()), DumbAware {
    override fun filterValueFor(item: NavigationItem): LanguageRef? = (item as? RequestMappingItem)?.run {
        targetElement.language.let { LanguageRef.forLanguage(it) }
    }

    override fun getPromptText() = "Enter mapping url"

    override fun getNotInMessage() = IdeUICustomization.getInstance().projectMessage("label.no.matches.found.in.project", project.name)

    override fun getNotFoundMessage() = IdeBundle.message("label.no.matches.found")

    override fun getCheckBoxName() = IdeUICustomization.getInstance().projectMessage("checkbox.include.non.project.items")

    override fun loadInitialCheckBoxState() = PropertiesComponent.getInstance(myProject).getBoolean("RequestMapping.includeLibraries")

    override fun saveInitialCheckBoxState(state: Boolean) {
        PropertiesComponent.getInstance(myProject).setValue("RequestMapping.includeLibraries",state)
    }

    override fun getSeparators(): Array<String> = emptyArray()

    override fun getFullName(element: Any) = getElementName(element)

    override fun willOpenEditor() = true

    override fun getListCellRenderer() = MyNavigationItemListCellRenderer()
}