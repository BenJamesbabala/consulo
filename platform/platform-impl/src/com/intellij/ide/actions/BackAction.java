
/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredDispatchThread;

public class BackAction extends AnAction implements DumbAware {
  @RequiredDispatchThread
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
    if (project == null) return;
    IdeDocumentHistory.getInstance(project).back();
  }

  @RequiredDispatchThread
  @Override
  public void update(@NotNull AnActionEvent event){
    Presentation presentation = event.getPresentation();
    Project project = CommonDataKeys.PROJECT.getData(event.getDataContext());
    if (project == null || project.isDisposed()) {
      presentation.setEnabled(false);
      return;
    }
    presentation.setEnabled(IdeDocumentHistory.getInstance(project).isBackAvailable());
  }
}