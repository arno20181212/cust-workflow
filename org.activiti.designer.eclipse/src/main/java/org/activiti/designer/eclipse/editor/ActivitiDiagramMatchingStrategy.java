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
 * Eclipse RCP�п���Editor���ظ��򿪵ķ���
 * ����һ��
 * ��org.eclipse.ui.editors��չ�����и�matchingStrategy��Ԫ�أ�����ʵ��IEditorMatchingStrategy�ӿڣ���д
 * 
 * public boolean matches(IEditorReference editorRef, IEditorInput input)�������Ϳ��������жϱ༭�������Ƿ�ƥ��򿪵ı༭����
 * ˵�ļ򵥵㣺ÿ�����Ǵ�һ���ļ���ת��Ϊinput��ȥ�ص�matches�������ж��Ƿ��ļ��Ѿ��򿪣��Ӷ��ﵽ����򿪶��Ŀ�ģ���������һ��Ӧ����һ���༭���༭����ļ�
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
