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
package com.intellij.codeInspection.reference;

import com.intellij.openapi.extensions.ExtensionPointName;

/**
 * Callback which gets called while a reference graph is being built during a global
 * inspection run.
 *
 * @author anna
 * @since 6.0
 * @see com.intellij.codeInspection.GlobalInspectionTool#getAnnotator
 */
public abstract class RefGraphAnnotator {
  public static final ExtensionPointName<RefGraphAnnotator> EP_NAME = ExtensionPointName.create("com.intellij.refGraphAnnotator");
  /**
   * Called before the references to the specified element have been collected.
   *
   * @param refElement the element the references to which are about to be collected.
   */
  public void onInitialize(RefElement refElement) {
  }

  /**
   * Called after the references to the specified element have been collected.
   *
   * @param refElement the element the references to which have been collected.
   */
  public void onReferencesBuild(RefElement refElement){
  }

  /**
   * Called when a reference to the specified element has been found.
   *
   * @param refWhat                        the referenced element.
   * @param refFrom                        the referencing element.
   * @param referencedFromClassInitializer if true, <code>refFrom</code> is a class and the reference
   *                                       has been found in its initializer block.
   */
  public void onMarkReferenced(RefElement refWhat,
                               RefElement refFrom,
                               boolean referencedFromClassInitializer) {
  }

  /**
   * Called when a reference to the specified element has been found.
   *
   * @param refWhat                        the referenced element.
   * @param refFrom                        the referencing element.
   * @param referencedFromClassInitializer if true, <code>refFrom</code> is a class and the reference
   *                                       has been found in its initializer block.
   * @param forReading                     used for reading
   * @param forWriting                     used for writing
   */
  public void onMarkReferenced(RefElement refWhat,
                               RefElement refFrom,
                               boolean referencedFromClassInitializer,
                               boolean forReading,
                               boolean forWriting) {
    onMarkReferenced(refWhat, refFrom, referencedFromClassInitializer);
  }

}
