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
package org.activiti.designer.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.TextAnnotation;
import org.activiti.designer.command.AssociationModelUpdater;
import org.activiti.designer.command.BoundaryEventModelUpdater;
import org.activiti.designer.command.BpmnProcessModelUpdater;
import org.activiti.designer.command.BusinessRuleTaskModelUpdater;
import org.activiti.designer.command.CallActivityModelUpdater;
import org.activiti.designer.command.EndEventModelUpdater;
import org.activiti.designer.command.GatewayModelUpdater;
import org.activiti.designer.command.IntermediateCatchEventModelUpdater;
import org.activiti.designer.command.LaneModelUpdater;
import org.activiti.designer.command.ManualTaskModelUpdater;
import org.activiti.designer.command.MessageFlowModelUpdater;
import org.activiti.designer.command.PoolModelUpdater;
import org.activiti.designer.command.ProcessModelUpdater;
import org.activiti.designer.command.ReceiveTaskModelUpdater;
import org.activiti.designer.command.ScriptTaskModelUpdater;
import org.activiti.designer.command.SendTaskModelUpdater;
import org.activiti.designer.command.SequenceFlowModelUpdater;
import org.activiti.designer.command.ServiceTaskModelUpdater;
import org.activiti.designer.command.StartEventModelUpdater;
import org.activiti.designer.command.SubProcessModelUpdater;
import org.activiti.designer.command.TextAnnotationModelUpdater;
import org.activiti.designer.command.ThrowEventModelUpdater;
import org.activiti.designer.command.UserTaskModelUpdater;
import org.activiti.designer.controller.AssociationShapeController;
import org.activiti.designer.controller.BoundaryEventShapeController;
import org.activiti.designer.controller.BusinessObjectShapeController;
import org.activiti.designer.controller.CallActivityShapeController;
import org.activiti.designer.controller.CatchEventShapeController;
import org.activiti.designer.controller.EventBasedGatewayShapeController;
import org.activiti.designer.controller.EventShapeController;
import org.activiti.designer.controller.EventSubProcessShapeController;
import org.activiti.designer.controller.ExclusiveGatewayShapeController;
import org.activiti.designer.controller.InclusiveGatewayShapeController;
import org.activiti.designer.controller.LaneShapeController;
import org.activiti.designer.controller.MessageFlowShapeController;
import org.activiti.designer.controller.ParallelGatewayShapeController;
import org.activiti.designer.controller.PoolShapeController;
import org.activiti.designer.controller.SequenceFlowShapeController;
import org.activiti.designer.controller.SubProcessShapeController;
import org.activiti.designer.controller.TaskShapeController;
import org.activiti.designer.controller.TextAnnotationShapeController;
import org.activiti.designer.controller.ThrowEventShapeController;
import org.activiti.designer.controller.TransactionShapeController;
import org.activiti.designer.features.ActivityResizeFeature;
import org.activiti.designer.features.AddBaseElementFeature;
import org.activiti.designer.features.ChangeElementTypeFeature;
import org.activiti.designer.features.ContainerResizeFeature;
import org.activiti.designer.features.CopyFlowElementFeature;
import org.activiti.designer.features.CreateAssociationFeature;
import org.activiti.designer.features.CreateBoundaryCancelFeature;
import org.activiti.designer.features.CreateBoundaryCompensateFeature;
import org.activiti.designer.features.CreateBoundaryErrorFeature;
import org.activiti.designer.features.CreateBoundaryMessageFeature;
import org.activiti.designer.features.CreateBoundarySignalFeature;
import org.activiti.designer.features.CreateBoundaryTimerFeature;
import org.activiti.designer.features.CreateBusinessRuleTaskFeature;
import org.activiti.designer.features.CreateCallActivityFeature;
import org.activiti.designer.features.CreateCancelEndEventFeature;
import org.activiti.designer.features.CreateCompensationThrowingEventFeature;
import org.activiti.designer.features.CreateEmbeddedSubProcessFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateErrorEndEventFeature;
import org.activiti.designer.features.CreateErrorStartEventFeature;
import org.activiti.designer.features.CreateEventGatewayFeature;
import org.activiti.designer.features.CreateEventSubProcessFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateInclusiveGatewayFeature;
import org.activiti.designer.features.CreateLaneFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateMessageCatchingEventFeature;
import org.activiti.designer.features.CreateMessageFlowFeature;
import org.activiti.designer.features.CreateMessageStartEventFeature;
import org.activiti.designer.features.CreateNoneThrowingEventFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreatePoolFeature;
import org.activiti.designer.features.CreateReceiveTaskFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateSequenceFlowFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateSignalCatchingEventFeature;
import org.activiti.designer.features.CreateSignalStartEventFeature;
import org.activiti.designer.features.CreateSignalThrowingEventFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateTerminateEndEventFeature;
import org.activiti.designer.features.CreateTextAnnotationFeature;
import org.activiti.designer.features.CreateTimerCatchingEventFeature;
import org.activiti.designer.features.CreateTimerStartEventFeature;
import org.activiti.designer.features.CreateTransactionFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteArtifactFeature;
import org.activiti.designer.features.DeleteFlowElementFeature;
import org.activiti.designer.features.DeleteLaneFeature;
import org.activiti.designer.features.DeleteMessageFlowFeature;
import org.activiti.designer.features.DeletePoolFeature;
import org.activiti.designer.features.DirectEditFlowElementFeature;
import org.activiti.designer.features.DirectEditTextAnnotationFeature;
import org.activiti.designer.features.LayoutTextAnnotationFeature;
import org.activiti.designer.features.MoveActivityFeature;
import org.activiti.designer.features.MoveBoundaryEventFeature;
import org.activiti.designer.features.MoveEventFeature;
import org.activiti.designer.features.MoveGatewayFeature;
import org.activiti.designer.features.MoveLaneFeature;
import org.activiti.designer.features.MovePoolFeature;
import org.activiti.designer.features.MoveTextAnnotationFeature;
import org.activiti.designer.features.PasteFlowElementFeature;
import org.activiti.designer.features.ReconnectSequenceFlowFeature;
import org.activiti.designer.features.UpdateFlowElementFeature;
import org.activiti.designer.features.UpdateMessageFlowFeature;
import org.activiti.designer.features.UpdatePoolAndLaneFeature;
import org.activiti.designer.features.UpdateTextAnnotationFeature;
import org.activiti.designer.util.editor.BpmnIndependenceSolver;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import com.alfresco.designer.gui.controller.AlfrescoStartEventShapeController;
import com.alfresco.designer.gui.controller.AlfrescoTaskShapeController;
import com.alfresco.designer.gui.features.CreateAlfrescoMailTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoScriptTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoStartEventFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoUserTaskFeature;
/**
 * graphiti笔记1
 * https://blog.csdn.net/andywangcn/article/details/7905742
 * 
 * graphiti笔记2
 * https://blog.csdn.net/andywangcn/article/details/7943720?locationNum=2&fps=1
 *
 */
