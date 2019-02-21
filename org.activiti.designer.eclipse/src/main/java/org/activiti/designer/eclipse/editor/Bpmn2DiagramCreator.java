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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.activiti.designer.eclipse.util.FileService;
import org.activiti.designer.util.ActivitiConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class Bpmn2DiagramCreator {
  
  public ActivitiDiagramEditorInput createBpmnDiagram(final IFile dataFile, final IFile diagramFile, final ActivitiDiagramEditor diagramEditor,
          final String templateContent, final boolean openEditor) {
    
    IFile finalDataFile = dataFile;//dataFile="L/Test/test.biz",diagramFile="L/Test/.biz/test.bpmn2d",templateContent=null,openEditor=false

    final IPath diagramPath = diagramFile.getFullPath();//diagramPath="/Test/.biz/test.bpmn2d";
    final String diagramName = diagramPath.removeFileExtension().lastSegment();//diagramPath.removeFileExtension().lastSegment()="test";diagramPath.removeFileExtension()="/Test/.biz/test"
    final URI uri = URI.createPlatformResourceURI(diagramPath.toString(), true);//diagramPath.toString()="/Test/.biz/test.bpmn2d";
    //uri ="platform:/resource/Test/.biz/test.bpmn2d";
    if (templateContent != null) {
      // there is a template to use
      final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templateContent);
      finalDataFile = FileService.recreateDataFile(new Path(uri.trimFragment().toPlatformString(true)));
      final String filePath = finalDataFile.getLocationURI().getRawPath().replaceAll("%20", " ");

      OutputStream os = null;
      try {
        os = new FileOutputStream(filePath);

        byte[] buffer = new byte[1024];
        int len = is.read(buffer);
        while (len > 0) {
          os.write(buffer, 0, len);

          len = is.read(buffer);
        }

      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      } catch (IOException exception) {
        exception.printStackTrace();
      } finally {
        if (os != null) {
          try {
            os.close();
          } catch (IOException exception) {
            exception.printStackTrace();
          }
        }
      }

      try {
        is.close();
      } catch (IOException exception) { 
        exception.printStackTrace();
      }
    }
    //Diagram diagram = Graphiti.getPeCreateService().createDiagram(diagramTypeId, diagramName, true);
    final Diagram diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", diagramName, true);//diagramName="test"

    FileService.createEmfFileForDiagram(uri, diagram, diagramEditor, null, null);
    /**
     *getDiagramTypeProviderId:
     * 
     *Gets the diagram type provider id.
     *
     *Returns:
     *provider id of the diagram type providers which can handle the given diagram type id. 
     *If more then one diagram type providers available, the first one will be returned.
     */
    final String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());//providerId="org.activiti.designer.diagram.ActivitiBPMNDiagramTypeProvider"
    final ActivitiDiagramEditorInput result = new ActivitiDiagramEditorInput(EcoreUtil.getURI(diagram), providerId);//EcoreUtil.getURI(diagram)="platform:/resource/Test/.biz/test.bpmn2d#/"
    result.setDataFile(finalDataFile);
    result.setDiagramFile(diagramFile);

    if (openEditor) {
      openEditor(result);
    }

    return result;
  }

  public void openEditor(final ActivitiDiagramEditorInput editorInput) {
    final IWorkbench workbench = PlatformUI.getWorkbench();

    workbench.getDisplay().syncExec(new Runnable() {

      @Override
      public void run() {
        try {
        	/**
        	 * 当我们在知道文件位置的时候. 没有明确Input的时候.我们可以获取IFile对象来打开Editor.  但是想要获取的文件必须在项目里面, 这样才能获取IFile对象
        	 * -----------code example1-------------------
        	 *
        	 * //获取工作控件, 获取根, 获取项目.
        	 * IProject porject = ResourcesPlugin.getWorkspace().getRoot().getProjects()[0];
        	 * //因为我只有一个项目, 所以没有循环遍历. 直接获取的第1个对象. 
        	 * //在很明确项目名字的时候. 也可以使用getProject(name), 方法来获取项目. 同样可以获取IProject对象.
        	 * //传入项目名, 获取IFile对象.
        	 * IFile ifile = porject.getFile("src/com/test/www.xml");
        	 * IDE.openEditor(page, ifile, SQLEditor.ID);	//打开编辑器.
        	 * 
        	 * ------------------------------
        	 * 可以使用IDE.openEditor方法来打开编辑器. 此方法有很多重载方式. 甚至可以打开外部文件. 这里的外部文件是指eclipse工作空间外部的文件.
        	 * 
        	 * 
        	 * 我们在明确有Input的时候, 可以使用这种方式:
        	 * -----------code example2-------------------
        	 * IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); //打开一个空白页.  
        	 * TestEditorInput input = new TestEditorInput("测试编辑器");
        	 * try {  
        	 *     page.openEditor(input, TestEditor.ID);  
        	 *     } catch (PartInitException e1) {  
        	 *         e1.printStackTrace();  
        	 *         }
        	 * ------------------------------
        	 */
          IDE.openEditor(workbench.getActiveWorkbenchWindow().getActivePage(), editorInput, ActivitiConstants.DIAGRAM_EDITOR_ID);

        } catch (PartInitException exception) {
          exception.printStackTrace();
        }
      }
    });
  }

  /**
   * Delete the temporary diagram file. If the containing folder hierarchy is
   * empty, it will also be deleted.
   * 
   * @param file
   *          - the temporary diagram file.
   */
  public static void dispose(IFile file) {
    try {
      IContainer container = file.getParent();
      file.delete(true, null);
      while (isEmptyFolder(container)) {
        container.delete(true, null);
        container = container.getParent();
      }
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Check if the given folder is empty. This is true if it contains no files,
   * or only empty folders.
   * 
   * @param container
   *          - folder to check
   * @return true if the folder is empty.
   */
  public static boolean isEmptyFolder(IContainer container) {
    try {
      IResource[] members = container.members();
      for (IResource res : members) {
        int type = res.getType();
        if (type == IResource.FILE || type == IResource.PROJECT || type == IResource.ROOT) {
          return false;
        }
        if (!isEmptyFolder((IContainer) res)) {
          return false;
        }
      }
    } catch (CoreException e) {
      return false;
    }
    return true;
  }

}
