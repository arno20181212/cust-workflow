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
package org.activiti.designer.property;

import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.alfresco.AlfrescoStartEvent;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PropertyStartEventFilter extends ActivitiPropertyFilter {
	/**
	  * https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fjavadoc%2Forg%2Feclipse%2Fgraphiti%2Fui%2Ffeatures%2Fpackage-summary.html
	  * 
	  * In our example the section should be shown if the selected element represents a EClass. 
	  * Therefore we have to implement a property filter class by 
	  * extending AbstractPropertySectionFilter and overwriting the method accept.
	  */
	@Override
	protected boolean accept(PictogramElement pe) {
		Object bo = getBusinessObject(pe);
		if (bo instanceof StartEvent && bo instanceof AlfrescoStartEvent == false) {
			if (((StartEvent) bo).getEventDefinitions().size() > 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

}