public class ActivitiBPMNFeatureProvider extends DefaultFeatureProvider {

  protected List<BusinessObjectShapeController> shapeControllers;
  protected List<BpmnProcessModelUpdater> modelUpdaters;

  public ActivitiBPMNFeatureProvider(IDiagramTypeProvider dtp) {
    super(dtp);
    /**
     * Sets the independence solver
     */
    setIndependenceSolver(new BpmnIndependenceSolver(dtp));
    
    this.shapeControllers = new ArrayList<BusinessObjectShapeController>();
    shapeControllers.add(new EventShapeController(this));
    shapeControllers.add(new TaskShapeController(this));
    shapeControllers.add(new ExclusiveGatewayShapeController(this));
    shapeControllers.add(new EventBasedGatewayShapeController(this));
    shapeControllers.add(new InclusiveGatewayShapeController(this));
    shapeControllers.add(new ParallelGatewayShapeController(this));
    shapeControllers.add(new CatchEventShapeController(this));
    shapeControllers.add(new ThrowEventShapeController(this));
    shapeControllers.add(new SubProcessShapeController(this));
    shapeControllers.add(new CallActivityShapeController(this));
    shapeControllers.add(new EventSubProcessShapeController(this));
    shapeControllers.add(new TransactionShapeController(this));
    shapeControllers.add(new BoundaryEventShapeController(this));
    shapeControllers.add(new PoolShapeController(this));
    shapeControllers.add(new LaneShapeController(this));
    shapeControllers.add(new TextAnnotationShapeController(this));
    shapeControllers.add(new SequenceFlowShapeController(this));
    shapeControllers.add(new MessageFlowShapeController(this));
    shapeControllers.add(new AssociationShapeController(this));
    shapeControllers.add(new AlfrescoStartEventShapeController(this));
    shapeControllers.add(new AlfrescoTaskShapeController(this));
    
    this.modelUpdaters = new ArrayList<BpmnProcessModelUpdater>();
    modelUpdaters.add(new StartEventModelUpdater(this));
    modelUpdaters.add(new EndEventModelUpdater(this));
    modelUpdaters.add(new UserTaskModelUpdater(this));
    modelUpdaters.add(new ServiceTaskModelUpdater(this));
    modelUpdaters.add(new ScriptTaskModelUpdater(this));
    modelUpdaters.add(new ReceiveTaskModelUpdater(this));
    modelUpdaters.add(new BusinessRuleTaskModelUpdater(this));
    modelUpdaters.add(new SendTaskModelUpdater(this));
    modelUpdaters.add(new ManualTaskModelUpdater(this));
    modelUpdaters.add(new GatewayModelUpdater(this));
    modelUpdaters.add(new IntermediateCatchEventModelUpdater(this));
    modelUpdaters.add(new ThrowEventModelUpdater(this));
    modelUpdaters.add(new CallActivityModelUpdater(this));
    modelUpdaters.add(new SubProcessModelUpdater(this));
    modelUpdaters.add(new BoundaryEventModelUpdater(this));
    modelUpdaters.add(new PoolModelUpdater(this));
    modelUpdaters.add(new LaneModelUpdater(this));
    modelUpdaters.add(new TextAnnotationModelUpdater(this));
    modelUpdaters.add(new ProcessModelUpdater(this));
    modelUpdaters.add(new SequenceFlowModelUpdater(this));
    modelUpdaters.add(new MessageFlowModelUpdater(this));
    modelUpdaters.add(new AssociationModelUpdater(this));
  }
  
