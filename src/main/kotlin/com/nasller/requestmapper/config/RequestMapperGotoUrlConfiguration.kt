// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.nasller.requestmapper.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.ide.util.gotoByName.LanguageRef;
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;

@Service(Service.Level.PROJECT)
@State(name = "RequestMapperGotoUrlConfiguration", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class RequestMapperGotoUrlConfiguration: ChooseByNameFilterConfiguration<LanguageRef>(){
    override fun nameForElement(type: LanguageRef) = type.id

    companion object{
        fun getInstance(project: Project): RequestMapperGotoUrlConfiguration{
            return project.getService(RequestMapperGotoUrlConfiguration::class.java)
        }
    }
}