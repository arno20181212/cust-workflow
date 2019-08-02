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
package org.activiti.designer.eclipse.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditorInput;
import org.activiti.designer.util.ActivitiConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class FileService {

   /**
   * Returns a temporary file used as diagram file. Conceptually, this is a placeholder used by
   * Graphiti as editor input file. The real data file is found at the given data file path.
   *
   * @param dataFilePath path of the actual BPMN2 model file
   * @param diagramFileTempFolder folder containing the diagram files
   * @return an IFile for the temporary file. If the file exists, it is first
   *         deleted.
   */
  public static IFile getTemporaryDiagramFile(IPath dataFilePath, IFolder diagramFileTempFolder) {

    final IPath path = dataFilePath.removeFileExtension().addFileExtension(
            ActivitiConstants.DIAGRAM_FILE_EXTENSION_RAW);//==>path="/Test/test.bpmn2d"
    final IFile tempFile = diagramFileTempFolder.getFile(path.lastSegment());//path.lastSegment()="test.bpmn2d"
    //tempFile = "L/Test/.biz/test.bpmn2d"
    // We don't need anything from that file and to be sure there are no side
    // effects we delete the file
    if (tempFile.exists()) {
      try {
        tempFile.delete(true, null);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
    return tempFile;//tempFile="L/Test/.biz/test.bpmn2d"
  }

  /**
   * Returns or constructs a temporary folder for diagram files used as Graphiti editor input
   * files. The given path reflects the path where the original data file is located. The folder
   * is constructed in the project root named after the data file extension
   * {@link #DATA_FILE_EXTENSION_RAW}.
   *
   * @param dataFilePath path of the actual BPMN2 model file
   * @return an IFolder for the temporary folder.
   * @throws CoreException in case the folder could not be created.
   */
  public static IFolder getOrCreateTempFolder(IPath dataFilePath) throws CoreException {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    String name = dataFilePath.getFileExtension();
    if (name == null || name.length() == 0) {
      name = "bpmn";
    }
    /// dataFilePath = "Test/test.biz"
    String dir = dataFilePath.segment(0);// =>dir="Test"
    //
    /**
     * getFolder:
     * 
     * Returns a handle to the folder with the given name in this project.
     * This is a resource handle operation; neither the container nor the result need exist in the workspace. 
     * The validation check on the resource name/path is not done when the resource handle is constructed; 
     * rather, it is done automatically as the resource is created.
     * 
     * Parameters:
     * name - the string name of the member folder
     * Returns:
     * the (handle of the) member folder
     */
    IFolder folder = root.getProject(dir).getFolder("." + name);//name='bzi',dir="Test"
    //folder==>"F/Test/.biz";
    if (!folder.exists()) {
      folder.create(true, true, null);
    }
    String[] segments = dataFilePath.segments();//dataFilePath="/Test/test.biz",segments=[Test, test.biz]
    //segments = [Test, test.biz]
    for (int i = 1; i < segments.length - 1; i++) {//一个一个创建文件夹，不能象jdk中的mkdirs一次性创建
      String segment = segments[i];
      folder = folder.getFolder(segment);
      if (!folder.exists()) {
        folder.create(true, true, null);
      }
    }
    return folder;//folder="F/Test/.biz"
  }

  /**
   * Recreates the data file from the given input path. In case the given path reflects a temporary
   * diagram file, it's path is used to recreate the data file, otherwise the given path is simply
   * made absolute and returned.
   *
   * @param inputPath the path to recreate the data file from
   * @return a file object representing the data file
   */
  public static IFile recreateDataFile(final IPath inputPath) {
	  //inputPath="/Test/.biz/test.bpmn2d"
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getFile(inputPath).getProject();//project="P/Test"

   /**
    * IPath	getFullPath():
    * Returns the full, absolute path of this resource relative to the workspace.
    * ======================================
    * int matchingFirstSegments(inputPath):
    * Returns a count of the number of segments which match in this path and the given path (device ids are ignored), 
    * comparing in increasing segment number order.
    * 
    * project.getFullPath()="/Test"
    * project.getFullPath().matchingFirstSegments(inputPath)=1
    * inputPath="/Test/.biz/test.bpmn2d"
    */
    final int matchingSegments = project.getFullPath().matchingFirstSegments(inputPath);
    final int totalSegments = inputPath.segmentCount();//totalSegments=3
    final String extension = inputPath.getFileExtension();

    IFile result = null;

    if (totalSegments > matchingSegments) {
      // it shall be more than just the project

      IPath resultPath = null;

      if (ActivitiConstants.DIAGRAM_FILE_EXTENSION_RAW.equals(extension)) {
        // we got a temporary file here, so rebuild the file of the model from its path
    	/**
    	 * IPath.segment(int index):
    	 * Returns the specified segment of this path, or null if the path does not have such a segment.
    	 */
        String originalExtension = inputPath.segment(matchingSegments);//originalExtension=".biz",matchingSegments=1
        if (originalExtension.startsWith(".")) {
          originalExtension = originalExtension.substring(1);
        }

        final String[] segments = inputPath.segments();//segments=[Test, .biz, test.bpmn2d],inputPath="/Test/.biz/test.bpmn2d"
        IPath originalPath = project.getFullPath();//originalPath="/Test"
        for (int index = matchingSegments + 1; index < segments.length; ++index) {
          originalPath = originalPath.append(segments[index]);
        }//originalPath="/Test/test.bpmn2d"
        
        resultPath = originalPath.removeFileExtension().addFileExtension(originalExtension);//resultPath="/Test/test.biz"
      }
      else {
        resultPath = inputPath.makeAbsolute();
      }

      result = root.getFile(resultPath);
    }

    return result;
  }

  /**
   * Returns the appropriate data file for the given input. The data file is the BPMN file with
   * the file extension {@link #DATA_FILE_EXTENSION_RAW}. This method can handle various different
   * editor input versions:
   *
   * <ul>
   *     <li>the special {@link ActivitiDiagramEditorInput} that directly references the data file</li>
   *     <li>a {@link DiagramEditorInput} that may either point to a data- or diagram file</li>
   *     <li>a {@link FileEditorInput} that points to a data file</li>
   *     <li>a {@link IURIEditorInput} that points to an external file outside Eclipse</li>
   * </ul>
   *
   * @param input the input to handle
   * @return the appropriate data file or <code>null</code> if none could be determined.
   */
  public static IFile getDataFileForInput(final IEditorInput input) {
	  /**
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
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    if (input instanceof ActivitiDiagramEditorInput) {//ActivitiDiagramEditorInput 在DiagramEditorInput上增加了diagramFile（图形文件）和datafile（数据文件）属性
      final ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput) input;
      return adei.getDataFile();
    } else if (input instanceof DiagramEditorInput) {//这是什么情况？
    /**
     * DiagramEditorInput:
	 * The editor input object for IDiagramContainerUIs. 
	 * Wraps the URI of a Diagram and an ID of a diagram type provider for displaying it with a Graphiti diagram editor.
	 * （包装关系图的URI和关系图类型提供程序的ID，以便用图形图编辑器显示它）
	 */
      final DiagramEditorInput dei = (DiagramEditorInput) input;
      /**
       * http://java.sun.com/j2se/1.3/docs/guide/collections/designfaq.html#28 
       * 分层 URI 还要按照下面的语法进行进一步的解析
       * [ scheme :][ // authority][ path][ ? query][ # fragment]
       * 其中， :、 /、 ? 和 # 代表它们自身。分层 URI 的特定于方案的部分包含方案和片段部分之间的字符
       * 
       * trimFragment()应该删除了"#28"字符
       * 
       * 
       * public java.lang.String toPlatformString(boolean decode)
       * If this is a platform URI, as determined by isPlatform(), returns the workspace-relative or plug-in-based path to the resource, 
       * （如果这是平台URI（由is platform（）确定），则返回资源的相对工作区或基于插件的路径。）
       * optionally decoding the segments in the process.
       * 
       * 例：输入的url地址为http://localhost:8080/testproject/test?32fr
       * getRequestURI()返回/testproject/test，为一个String
       * getRequestURL()返回http://localhost:8080/testproject/test，为一个StringBuffer
       * 
       * dei.getUri() = "platform:/resource/Test/.biz/test.bpmn2d#/0"
       * dei.getUri().trimFragment()="platform:/resource/Test/.biz/test.bpmn2d"
       * dei.getUri().trimFragment().toPlatformString(true)="/Test/.biz/test.bpmn2d"
       */
      IPath path = new Path(dei.getUri().trimFragment().toPlatformString(true));

      return recreateDataFile(path);
    } else if (input instanceof FileEditorInput) {
      /**
       * FileEditorInput:
       * 
       * Adapter for making a file resource a suitable input for an editor.
       * This class may be instantiated; it is not intended to be subclassed.
       */
      final FileEditorInput fei = (FileEditorInput) input;

      return fei.getFile();
    } else if (input instanceof IURIEditorInput) {//类似这种：File->openFile..,选择文件打开
    
      /**
       * IURIEditorInput:
       * 
       * This interface defines an editor input based on a URI.
       * Clients implementing this editor input interface should override Object.equals(Object) to answer true for two inputs that are the same. 
       * The IWorkbenchPage.openEditor APIs are dependent on this to find an editor with the same input.
       * Path-oriented editors should support this as a valid input type, and can allow full read-write editing of its content.
       * All editor inputs must implement the IAdaptable interface; extensions are managed by the platform's adapter manager
       */
      // opened externally to Eclipse
      final IURIEditorInput uei = (IURIEditorInput) input;
      final java.net.URI uri = uei.getURI();
      final String path = uri.getPath();

      try {
    	  /**
    	   * root.getProject:
    	   * 
    	   * Returns a handle to the project resource with the given name which is a child of this root. 
    	   * The given name must be a valid path segment as defined by IPath.isValidSegment(String).
    	   * Note: This method deals exclusively with resource handles, independent of whether the resources exist in the workspace. 
    	   * With the exception of validating that the name is a valid path segment, 
    	   * validation checking of the project name is not done when the project handle is constructed; rather, 
    	   * it is done automatically as the project is created.
    	   * 
    	   * 例子：
    	   * The typical usage pattern is that a client creates one of the concrete operations and executes it using the platform operation history. 
    	   * For example, the following snippet deletes the project "Blort" without deleting its contents, and adds it to the operation history so that it can be undone and redone.
    	   * 
    	   * IProject project = getWorkspace().getRoot().getProject("Blort");
    	   * // assume that getMonitor() returns a suitable progress monitor
    	   * project.create(getMonitor());
    	   * project.open(getMonitor());
    	   * DeleteResourcesOperation op = new DeleteResourcesOperation(
    	   *           new IResource[] { project }, "Delete Project Blort", false);
    	   *           PlatformUI.getWorkbench().getOperationSupport()
    	   *           .getOperationHistory().execute(operation, getMonitor(), null);
    	   */
        final IProject importProject = root.getProject("import");//获取import工程
        if (!importProject.exists()) {
        	/**
        	 * void create(IProgressMonitor monitor)
        	 * 
        	 * Parameters:
        	 * monitor - a progress monitor, or null if progress reporting is not desired
        	 */
          importProject.create(null);
        }
        /**
         * void open(IProgressMonitor monitor)
         * 
         * Parameters:
         * monitor - a progress monitor, or null if progress reporting is not desired
         */
        importProject.open(null);

        final InputStream is = new FileInputStream(path);

        final String fileName;
        if (path.contains("/")) {
          fileName = path.substring(path.lastIndexOf("/") + 1);
        } else {
          fileName = path.substring(path.lastIndexOf("\\") + 1);
        }

        IFile importFile = importProject.getFile(fileName);
        if (importFile.exists()) {
          importFile.delete(true, null);
        }

        importFile.create(is, true, null);

        return importProject.getFile(fileName);
      } catch (CoreException exception) {
        exception.printStackTrace();
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    return null;
  }



	public static TransactionalEditingDomain createEmfFileForDiagram(final URI diagramResourceUri//platform:/resource/Test/.biz/test.bpmn2d
	                                                               , final Diagram diagram
	                                                               , final ActivitiDiagramEditor diagramEditor
	                                                               , final InputStream contentStream //null
	                                                               , final IFile resourceFile) {//resourceFile=null

		TransactionalEditingDomain editingDomain = null;
		/**
		 * 一个ResourceSet代表了一个Resource的集合。提供了createResource()，getResource()，以及getEObject()方法。createResource()创建一个新的，空的resource。
		 * getResource()方法也同样创建一个resource，但是会使用给定的URI来装载这个Resource。用户应该始终调用ResourceSet的这两个方法，
		 * 而不是Resource的构造函数或者Resource.Factory的createResource()方法来创建一个Resource。这是因为ResourceSet会保证相同的URI所对应的Resource不会被装载多次，
		 * 而导致内存中有相同的副本，并且，ResourceSet能够自动处理跨文档的引用，而Resource却不行
		 */
		ResourceSet resourceSet = null;

		if (diagramEditor == null || diagramEditor.getDiagramBehavior() == null || 
		    diagramEditor.getDiagramBehavior().getResourceSet() == null || diagramEditor.getEditingDomain() == null) {
		  
		  // nothing found, create a new one
		  resourceSet = new ResourceSetImpl();

		  // try to retrieve an editing domain for this resource set
		  editingDomain = TransactionUtil.getEditingDomain(resourceSet);

		  if (editingDomain == null) {
		    // not existing yet, create a new one
		    editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
		  }
		} else {
		  editingDomain = diagramEditor.getEditingDomain();
		  resourceSet = diagramEditor.getDiagramBehavior().getResourceSet();
		}
		//创建资源,相当于创建一个空的EMF模型格式的文件，EMF中持久化的基本单元叫做资源（resource），它是一个或多个与其内容一起持久化的对象的容器。
		// Create a resource for this file.
		final Resource resource = resourceSet.createResource(diagramResourceUri);//diagramResourceUri = "platform:/resource/Test/.biz/test.bpmn2d"
		/**
		 * RecordingCommand:
		 * 
		 * A partial Command implementation that records the changes made by a subclass's direct manipulation of objects via the metamodel's API. 
		 * This simplifies the programming model for complex commands (not requiring composition of set/add/remove commands) while still providing automatic undo/redo support.
		 * 
		 * Subclasses are simply required to implement the doExecute() method to make the desired changes to the model. Note that, because changes are recorded for automatic undo/redo, 
		 * the concrete command must not make any changes that cannot be recorded by EMF (unless it does not matter that they will not be undone).
		 */
		final CommandStack commandStack = editingDomain.getCommandStack();
		commandStack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				//https://blog.csdn.net/u012521340/article/details/76147176
				/**
				 * 资源可以通过isModified()，来跟踪对其完整内容的任何更改，并且返回自从最近保存和加载后的变更。
				 * 默认情况下，这个特性是无效的，因为它的实现昂贵而且对撤销命令是无效的，由于撤销看起来像变更。适当时候，setTrackingModification() 可以使其生效。
				 */
				resource.setTrackingModification(true);

				if (contentStream == null || resourceFile == null) {
			/**
			 * EMF对象由Resource接口来进行持久化，方法是将对象添加到资源的内容列表中，然后调用save()方法，例子如下：

				PurchaseOrder po = ...
				Resource resource = ...
				resource.getContents().add(po);
				resource.save(null);
				--------------------- 
				原文：https://blog.csdn.net/u012521340/article/details/76147176 
			 */
	        resource.getContents().add(diagram);
				} else {//这是什么情况？
				  try {
				    resourceFile.create(contentStream, true, null);
				  } catch (CoreException exception) {
				    exception.printStackTrace();
				  }
				}

			}
		});

		save(editingDomain, Collections.<Resource, Map<?, ?>> emptyMap());
		return editingDomain;
	}

	private static void save(TransactionalEditingDomain editingDomain, Map<Resource, Map<?, ?>> options) {
		saveInWorkspaceRunnable(editingDomain, options);
	}

	private static void saveInWorkspaceRunnable(final TransactionalEditingDomain editingDomain,
			final Map<Resource, Map<?, ?>> options) {

		final Map<URI, Throwable> failedSaves = new HashMap<URI, Throwable>();
		/**
		 * WorkspaceJob是为修改资源文件增加的扩展，常见的对文件的打开，保存，等等操作一般需要在这个类中执行。与WorkspaceJob对应的是IWorkspaceRunnable。
		 * 
		 * A runnable which executes as a batch operation within the workspace. 
		 * The IWorkspaceRunnable interface should be implemented by any class whose instances are intended to be run by IWorkspace.run
		 */
		final IWorkspaceRunnable wsRunnable = new IWorkspaceRunnable() {
			@Override
			public void run(final IProgressMonitor monitor) throws CoreException {

				final Runnable runnable = new Runnable() {

					@Override
					public void run() {
						//EMF中模型不能直接编辑，需要有事务，支持undo redo（原因可能是资源之间存在相互引用，需要保持一个事物，保存同时成功或者失败）
						Transaction parentTx;
						if (editingDomain != null
								&& (parentTx = ((TransactionalEditingDomainImpl) editingDomain).getActiveTransaction()) != null) {
							do {
								if (!parentTx.isReadOnly()) {
									throw new IllegalStateException(
											"FileService.save() called from within a command (likely produces a deadlock)"); //$NON-NLS-1$
								}
							} while ((parentTx = ((TransactionalEditingDomainImpl) editingDomain)
									.getActiveTransaction().getParent()) != null);//活动的事务，说明其他操作处理中 
						}

						final EList<Resource> resources = editingDomain.getResourceSet().getResources();
						// Copy list to an array to prevent ConcurrentModificationExceptions during the saving of the dirty resources
						//将列表复制到数组，以防止在保存脏资源期间发生并发修改异常
						Resource[] resourcesArray = new Resource[resources.size()];
						resourcesArray = resources.toArray(resourcesArray);
						final Set<Resource> savedResources = new HashSet<Resource>();
						for (final Resource resource : resourcesArray) {
							if (resource.isModified()) {
								try {
									resource.save(options.get(resource));//options={},options.get(resource)=null,save()方法的参数，一个Map，如果指定了保存操作的选项，那么这个参数将非空（non-null）。EMF的XML资源支持的选项详细介绍在15.3.3。
									savedResources.add(resource);//多余的？
								} catch (final Throwable t) {
									failedSaves.put(resource.getURI(), t);
								}
							}
						}
					}
				};

				try {
					editingDomain.runExclusive(runnable);//以独占方式运行,运行完成之前，其他线程或者应用只能等着
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
				editingDomain.getCommandStack().flush();
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wsRunnable, null);
			if (!failedSaves.isEmpty()) {
				throw new WrappedException(createMessage(failedSaves), new RuntimeException());
			}
		} catch (final CoreException e) {
			final Throwable cause = e.getStatus().getException();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(e);
		}
	}

	private static String createMessage(Map<URI, Throwable> failedSaves) {
		final StringBuilder buf = new StringBuilder("The following resources could not be saved:");
		for (final Entry<URI, Throwable> entry : failedSaves.entrySet()) {
			buf.append("\nURI: ").append(entry.getKey().toString()).append(", cause: \n")
					.append(getExceptionAsString(entry.getValue()));
		}
		return buf.toString();
	}

	private static String getExceptionAsString(Throwable t) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		final String result = stringWriter.toString();
		try {
			stringWriter.close();
		} catch (final IOException e) {
			// $JL-EXC$ ignore
		}
		printWriter.close();
		return result;
	}
}