  /**
   * @param businessObject object to get a {@link BusinessObjectShapeController} for
   * @return a {@link BusinessObjectShapeControllr} capable of creating/updating shapes
   * of for the given businessObject.
   * @throws IllegalArgumentException When no controller can be found for the given object.
   */
  public BusinessObjectShapeController getShapeController(Object businessObject) {
    for (BusinessObjectShapeController controller : shapeControllers) {
      if (controller.canControlShapeFor(businessObject)) {
        return controller;
      }
    }
    throw new IllegalArgumentException("No controller can be found for object: " + businessObject);
  }
  
  /**
   * @return true, if a {@link BusinessObjectShapeController} is available for the given business object.
   */
  public boolean hasShapeController(Object businessObject) {
    for (BusinessObjectShapeController controller : shapeControllers) {
        if (controller.canControlShapeFor(businessObject)) {
          return true;
        }
      }
    return false;
  }
  
  /**
   * @param businessObject the business-object to update
   * @param pictogramElement optional pictogram-element to refresh after update is performed. When null
   * is provided, no additional update besides the actual model update is done.
   * @return the updater capable of updating the given object. Null, if the object cannot be updated.
   */
  public BpmnProcessModelUpdater getModelUpdaterFor(Object businessObject, PictogramElement pictogramElement) {
    for (BpmnProcessModelUpdater updater : modelUpdaters) {
      if (updater.canControlShapeFor(businessObject)) {
        // creates a new BpmnProcessModelUpdater instances for undo/redo stack
        BpmnProcessModelUpdater updaterObject = updater.init(businessObject, pictogramElement);
        return updaterObject;
      }
    }
    throw new IllegalArgumentException("No updater can be found for object: " + businessObject);
  }

  /**
   * 1.2 提供 Add 功能（Functionality）
   * 为业务对象（领域模型对象）创建图形表达（图符元素。
   * 
   * (1) 创建add feature类（e.g:AddBaseElementFeature）
   * 
   * ①      实现接口IAddFeature，或者继承其抽象类(AbstractAddShapeFeature)。实现两个方法canAdd（判断给出的内容能否添加）和add（创建图形结构，确立与业务对象的联接）
   * ②      Add方法中创建绘图算法（graphics algorithm）（设置图形外观），并放到合适的位置（从给出的内容获取）。创建将被添加的对象与存放入它的容器的联接。
   * (2) 覆写FeatureProvider中的getAddFeature()方法，实现add feature的发布
   */
  @Override
  public IAddFeature getAddFeature(IAddContext context) {
    return new AddBaseElementFeature(this);
  }

