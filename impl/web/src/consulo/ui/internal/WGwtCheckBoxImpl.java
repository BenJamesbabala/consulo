/*
 * Copyright 2013-2016 must-be.org
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
package consulo.ui.internal;

import com.intellij.openapi.util.Comparing;
import com.intellij.util.SmartList;
import consulo.ui.CheckBox;
import consulo.ui.RequiredUIThread;
import consulo.ui.ValueComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author VISTALL
 * @since 11-Jun-16
 */
public class WGwtCheckBoxImpl extends WBaseGwtComponent implements CheckBox {
  private boolean mySelected;
  private String myText;
  private List<ValueComponent.ValueListener<Boolean>> myValueListeners = new SmartList<ValueComponent.ValueListener<Boolean>>();

  public WGwtCheckBoxImpl(boolean selected, String text) {
    mySelected = selected;
    myText = text;
  }

  @Override
  @NotNull
  public String getText() {
    return myText;
  }

  @Override
  public void setText(@NotNull final String text) {
    if (Comparing.equal(myText, text)) {
      return;
    }

    myText = text;

    markAsChanged();
  }

  @Override
  protected void getState(Map<String, String> map) {
    super.getState(map);

    map.put("text", myText);

    putIfNotDefault("selected", mySelected, true, map);
  }

  @Override
  public void invokeListeners(Map<String, String> variables) {
    mySelected = Boolean.parseBoolean(variables.get("selected"));

    fireValueListeners();
  }

  private void fireValueListeners() {
    ValueEvent<Boolean> event = new ValueEvent<Boolean>(this, getValue());
    for (ValueComponent.ValueListener<Boolean> valueListener : myValueListeners) {
      valueListener.valueChanged(event);
    }
  }

  @NotNull
  @Override
  public Boolean getValue() {
    return mySelected;
  }

  @Override
  @RequiredUIThread
  public void setValue(@Nullable final Boolean value) {
    if (value == null) {
      throw new IllegalArgumentException();
    }

    if (mySelected == value) {
      return;
    }

    mySelected = value;

    fireValueListeners();

    markAsChanged();
  }

  @Override
  public void addValueListener(@NotNull ValueComponent.ValueListener<Boolean> valueListener) {
    myValueListeners.add(valueListener);
  }

  @Override
  public void removeValueListener(@NotNull ValueComponent.ValueListener<Boolean> valueListener) {
    myValueListeners.remove(valueListener);
  }
}
