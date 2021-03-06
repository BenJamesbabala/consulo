/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package com.intellij.psi.impl.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public abstract class CacheManager {
  @NotNull
  public static CacheManager getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, CacheManager.class);
  }

  @NotNull
  public abstract PsiFile[] getFilesWithWord(@NotNull String word, short occurenceMask, @NotNull GlobalSearchScope scope, final boolean caseSensitively);

  @NotNull
  public abstract VirtualFile[] getVirtualFilesWithWord(@NotNull String word, short occurenceMask, @NotNull GlobalSearchScope scope, final boolean caseSensitively);

  public abstract boolean processFilesWithWord(@NotNull Processor<PsiFile> processor,
                                               @NotNull String word,
                                               short occurenceMask,
                                               @NotNull GlobalSearchScope scope,
                                               final boolean caseSensitively);
}

