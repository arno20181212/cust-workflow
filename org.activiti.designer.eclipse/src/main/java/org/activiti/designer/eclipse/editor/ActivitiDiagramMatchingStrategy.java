/**
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
package org.activiti.designer.eclipse.editor;

import org.activiti.designer.eclipse.util.FileService;
import org.eclipse.core.resources.IFile;
import org.eclipse.graphiti.ui.editor.DiagramEditorMatchingStrategy;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
/**
 * Eclipse RCP中控制Editor不重复打开的方法
 * 方法一：
 * 在org.eclipse.ui.editors扩展点中有个matchingStrategy的元素，可以实现IEditorMatchingStrategy接口，覆写
 * 
 * public boolean matches(IEditorReference editorRef, IEditorInput input)方法，就可以做到判断编辑器输入是否匹配打开的编辑器。
 * 说的简单点：每次我们打开一个文件，转化为input会去回调matches方法，判断是否文件已经打开，从而达到不会打开多次目的，即单例。一般应用于一个编辑器编辑多个文件
 * 
 * 
 *
 */
public class ActivitiDiagramMatchingStrategy implements IEditorMatchingStrategy {

  @Override
  public boolean matches(final IEditorReference editorRef, final IEditorInput input) {

    try {
      final IFile newDataFile = FileService.getDataFileForInput(input);
      final IFile openEditorDataFile = FileService.getDataFileForInput(editorRef.getEditorInput());

      if (null != newDataFile && newDataFile.equals(openEditorDataFile)) {
        return true;
      }
    } catch (PartInitException exception) {
      exception.printStackTrace();
    }

    /**
     * DiagramEditorMatchingStrategy:
     * 
     * Checks whether any file editor input matches one of the opened editors. 
     * Scenario is a user's double-clicking on a diagram file in the explorer. 
     * If done multiple times on the same file, no new editor must be opened.
     */
    return new DiagramEditorMatchingStrategy().matches(editorRef, input);
  }
}
