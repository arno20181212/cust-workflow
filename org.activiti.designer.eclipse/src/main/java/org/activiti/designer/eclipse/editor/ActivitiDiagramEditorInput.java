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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
/**
 * public class DiagramEditorInput
 * extends Object
 * implements IDiagramEditorInput
 * The editor input object for IDiagramContainerUIs. Wraps the URI of a Diagram and an ID of a diagram type provider for displaying it with a Graphiti diagram editor.
 * @author Administrator
 */


/**
 * IEditorInput:
 * 
 * IEditorInput is a light weight descriptor of editor input, like a file name but more abstract. It is not a model. 
 * It is a description of the model source for an IEditorPart.
 * Clients implementing this editor input interface should override Object.equals(Object) to answer true for two inputs that are the same. 
 * The IWorbenchPage.openEditor APIs are dependent on this to find an editor with the same input.
 * 
 * Clients should extend this interface to declare new types of editor inputs.
 * An editor input is passed to an editor via the IEditorPart.init method. Due to the wide range of valid editor inputs, 
 * it is not possible to define generic methods for getting and setting bytes.
 * 
 * Editor input must implement the IAdaptable interface; extensions are managed by the platform's adapter manager.
 * 
 * Please note that it is important that the editor input be light weight. Within the workbench, 
 * the navigation history tends to hold on to editor inputs as a means of reconstructing the editor at a later time. 
 * The navigation history can hold on to quite a few inputs (i.e., the default is fifty). 
 * The actual data model should probably not be held in the input.
 */
public class ActivitiDiagramEditorInput extends DiagramEditorInput {

  private IFile diagramFile;
  private IFile dataFile;

  public ActivitiDiagramEditorInput(URI diagramUri, String providerId) {
    super(diagramUri, providerId);
  }

  public IFile getDiagramFile() {
    return diagramFile;
  }

  public void setDiagramFile(IFile diagramFileName) {
    this.diagramFile = diagramFileName;
  }

  public IFile getDataFile() {
    return dataFile;
  }

  public void setDataFile(IFile dataFileName) {
    this.dataFile = dataFileName;
  }
  
/**
 * equals(Object obj) 
 *    Checks if this instance of the input represent the same object as the given instance.
 */
  @Override
  public boolean equals(Object obj) {
    boolean result = false;

    if (obj == null) {
      return result;
    }

    if (obj instanceof ActivitiDiagramEditorInput) {
      final ActivitiDiagramEditorInput otherInput = (ActivitiDiagramEditorInput) obj;
      
      /**
       * boolean equals(Object other):
       * 
       * Compares two objects for equality; for resources, equality is defined in terms of their handles: 
       * same resource type, equal full paths, and identical workspaces
       * 
       */
      if (diagramFile.equals(otherInput.diagramFile)) {
        result = true;
      }
    }

    return result;
  }
}
