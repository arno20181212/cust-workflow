import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import junit.framework.TestCase;

public class SampleViewTest extends TestCase {
 
    public SampleViewTest(String name) throws Exception {
    	final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//    	IProject importProject = root.getProject("import");//获取import工程
//    	if (!importProject.exists()) {
//        	//System.out.println("not exist");
//        }
    }
 
}
