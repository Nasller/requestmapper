package com.viartemev.requestmapper.actions

import com.intellij.ide.actions.GotoActionBase
import com.intellij.ide.util.gotoByName.ChooseByNameFilter
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.project.DumbAware
import com.viartemev.requestmapper.RequestMappingItem
import com.viartemev.requestmapper.RequestMappingModel
import com.viartemev.requestmapper.extensions.Extensions

class GoToRequestMappingAction : GotoActionBase(), DumbAware {
    override fun gotoActionPerformed(e: AnActionEvent) {
        val project = e.getData(PROJECT) ?: return
        val requestMappingModel = RequestMappingModel(project, Extensions.getExtensions())
        showNavigationPopup(e, requestMappingModel, object : GotoActionCallback<String>() {
            override fun createFilter(popup: ChooseByNamePopup): ChooseByNameFilter<String>? {
                popup.setCheckBoxShortcut(CustomShortcutSet.EMPTY)
                return super.createFilter(popup)
            }

            override fun elementChosen(popup: ChooseByNamePopup, element: Any) {
                if (element is RequestMappingItem && element.canNavigate()) {
                    element.navigate(true)
                }
            }
        }, false)
    }
}