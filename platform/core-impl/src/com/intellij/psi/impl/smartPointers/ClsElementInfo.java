/*
 * Copyright 2000-2011 JetBrains s.r.o.
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
package com.intellij.psi.impl.smartPointers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnchor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import consulo.annotations.RequiredReadAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: cdr
 */
public class ClsElementInfo extends SmartPointerElementInfo {
  private final PsiAnchor.StubIndexReference myStubIndexReference;

  public ClsElementInfo(@NotNull PsiAnchor.StubIndexReference stubReference) {
    myStubIndexReference = stubReference;
  }

  @RequiredReadAction
  @Override
  public PsiElement restoreElement() {
    return myStubIndexReference.retrieve();
  }

  @Override
  public int elementHashCode() {
    return myStubIndexReference.hashCode();
  }

  @Override
  public boolean pointsToTheSameElementAs(@NotNull SmartPointerElementInfo other) {
    return other instanceof ClsElementInfo && myStubIndexReference.equals(((ClsElementInfo)other).myStubIndexReference);
  }

  @Override
  public VirtualFile getVirtualFile() {
    return myStubIndexReference.getVirtualFile();
  }

  @Override
  public Segment getRange() {
    return null;
  }

  @NotNull
  @Override
  public Project getProject() {
    return myStubIndexReference.getProject();
  }

  @Nullable
  @Override
  public Segment getPsiRange() {
    return null;
  }

  @Override
  public PsiFile restoreFile() {
    return myStubIndexReference.getFile();
  }

  @Override
  public String toString() {
    return myStubIndexReference.toString();
  }
}