  /**
   * 1.3      提供Create Feature
   * 创建一个业务对象和相应的图符元素。通常创建业务对象（business object），然后调用Add Feature去创建相应的图符元素(graphics object),并在add feature中建立business object和graphics object之间的联系。创建CreateFeature之后，
   * 框架会自动集成到平台UI（platform’s UI）。
   * (1)     实现接口ICreateFeature,或继承抽象类AbstractCreateFeature. 覆写canCreate和create（创建业务对象，
   * 并为其添加图形表达）.
   * 
   * (2)     覆写FeatureProvider类的getCreateFeatures()方法发布create feature。
   * 
   * 父图形（container）的位置和大小的改变可以由graphiti框架的默认功能更新，但是子图形的更新必须开发者自己实现。
   * 
   * IPeService：为图符元素的创建和布局提供服务（例如形状shape、线条connection），扩展自IPeCreateService, IPeLayoutService。
   * 
   * IGaService：为绘图算法（graphics algorithm，外观）的创建和布局提供服务。
   */
  @Override
  public ICreateFeature[] getCreateFeatures() {//ActivitiToolBehaviorProvider.getPalette()执行时会调用这个函数创建组件
    return new ICreateFeature[] { new CreateAlfrescoStartEventFeature(this), new CreateStartEventFeature(this), new CreateTimerStartEventFeature(this),
        new CreateMessageStartEventFeature(this), new CreateErrorStartEventFeature(this), new CreateSignalStartEventFeature(this), new CreateEndEventFeature(this),
        new CreateErrorEndEventFeature(this), new CreateTerminateEndEventFeature(this), new CreateCancelEndEventFeature(this), new CreateUserTaskFeature(this),
        new CreateAlfrescoUserTaskFeature(this), new CreateScriptTaskFeature(this), new CreateServiceTaskFeature(this), new CreateMailTaskFeature(this),
        new CreateManualTaskFeature(this), new CreateReceiveTaskFeature(this), new CreateBusinessRuleTaskFeature(this), 
        new CreateParallelGatewayFeature(this), new CreateExclusiveGatewayFeature(this), new CreateInclusiveGatewayFeature(this), new CreateEventGatewayFeature(this),
        new CreateBoundaryTimerFeature(this), new CreateBoundaryErrorFeature(this), new CreateBoundaryMessageFeature(this), new CreateBoundaryCancelFeature(this), new CreateBoundaryCompensateFeature(this), new CreateBoundarySignalFeature(this), 
        new CreateTimerCatchingEventFeature(this), new CreateSignalCatchingEventFeature(this), new CreateMessageCatchingEventFeature(this), 
        new CreateSignalThrowingEventFeature(this), new CreateCompensationThrowingEventFeature(this), new CreateNoneThrowingEventFeature(this),
        new CreateEventSubProcessFeature(this), new CreateTransactionFeature(this), new CreateEmbeddedSubProcessFeature(this), new CreatePoolFeature(this), new CreateLaneFeature(this),
        new CreateCallActivityFeature(this), new CreateAlfrescoScriptTaskFeature(this), new CreateAlfrescoMailTaskFeature(this),
        new CreateTextAnnotationFeature(this) };
  }
/**
 * 1.5      移除和删除功能
(1)     区别

①      Remove feature仅仅移把图符元素从图符模型中移除，底层的业务对象（领域模型元素）仍然存在。它基本上是与add feature相反的功能。

②      Delete feature是删除了相应的业务对象（域模型元素）。它基本上是Create Feature相反的功能。Delete feature通常调用Remove feature去删除的图符元素，然后删除相应的业务对象。

(2)     注意

①      通常情况下开发者不必实现这两个功能，graphiti框架提供了良好的默认实现（DefaultRemoveFeature类）。

②      如果定义了自己的Remove feature 和 delete feature 也需要在feature provider中发布。getRemoveFeature
---------------------  
原文：https://blog.csdn.net/andywangcn/article/details/7943720 

 */
  @Override
  public IDeleteFeature getDeleteFeature(IDeleteContext context) {
    PictogramElement pictogramElement = context.getPictogramElement();
    /**
     * getBusinessObjectForPictogramElement()
     * Returns:
     * The first of possibly several business objects which are linked to the given pictogram element. Can be null.
     */
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);//

