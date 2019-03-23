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
package org.activiti.designer.features;

import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateStartEventFeature extends AbstractCreateBPMNFeature {

  public static final String FEATURE_ID_KEY = "startevent";

  public CreateStartEventFeature(IFeatureProvider fp) {
    // set name and description of the creation feature
    super(fp, "StartEvent", "Add start event");
  }

  public boolean canCreate(ICreateContext context) {
	/**
	 * 获取拖拽过来的图标，例如startEvent组件拖拽到 EventSubProcess组件中，禁止拖入，图标显示禁止符号
	 */
    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
    if (parentObject instanceof EventSubProcess)
      return false;//startEvent组件拖拽到 EventSubProcess组件中
    return super.canCreate(context);
  }

  public Object[] create(ICreateContext context) {
    StartEvent startEvent = new StartEvent();
    addObjectToContainer(context, startEvent, "Start");

    // return newly created business object(s)
    return new Object[] { startEvent };
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_STARTEVENT_NONE.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
}
