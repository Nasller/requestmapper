package com.viartemev.requestmapper.actions

import com.intellij.ide.actions.SearchEverywhereBaseAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.UpdateInBackground
import com.intellij.openapi.project.DumbAware
import com.viartemev.requestmapper.RequestMappingGotoSEContributor

class RequestMappingSearchEveryWhereAction : SearchEverywhereBaseAction(), UpdateInBackground, DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        showInSearchEverywherePopup(RequestMappingGotoSEContributor.SearchProviderId, e, true, false)
    }
}