    if (bo instanceof FlowElement) {
      return new DeleteFlowElementFeature(this);
    } else if (bo instanceof Lane || bo instanceof Pool) {
      return new DeleteLaneFeature(this);
    } else if (bo instanceof Artifact) {
      return new DeleteArtifactFeature(this);
    } else if (bo instanceof MessageFlow) {
      return new DeleteMessageFlowFeature(this);
    }
    return super.getDeleteFeature(context);
  }
/**
 * 1.12   提供复制粘贴功能
在图形编辑器，复制和粘贴的经常在graphical model-elements.上执行。

1.12.1    Creating a Copy Feature
(1)      必须实现接口ICopyFeature，或者继承其子类如AbstractCopyFeature。

(2)      覆写方法：canCopy，copy到剪贴板。

(3)      在feature provider中覆写getCopyFeature发布。

1.12.2    Creating a Paste Feature
(1)      必须实现接口IPasteFeature.，或者继承其子类如AbstractPasteFeature。

(2)     覆写方法：canPaste，paste

(3)      在feature provider中覆写getPasteFeature发布。
--------------------- 
原文：https://blog.csdn.net/andywangcn/article/details/7943720 
 */
  @Override
  public ICopyFeature getCopyFeature(ICopyContext context) {
    return new CopyFlowElementFeature(this);
  }

  @Override
  public IPasteFeature getPasteFeature(IPasteContext context) {
    return new PasteFlowElementFeature(this);
  }

  /**
   * 1.10提供添加连线（Connection）
1.10.1    步骤
(1)     创建add feature 必须实现接口IAddFeature，或者继承其抽象类。实现两个方法canAdd（判断给出的内容能否添加）和add（创建图形结构，确立与业务对象的联接）

(2)     在feature provider 中用方法getAddFeature发布。

(3)     Creating a Create-Connection-Feature

①       必须实现接口ICreateConnectionFeature或者继承其抽象子类，如AbstractCreateConnectionFeature。

②       覆写方法canCreate（检查给定内容是否能作为线两端点被连接的对象）canStartConnection（检查连线是否能从给定的源头锚开始）create，必须给要连接的对象的图形添加anchors（图形的Add Feature的add方法中peCreateService.createChopboxAnchor(containerShape);）发布。

③       必须给容器图形添加锚

 

1.10.2    Connection anchors
Anchor是一个计算过的固定的位置，通常与图形表示法有关。用户必须提供源头和目标锚，通常是通过拖放到锚的图形表达上，然后可以自动计算连接点。

 

框架目前默认支持3种锚。

方盒锚（Chop box anchor）：方盒锚在父图形的中心，是唯一不需要绘图算法的。连线的端点并不能直接练到锚上，而上在连线与图形的边缘焦点上，因此该焦点的位置会根据图形和线的大小位置实时计算。（IPeCreateService.createChopboxAnchor）

Figure: Chop box anchor (always pointing to the center)

 Box Relative Anchors：其位置与父图形的大小位置有关，例如可以设置在右边中点上。IPeCreateService.createBoxRelativeAnchor

Figure: Box relative anchor (on middle-right border outside of shape)

 Fix Point Anchors：其在父图形中固定位置上。（IPeCreateService.createFixPointAnchor）

连接线可以是一条折线；固定点锚的可见的位置端点location和逻辑端点reference-point是同一点。所有锚点都有一个图形表示, 如果连线有折点，则连接线是指从关联端点到邻接的一个折点的线段。

 

Note: All anchors have a graphical representation and connections are starting and ending virtually in the center of the graphical representation. But how can it be achieved that connections are starting precisely from the border of this graphical representation? For this purpose define a teh anchor location there and use setUseAnchorLocationAsConnectionEndpoint. At the end of this chapter is an example for this use case.

1.10.3    Connection Decorators修饰
Graphiti框架支持两种修饰：

静态修饰：（不活动图符元素），通常用于连接线的末端（如箭头），静态修饰只支持polyline polygon 否则不能旋转。

 

Figure: Static connection decorator

 动态修饰：激活的图符元素，可以被拖动（如线的文本标签）。

Figure: Dynamic connection decorator

 创建连线修饰的步骤：

①    创建一个连接线修饰符。

②    添加一个有效的图形算法给修饰符

③    添加这个修饰符到连接线。

④    （可选）链接修饰符到业务对象

这个修饰符将被添加到add connection feature. 在类中添加

private Polyline createArrow(GraphicsAlgorithmContainer gaContainer)

在add方法中创建修饰符。

Graphiti Developer Guide > Tutorial > Features > Add Connection Feature

1.10.4    创建通过在图元上拖拽的实现连接的连接线
--------------------- 
原文：https://blog.csdn.net/andywangcn/article/details/7943720 
   */
  @Override
  public ICreateConnectionFeature[] getCreateConnectionFeatures() {
    return new ICreateConnectionFeature[] { new CreateSequenceFlowFeature(this), 
        new CreateMessageFlowFeature(this), new CreateAssociationFeature(this) };
  }

  @Override
  public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
    return new ReconnectSequenceFlowFeature(this);
  }
