// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.nasller.requestmapper.config;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.ide.util.gotoByName.LanguageRef;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;


@State(name = "RequestMapperGotoUrlConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class RequestMapperGotoUrlConfiguration extends ChooseByNameFilterConfiguration<LanguageRef> {
  public static RequestMapperGotoUrlConfiguration getInstance(Project project) {
    return project.getService(RequestMapperGotoUrlConfiguration.class);
  }

  @Override
  protected String nameForElement(LanguageRef type) {
    return type.getId();
  }
}