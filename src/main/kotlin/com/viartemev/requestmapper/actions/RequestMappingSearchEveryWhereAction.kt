package com.viartemev.requestmapper.actions

import com.intellij.ide.actions.SearchEverywhereBaseAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.UpdateInBackground
import com.intellij.openapi.project.DumbAware
import com.viartemev.requestmapper.RequestMappingGoToContributor

class RequestMappingSearchEveryWhereAction : SearchEverywhereBaseAction(), UpdateInBackground, DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        showInSearchEverywherePopup(RequestMappingGoToContributor::class.simpleName!!, e, true, false)
    }
}