/**
 * 1.4      更新（Update）功能
图元的属性值是存储在绘图算法中的（ graphics algorithm ）它只在创建是存储一次。如果想在图上或属性页编辑后直接生效，
则必须增加更新能力。

1.4.1        Graphiti支持两种更新策略：
(1)    如果自动更新是激活状态，业务模型的更改会立即更新到图（diagram）中。

(2)    如果自动更新是未激活状态，业务模型的更改后，在diagram中仅标记图形以过期。用户可以手动的更新这些图形。

(3)    默认情况下自动更新是未激活的，但是可以通过覆写IDiagramTypeProvider类中的以下方法配置：

①      isAutoUpdateAtStartup：当文件输入更改后刷新

②      isAutoUpdateAtRuntime：数据模型更改后刷新

③      isAutoUpdateAtReset：当编辑器获得焦点后刷新

1.4.2        创建update feature
(1)     实现接口IUpdateFeature.或者继承其子类如AbstractUpdateFeature

①      canUpdate：检查给定上下文的当前图符元素(pictogram element)的业务对象是否能被更新。

②      updateNeeded：检查图符元素的值是否最新。

③      update：从业务对象复制最新值到图符元素的绘图算法来更新图符元素。

(2)     在 feature provider中通过方法getUpdateFeature发布。
 */
  @Override
  public IUpdateFeature getUpdateFeature(IUpdateContext context) {
    PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    //ContainerShape:A representation of the model object 'Container Shape'.
    if (pictogramElement instanceof ContainerShape) {//容器型状态（包括方形，圆形等有边界图片，线和点不属于容器状态，因为都是实心的，中间没有可以装填东西的区域）
      if (bo instanceof FlowElement) {
        return new UpdateFlowElementFeature(this);
      } else if (bo instanceof Pool || bo instanceof Lane) {
        return new UpdatePoolAndLaneFeature(this);
      } else if (bo instanceof TextAnnotation) {
        return new UpdateTextAnnotationFeature(this);
      }
      //FreeFormConnection:A representation of the model object 'Free Form Connection'.
    } else if (pictogramElement instanceof FreeFormConnection) {//自由形式连接
      if (bo instanceof FlowElement) {
        return new UpdateFlowElementFeature(this);
      } else if (bo instanceof MessageFlow) {
        return new UpdateMessageFlowFeature(this);
      }
    }
    return super.getUpdateFeature(context);
  }

  @Override
  public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
    // simply return all create connection features
    return getCreateConnectionFeatures();
  }

  /**
   * 1.11提供直接编辑图元的功能
直接编辑图形，图形实时的现实修改。

1.11.1    Creating a Direct Editing Feature
(1)     创建Direct Editing Feature必须实现接口IDirectEditingFeature或者继承其子类如AbstractDirectEditingFeature.

(2)     实现或覆写方法：

①      getEditingType：返回用于编辑该值的编辑器的类型。

②      canDirectEdit：该方法检查给定的上下文，并因此决定是否支持直接编辑。

③      getInitialValue：返回编辑器用以进行初始化的初始值，这通常是在当前显示的值。

④      checkValueValid：对当前被编辑器编辑而变化的值进行检查。

⑤      setValue：在编辑过程的结束时，把编辑的值设置到模型。

(3)     在feature provider中覆写getDirectEditingFeature发布。

1.11.2    图元对象被创建之后就立即激活直接修改功能。
一个新的图元在创建时会调用不同的feature：create feature, add feature and update feature.。为了自动切换到直接编辑模式，我们需要从这些features中收集一些信息，并将其存储在接口IDirectEditingInfo。所有的features通过调用getFeatureProvider().getDirectEditingInfo().来访问直接编辑信息。在创建过程中的自动直接编辑的必须在create feature中激活，在create方法的后部实现。

In the add feature the outer container shape of the newly created object must be set。the shape (pictogram element) and its graphics algorithm have to be specified, where the direct editing editor shall be opened.

// set container shape for direct editing after object creation

directEditingInfo.setMainPictogramElement(containerShape);

// set shape and graphics algorithm where the editor for

// direct editing shall be opened after object creation

directEditingInfo.setPictogramElement(shape);

directEditingInfo.setGraphicsAlgorithm(text);
--------------------- 
原文：https://blog.csdn.net/andywangcn/article/details/7943720 
   */
  @Override
  public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
    PictogramElement pe = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pe);
    if (bo instanceof FlowElement) {
      return new DirectEditFlowElementFeature(this);
    } else if (bo instanceof TextAnnotation) {
      return new DirectEditTextAnnotationFeature(this);
    }
    return super.getDirectEditingFeature(context);
  }
