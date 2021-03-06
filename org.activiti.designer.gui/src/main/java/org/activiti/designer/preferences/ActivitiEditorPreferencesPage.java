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
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * FieldEditorPreferencePage.createContent主要執行了以下三個操作：
 * 調用虛函數createFieldEditors(), 此方法在FavoritesPreferencePage必須實現,用於創建FieldEditor
 *
 */
public class ActivitiEditorPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ActivitiEditorPreferencesPage() {
		super(GRID);
	}
	
	public void createFieldEditors() {
		/**
		 * BooleanFieldEditor:PreferencePage上的checkbox对象
		 */
		addField(new BooleanFieldEditor(Preferences.EDITOR_ADD_LABELS_TO_NEW_SEQUENCEFLOWS.getPreferenceId(),
				"&Automatically create a label when adding a new sequence flow", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS.getPreferenceId(),
				"&Create default diagram content when creating new diagrams and subprocesses", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefStore);
		setDescription("Set preferences used while editing Activiti Diagrams");
		setTitle("Activiti Designer Editor Preferences");
	}
}
