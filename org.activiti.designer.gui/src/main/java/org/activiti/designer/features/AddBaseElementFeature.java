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

import org.activiti.designer.controller.BusinessObjectShapeController;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Base feature to extend for adding a new BPMN element to the diagram.
 * 
 * @author Tijs Rademakers
 */
public class AddBaseElementFeature extends AbstractAddShapeFeature {
/**
 * (1) 创建add feature类

                       ①  实现接口IAddFeature，或者继承其抽象类AbstractAddShapeFeature。实现两个方法canAdd（判断给出的内容能否添加）
                               和add（创建图形结构，确立与业务对象的联接）

                      ②   Add方法中创建绘图算法（graphics algorithm）（设置图形外观），并放到合适的位置（从给出的内容获取）。
                                创建将被添加的对象与存放入它的容器的联接。

 * @param fp
 */
  public AddBaseElementFeature(ActivitiBPMNFeatureProvider fp) {
    super(fp);
  }

  /**
   * 判断给出的内容能否添加
   */
  @Override
  public boolean canAdd(IAddContext context) {
    return getBpmnFeatureProvider().hasShapeController(context.getNewObject());
  }
  
  /**
   * Add方法中创建绘图算法（graphics algorithm）（设置图形外观），并放到合适的位置（从给出的内容获取）。
   * 创建将被添加的对象与存放入它的容器的联接。
   */
  @Override
  public PictogramElement add(IAddContext context) {
    final ContainerShape parent = context.getTargetContainer();
    
    // Get the controller, capable of creating a new shape for the business-object
    BusinessObjectShapeController shapeController = getBpmnFeatureProvider()
        .getShapeController(context.getNewObject());
   
    /**
     * IAddContext:
     * Object getNewObject()
     * A pictogram element has to be added. This pictogram element has to link to a domain model element.
     * Returns:
     *   instance of a domain model element
     *   
     */
    
    // Request a new shape from the controller
    final PictogramElement containerShape = shapeController.createShape(context.getNewObject(), 
        parent, context.getWidth(), context.getHeight(), context);
        
    // Create link between shape and business object
    link(containerShape, context.getNewObject());
    //在给定的pictogramelement布局
    layoutPictogramElement(containerShape);
    
    return containerShape;
  }

  protected ActivitiBPMNFeatureProvider getBpmnFeatureProvider() {
    return (ActivitiBPMNFeatureProvider) getFeatureProvider();
  }
}