/**
 * 1.7    提供调整大小的功能
实现接口IResizeShapeFeature，或者继承其具体的子接口或抽象子类。

一般要覆写canResizeShape, resizeShape方法。
 */
  @Override
  public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
    Shape shape = context.getShape();
    Object bo = getBusinessObjectForPictogramElement(shape);
    if (bo instanceof SubProcess || bo instanceof Pool || bo instanceof Lane) {
      return new ContainerResizeFeature(this);
    } else if (bo instanceof Activity) {
      return new ActivityResizeFeature(this);
    }
    return super.getResizeShapeFeature(context);
  }
/*移动图形时调用*/
  @Override
  public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
    Shape shape = context.getShape();
    Object bo = getBusinessObjectForPictogramElement(shape);
    if (bo instanceof BoundaryEvent) {
      return new MoveBoundaryEventFeature(this);

    } else if (bo instanceof Activity) {
      // in case an activity is moved, make sure, attached boundary events will move too
      return new MoveActivityFeature(this);

    } else if (bo instanceof Gateway) {
      return new MoveGatewayFeature(this);

    } else if (bo instanceof Event) {
      return new MoveEventFeature(this);

    } else if (bo instanceof Lane) {
      return new MoveLaneFeature(this);
    
    } else if (bo instanceof Pool) {
      // in case a pool is moved, make sure, attached boundary events will move too
      return new MovePoolFeature(this);
    
    } else if (bo instanceof TextAnnotation) {
      return new MoveTextAnnotationFeature(this);
    }
    return super.getMoveShapeFeature(context);
  }
/**
 * 1.8    提供布局功能
支持重新计算的图符模型（pictogram model）内的位置和大小。

实现接口ILayoutFeature，或者继承其具体的子接口或抽象子类。覆写canLayout和layout方法。
 */
  @Override
  public ILayoutFeature getLayoutFeature(ILayoutContext context) {
    final PictogramElement pe = context.getPictogramElement();
    final Object bo = getBusinessObjectForPictogramElement(pe);

    if (bo instanceof TextAnnotation) {
      return new LayoutTextAnnotationFeature(this);
    }

    return super.getLayoutFeature(context);
  }
/**
 * 1.9  提供自定义功能
实现接口ICustomFeature，或者继承其具体的子接口或抽象子类。

覆写canExecute，execute方法。

这两种方法获得一个用户上下文作为参数。接口ICostomContext提供内部的图符元素和内部的图形算法的另外象形元素（选择）。这些是将鼠标指针置于/点击的内部元件。
--------------------- 
原文：https://blog.csdn.net/andywangcn/article/details/7943720 
 */
  @Override
  public ICustomFeature[] getCustomFeatures(ICustomContext context) {
    return new ICustomFeature[] { new DeletePoolFeature(this), new ChangeElementTypeFeature(this) };
  }
}
