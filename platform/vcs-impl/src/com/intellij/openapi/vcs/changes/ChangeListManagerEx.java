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
package com.intellij.openapi.vcs.changes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredDispatchThread;

import java.util.Collection;
import java.util.List;

/**
 * @author yole
 */
public abstract class ChangeListManagerEx extends ChangeListManager {
  @Nullable
  public abstract LocalChangeList getIdentityChangeList(Change change);
  public abstract boolean isInUpdate();
  public abstract Collection<LocalChangeList> getInvolvedListsFilterChanges(final Collection<Change> changes, final List<Change> validChanges);


  public abstract LocalChangeList addChangeList(@NotNull String name, @Nullable final String comment, @Nullable Object data);

  /**
   * Blocks modal dialogs that we don't want to popup during some process, for example, above the commit dialog.
   */
  @RequiredDispatchThread
  public abstract void blockModalNotifications();

  /**
   * Unblocks modal dialogs showing and shows the ones which were queued.
   */
  @RequiredDispatchThread
  public abstract void unblockModalNotifications();
}