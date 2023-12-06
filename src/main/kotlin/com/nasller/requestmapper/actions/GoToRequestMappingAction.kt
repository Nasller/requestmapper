package com.nasller.requestmapper.actions

import com.intellij.ide.actions.GotoActionBase
import com.intellij.ide.ui.laf.darcula.ui.DarculaEditorTextFieldBorder
import com.intellij.ide.util.gotoByName.ChooseByNameFilter
import com.intellij.ide.util.gotoByName.ChooseByNameModelEx
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.DumbAware
import com.intellij.util.ui.JBInsets
import com.nasller.requestmapper.RequestMappingItem
import com.nasller.requestmapper.RequestMappingModel
import com.nasller.requestmapper.config.RequestMapperGotoUrlConfiguration
import com.nasller.requestmapper.contributors.RequestMappingContributor
import java.awt.Component
import java.awt.Insets

class GoToRequestMappingAction : GotoActionBase(), DumbAware {
    override fun gotoActionPerformed(e: AnActionEvent) {
        val project = e.getData(PROJECT) ?: return
        val contributors = RequestMappingContributor.getExtensions()
        val model = RequestMappingModel(project, contributors)
        model.setFilterItems(contributors.map(RequestMappingContributor::getLanguageRef))
        val start = getInitialText(true, e)
        val popup = CustomChooseByNamePopup.createPopup(project, model,
            ChooseByNameModelEx.getItemProvider(model, getPsiContext(e)), start.first,
            model.willOpenEditor() && FileEditorManagerEx.getInstanceEx(project).hasSplitOrUndockedWindows(), start.second)
        showNavigationPopup(object : GotoActionCallback<LanguageRef>() {
            override fun createFilter(popup: ChooseByNamePopup): ChooseByNameFilter<LanguageRef> {
                popup.setCheckBoxShortcut(CustomShortcutSet.EMPTY)
                popup.textField.apply {
                    columns = 100
                    border = object : DarculaEditorTextFieldBorder() {
                        override fun getBorderInsets(c: Component): Insets {
                            return JBInsets.create(6, 4).asUIResource()
                        }
                    }
                }
                return object : ChooseByNameFilter<LanguageRef>(popup, model, RequestMapperGotoUrlConfiguration.getInstance(project), project) {
                    override fun textForFilterValue(value: LanguageRef) = value.displayName

                    override fun iconForFilterValue(value: LanguageRef) = value.icon

                    override fun getAllFilterValues() = RequestMappingContributor.getExtensions().map(RequestMappingContributor::getLanguageRef)
                }
            }

            override fun elementChosen(popup: ChooseByNamePopup, element: Any) {
                if (element is RequestMappingItem && element.canNavigate()) {
                    element.navigate(true)
                }
            }
        }, "Request mapper url", popup, true)
    }
}