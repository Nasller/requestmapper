package com.viartemev.requestmapper

import com.intellij.ide.IdeBundle
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider
import com.intellij.ide.util.gotoByName.FilteringGotoByModel
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.ui.IdeUICustomization

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
}
