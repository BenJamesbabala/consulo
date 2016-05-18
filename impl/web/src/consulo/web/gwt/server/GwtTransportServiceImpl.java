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
package consulo.web.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.IdentifierHighlighterPassFactory;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import consulo.web.gwt.client.transport.GwtColor;
import consulo.web.gwt.client.transport.GwtHighlightInfo;
import consulo.web.gwt.client.transport.GwtTextRange;
import consulo.web.gwt.client.transport.GwtVirtualFile;
import consulo.web.gwt.shared.GwtTransportService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 15-May-16
 */
public class GwtTransportServiceImpl extends RemoteServiceServlet implements GwtTransportService {

  private Project getProject() {
    String path = "R:/_github.com/consulo/cold";

    try {
      final Project project;
      ProjectManagerEx projectManager = ProjectManagerEx.getInstanceEx();
      Project[] openProjects = projectManager.getOpenProjects();
      if (openProjects.length > 0) {
        project = openProjects[0];
      }
      else {
        project = projectManager.loadProject(path);
        projectManager.openTestProject(project);
        final StartupManagerImpl startupManager = (StartupManagerImpl)StartupManager.getInstance(project);
        startupManager.runStartupActivities();
        startupManager.startCacheUpdate();
      }
      return project;
    }
    catch (Exception e) {
      e.getMessage();
    }
    return null;
  }

  @Override
  public GwtVirtualFile getProjectDirectory() {
    return GwtVirtualFileUtil.createVirtualFile(getProject(), getProject().getBaseDir());
  }

  @Override
  public String getContent(final String fileUrl) {
    final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
    if (fileByUrl != null) {
      if (fileByUrl.isDirectory() || fileByUrl.getFileType().isBinary()) {
        return null;
      }
      return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
        @Override
        public String compute() {
          PsiFile file = PsiManager.getInstance(getProject()).findFile(fileByUrl);

          Document document = PsiDocumentManager.getInstance(getProject()).getDocument(file);
          return document.getText();
        }
      });
    }
    return null;
  }

  @Override
  public List<GwtHighlightInfo> getLexerHighlight(String fileUrl) {
    final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
    if (fileByUrl != null) {
      if (fileByUrl.isDirectory() || fileByUrl.getFileType().isBinary()) {
        return null;
      }
      return ApplicationManager.getApplication().runReadAction(new Computable<List<GwtHighlightInfo>>() {
        @Override
        public List<GwtHighlightInfo> compute() {
          List<GwtHighlightInfo> list = new ArrayList<GwtHighlightInfo>();
          EditorHighlighter highlighter = HighlighterFactory.createHighlighter(getProject(), fileByUrl);
          PsiFile file = PsiManager.getInstance(getProject()).findFile(fileByUrl);
          highlighter.setText(file.getText());

          HighlighterIterator iterator = highlighter.createIterator(0);
          while (!iterator.atEnd()) {
            int start = iterator.getStart();
            int end = iterator.getEnd();
            TextAttributes textAttributes = iterator.getTextAttributes();

            GwtHighlightInfo highlightInfo = createHighlightInfo(textAttributes, new GwtTextRange(start, end));
            if (!highlightInfo.isEmpty()) {
              list.add(highlightInfo);
            }
            iterator.advance();
          }
          return list;
        }
      });
    }

    return Collections.emptyList();
  }


  public static GwtHighlightInfo createHighlightInfo(TextAttributes textAttributes, GwtTextRange textRange) {
    GwtColor foreground = null;
    GwtColor background = null;
    boolean bold = false;
    boolean italic = false;
    GwtTextRange myTextRange = null;

    Color foregroundColor = textAttributes.getForegroundColor();
    if (foregroundColor != null) {
      foreground = new GwtColor(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
    }

    Color backgroundColor = textAttributes.getBackgroundColor();
    if (backgroundColor != null) {
      background = new GwtColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
    }

    if ((textAttributes.getFontType() & Font.BOLD) != 0) {
      bold = true;
    }
    if ((textAttributes.getFontType() & Font.ITALIC) != 0) {
      italic = true;
    }
    myTextRange = textRange;
    return new GwtHighlightInfo(foreground, background, bold, italic, myTextRange);
  }

  @Override
  public List<GwtHighlightInfo> runHighlightPasses(String fileUrl, final int offset) {
    final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
    if (fileByUrl != null) {
      if (fileByUrl.isDirectory() || fileByUrl.getFileType().isBinary()) {
        return null;
      }
      IdentifierHighlighterPassFactory.ourTestingIdentifierHighlighting = true;
      return ApplicationManager.getApplication().runReadAction(new Computable<List<GwtHighlightInfo>>() {
        @Override
        public List<GwtHighlightInfo> compute() {
          final List<GwtHighlightInfo> list = new ArrayList<GwtHighlightInfo>();
          final Project project = getProject();
          final PsiFile file = PsiManager.getInstance(project).findFile(fileByUrl);

          try {

            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, fileByUrl, 0), false);
                if(offset != -1) {
                  editor.getCaretModel().moveToOffset(offset);
                }
                DaemonCodeAnalyzerImpl analyzer = (DaemonCodeAnalyzerImpl)DaemonCodeAnalyzerEx.getInstanceEx(project);
                TextEditor textEditor = TextEditorProvider.getInstance().getTextEditor(editor);
                List<HighlightInfo> highlightInfos =
                        analyzer.runPasses(file, PsiDocumentManager.getInstance(project).getDocument(file), textEditor, new int[0], false, null);

                for (HighlightInfo highlightInfo : highlightInfos) {
                  TextAttributes textAttributes = highlightInfo.getTextAttributes(null, null);
                  if (textAttributes == null) {
                    continue;
                  }
                  GwtHighlightInfo info = createHighlightInfo(textAttributes, new GwtTextRange(highlightInfo.getStartOffset(), highlightInfo.getEndOffset()));
                  list.add(info);
                }
              }
            });
          }
          catch (Exception e) {
            e.printStackTrace();
          }

          return list;
        }
      });
    }
    return null;
  }
}
