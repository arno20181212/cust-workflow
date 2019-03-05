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
package org.activiti.designer.preferences;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * 页面继承了FieldEditorPreferencePage，只需要实现createFieldEditors()
 * 即可构建自己的Preference结构；实现了IWorkbenchPreferencePage接口，
 * 这样eclipse才能够通过plugin.xml中定义的extension point="org.eclipse.ui.preferencePages"加载本页。
 * @author
 *
 */
public class ActivitiPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiPreferencePage() {
		super(GRID);
	}

	public void createFieldEditors() {
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
	}
}
