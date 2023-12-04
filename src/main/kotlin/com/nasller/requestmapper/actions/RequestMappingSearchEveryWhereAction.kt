package com.nasller.requestmapper.actions

import com.intellij.ide.actions.SearchEverywhereBaseAction
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.nasller.requestmapper.RequestMappingGotoSEContributor

class RequestMappingSearchEveryWhereAction : SearchEverywhereBaseAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        showInSearchEverywherePopup(RequestMappingGotoSEContributor.SearchProviderId, e, true, false)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}