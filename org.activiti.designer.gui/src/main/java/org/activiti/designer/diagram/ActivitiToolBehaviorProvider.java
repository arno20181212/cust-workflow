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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BusinessRuleTask;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.CancelEventDefinition;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ErrorEventDefinition;
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.EventGateway;
import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.TerminateEventDefinition;
import org.activiti.bpmn.model.ThrowEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.bpmn.model.Transaction;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.alfresco.AlfrescoStartEvent;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.features.AbstractCreateBPMNFeature;
import org.activiti.designer.features.ChangeElementTypeFeature;
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
import org.activiti.designer.features.CreateCustomServiceTaskFeature;
import org.activiti.designer.features.CreateCustomUserTaskFeature;
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
import org.activiti.designer.features.CreateMessageStartEventFeature;
import org.activiti.designer.features.CreateNoneThrowingEventFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreatePoolFeature;
import org.activiti.designer.features.CreateReceiveTaskFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateSendTaskFeature;
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
import org.activiti.designer.features.DeletePoolFeature;
import org.activiti.designer.features.contextmenu.OpenCalledElementForCallActivity;
import org.activiti.designer.integration.annotation.TaskName;
import org.activiti.designer.integration.annotation.TaskNames;
import org.activiti.designer.integration.palette.PaletteEntry;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.extension.CustomServiceTaskContext;
import org.activiti.designer.util.extension.CustomUserTaskContext;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.activiti.designer.util.workspace.ActivitiWorkspaceUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.alfresco.designer.gui.features.CreateAlfrescoMailTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoScriptTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoStartEventFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoUserTaskFeature;
/**
 * https://blog.csdn.net/andywangcn/article/details/7997623
 * @author forms-huangcan
 *
 */
public class ActivitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

  private static final Map<Class< ? extends ICreateFeature>, PaletteEntry> toolMapping = new HashMap<Class< ? extends ICreateFeature>, PaletteEntry>();

  public ActivitiToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);

    // Setup tool mappings to palette entries
    toolMapping.put(CreateStartEventFeature.class, PaletteEntry.START_EVENT);
    toolMapping.put(CreateTimerStartEventFeature.class, PaletteEntry.TIMER_START_EVENT);
    toolMapping.put(CreateMessageStartEventFeature.class, PaletteEntry.MESSAGE_START_EVENT);
    toolMapping.put(CreateErrorStartEventFeature.class, PaletteEntry.ERROR_START_EVENT);
    toolMapping.put(CreateSignalStartEventFeature.class, PaletteEntry.SIGNAL_START_EVENT);
    toolMapping.put(CreateAlfrescoStartEventFeature.class, PaletteEntry.ALFRESCO_START_EVENT);
    toolMapping.put(CreateEndEventFeature.class, PaletteEntry.END_EVENT);
    toolMapping.put(CreateErrorEndEventFeature.class, PaletteEntry.ERROR_END_EVENT);
    toolMapping.put(CreateTerminateEndEventFeature.class, PaletteEntry.TERMINATE_END_EVENT);
    toolMapping.put(CreateCancelEndEventFeature.class, PaletteEntry.CANCEL_END_EVENT);
    toolMapping.put(CreateExclusiveGatewayFeature.class, PaletteEntry.EXCLUSIVE_GATEWAY);
    toolMapping.put(CreateInclusiveGatewayFeature.class, PaletteEntry.INCLUSIVE_GATEWAY);
    toolMapping.put(CreateEventGatewayFeature.class, PaletteEntry.EVENT_GATEWAY);
    toolMapping.put(CreateMailTaskFeature.class, PaletteEntry.MAIL_TASK);
    toolMapping.put(CreateManualTaskFeature.class, PaletteEntry.MANUAL_TASK);
    toolMapping.put(CreateReceiveTaskFeature.class, PaletteEntry.RECEIVE_TASK);
    toolMapping.put(CreateParallelGatewayFeature.class, PaletteEntry.PARALLEL_GATEWAY);
    toolMapping.put(CreateScriptTaskFeature.class, PaletteEntry.SCRIPT_TASK);
    toolMapping.put(CreateServiceTaskFeature.class, PaletteEntry.SERVICE_TASK);
    toolMapping.put(CreateSendTaskFeature.class, PaletteEntry.SEND_TASK);
    toolMapping.put(CreateCallActivityFeature.class, PaletteEntry.CALL_ACTIVITY);
    toolMapping.put(CreateEmbeddedSubProcessFeature.class, PaletteEntry.SUBPROCESS);
    toolMapping.put(CreatePoolFeature.class, PaletteEntry.POOL);
    toolMapping.put(CreateLaneFeature.class, PaletteEntry.LANE);
    toolMapping.put(CreateEventSubProcessFeature.class, PaletteEntry.EVENT_SUBPROCESS);
    toolMapping.put(CreateTransactionFeature.class, PaletteEntry.TRANSACTION);
    toolMapping.put(CreateUserTaskFeature.class, PaletteEntry.USER_TASK);
    toolMapping.put(CreateAlfrescoUserTaskFeature.class, PaletteEntry.ALFRESCO_USER_TASK);
    toolMapping.put(CreateBoundaryTimerFeature.class, PaletteEntry.BOUNDARY_TIMER);
    toolMapping.put(CreateBoundaryErrorFeature.class, PaletteEntry.BOUNDARY_ERROR);
    toolMapping.put(CreateBoundaryMessageFeature.class, PaletteEntry.BOUNDARY_MESSAGE);
    toolMapping.put(CreateBoundarySignalFeature.class, PaletteEntry.BOUNDARY_SIGNAL);
    toolMapping.put(CreateBoundaryCancelFeature.class, PaletteEntry.BOUNDARY_CANCEL);
    toolMapping.put(CreateBoundaryCompensateFeature.class, PaletteEntry.BOUNDARY_COMPENSATION);
    toolMapping.put(CreateTimerCatchingEventFeature.class, PaletteEntry.CATCH_TIMER);
    toolMapping.put(CreateSignalCatchingEventFeature.class, PaletteEntry.CATCH_SIGNAL);
    toolMapping.put(CreateMessageCatchingEventFeature.class, PaletteEntry.CATCH_MESSAGE);
    toolMapping.put(CreateSignalThrowingEventFeature.class, PaletteEntry.THROW_SIGNAL);
    toolMapping.put(CreateCompensationThrowingEventFeature.class, PaletteEntry.THROW_COMPENSATION);
    toolMapping.put(CreateNoneThrowingEventFeature.class, PaletteEntry.THROW_NONE);
    toolMapping.put(CreateBusinessRuleTaskFeature.class, PaletteEntry.BUSINESSRULE_TASK);
    toolMapping.put(CreateAlfrescoScriptTaskFeature.class, PaletteEntry.ALFRESCO_SCRIPT_TASK);
    toolMapping.put(CreateAlfrescoMailTaskFeature.class, PaletteEntry.ALFRESCO_MAIL_TASK);
    toolMapping.put(CreateTextAnnotationFeature.class, PaletteEntry.TEXT_ANNOTATION);
  }
 /**
 * 双击行为定义在tool behavior provider中。通过覆写方法getDoubleClickFeature返回custom feature为给定的上下文执行双击行为。
 */
  @Override
  public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
    /*
     * ICustomFeature customFeature = new
     * ExpandCollapseSubProcessFeature(getFeatureProvider()); if
     * (customFeature.canExecute(context)) { return customFeature; }
     */
    
    //open call activity called element
    openCallActivityCalledElement(context);//打开容器类(Container)的组件（e.g:EventSubProcess,Transaction,SubProcess,Pool,Lane）
    
    return super.getDoubleClickFeature(context); 
  }
/**
 * (1) ceating an Image Provider
 * 
 * Graphiti是独立于平台的，但图像的实例是依赖于平台的。因此Graphiti中的图片是通过标识符（identify）来使用的，image provider的任务就是为图片标识符发布依赖平台的图片。
 * 
 * 创 建Image provider必须实现接口IImageProvider，或者继承其子类如AbstractImageProvider。
 * 覆写或实现方法 addAvailableImages（注册所有可用的图像标识符，并将它们映射到他们的依赖于平台的图像）。
 * 实现tool behavior provider类中的getContextButtonPad方法。
 * 
 * -============================================
 * https://blog.csdn.net/andywangcn/article/details/7997623
 * 
 1.2      提供Context Buttons
(1)      Context Button是每一个图符元素上小的、自动显示图标的动作按钮。当鼠标在图元上时显示，移除图元时隐藏。
Context Button不仅能提供Context menu的功能，并且一个Context Button上还可以添加多个drag & drop feature。

(2)      Context buttons 需要在 tool behavior provider中定义。

须要覆写tool behavior provider中的一个方法：
The method getContextButtonPad has to return the context buttons for the given context (which implement IContextButtonEntry).
 
(3)      有如下几类context button：

①    Generic context buttons:对于所有工具，该类按钮外观和行为都是标准化的，如delete、remove、update。显示在图元的上边缘，有个特殊的工具可以选择显示或隐藏哪一个。（0~3个）

②      Domain specific context buttons：他们是针对每个工具的，显示在图元右边缘和下边缘。外观和行为必须自定义。（0~5个）

③      Collapse context button: 对于所有工具，该类按钮外观和行为都是标准化的，必须定义一个特定的工具，通过定义调用功能来定义按钮的行为（createCollapseContextButton）。（0~1个）

每个context button可以有一个点击的功能和几个拖功放能（连接线），当同时存在多个拖放feature时，会有一个context-menu提示用户选择某个feature执行
 */
  @Override
  public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
    IContextButtonPadData data = super.getContextButtonPad(context);
    PictogramElement pe = context.getPictogramElement();

    //Generic context buttons:对于所有工具，该类按钮外观和行为都是标准化的，
    //如delete、remove、update。显示在图元的上边缘，有个特殊的工具可以选择显示或隐藏哪一个。（0~3个）
    setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);//Arno：就是显示在最上面的垃圾桶

    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);

    CreateConnectionContext connectionContext = new CreateConnectionContext();
    connectionContext.setSourcePictogramElement(pe);
    //连接线只能从锚点连接，如果容器类组件发出连接，从需要获取这个组件的锚点
    Anchor connectionAnchor = null;
    if (pe instanceof Anchor) {//这是什么情况？放在锚点上，出现"<->"这个符号？
      connectionAnchor = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {//鼠标防止图形上（容器类【中心可以放置东西的形状就是容器】），出现的连线
      connectionAnchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    connectionContext.setSourceAnchor(connectionAnchor);

    if (pe.eContainer() instanceof ContainerShape == false) {//
      return data;
    }
    //为何要加connectionContext，因为我们点击这些功能后，生成的新组件，都和原来的组件有连线的
    CreateContext taskContext = new CreateContext();
    taskContext.setTargetContainer((ContainerShape) pe.eContainer());
    taskContext.putProperty("org.activiti.designer.connectionContext", connectionContext);

    if (bo instanceof StartEvent || (bo instanceof Activity && bo instanceof EventSubProcess == false) ||
        bo instanceof IntermediateCatchEvent || bo instanceof ThrowEvent ||
        bo instanceof Gateway || bo instanceof BoundaryEvent) {

      CreateUserTaskFeature userTaskfeature = new CreateUserTaskFeature(getFeatureProvider());
      ContextButtonEntry newUserTaskButton = new ContextButtonEntry(userTaskfeature, taskContext);
      newUserTaskButton.setText("new user task"); //$NON-NLS-1$
      newUserTaskButton.setDescription("Create a new task"); //$NON-NLS-1$
      newUserTaskButton.setIconId(PluginImage.IMG_USERTASK.getImageKey());
      //Domain specific context buttons：他们是针对每个工具的，显示在图元右边缘和下边缘。外观和行为必须自定义。（0~5个）
      data.getDomainSpecificContextButtons().add(newUserTaskButton);

      CreateExclusiveGatewayFeature exclusiveGatewayFeature = new CreateExclusiveGatewayFeature(getFeatureProvider());
      ContextButtonEntry newExclusiveGatewayButton = new ContextButtonEntry(exclusiveGatewayFeature, taskContext);
      newExclusiveGatewayButton.setText("new exclusive gateway"); //$NON-NLS-1$
      newExclusiveGatewayButton.setDescription("Create a new exclusive gateway"); //$NON-NLS-1$
      newExclusiveGatewayButton.setIconId(PluginImage.IMG_GATEWAY_EXCLUSIVE.getImageKey());
      data.getDomainSpecificContextButtons().add(newExclusiveGatewayButton);

      CreateEndEventFeature endFeature = new CreateEndEventFeature(getFeatureProvider());
      ContextButtonEntry newEndButton = new ContextButtonEntry(endFeature, taskContext);
      newEndButton.setText("new end event"); //$NON-NLS-1$
      newEndButton.setDescription("Create a new end event"); //$NON-NLS-1$
      newEndButton.setIconId(PluginImage.IMG_ENDEVENT_NONE.getImageKey());
      data.getDomainSpecificContextButtons().add(newEndButton);
    }
    //创建单独的箭头，需要拖拽连线
    CreateConnectionContext ccc = new CreateConnectionContext();
    ccc.setSourcePictogramElement(pe);
    Anchor anchor = null;
    if (pe instanceof Anchor) {
      anchor = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {
      anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    ccc.setSourceAnchor(anchor);

    ContextButtonEntry button = new ContextButtonEntry(null, context);
    button.setText("Create connection"); //$NON-NLS-1$
    button.setIconId(PluginImage.IMG_EREFERENCE.getImageKey());
    ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
    for (ICreateConnectionFeature feature : features) {
      if (feature.isAvailable(ccc) && feature.canStartConnection(ccc)) {
    	//就是在编辑器中，选择箭头之后（drag），然后扯到目标上松开（drop），完成连线
        button.addDragAndDropFeature(feature);
      }
    }

    if (button.getDragAndDropFeatures().size() > 0) {
      data.getDomainSpecificContextButtons().add(button);
    }

    if (bo instanceof StartEvent || (bo instanceof Activity && bo instanceof EventSubProcess == false) ||
        bo instanceof IntermediateCatchEvent || bo instanceof ThrowEvent ||
        bo instanceof Gateway || bo instanceof BoundaryEvent) {

      ContextButtonEntry otherElementButton = new ContextButtonEntry(null, null);
      otherElementButton.setText("new element"); //$NON-NLS-1$
      otherElementButton.setDescription("Create a new element"); //$NON-NLS-1$
      otherElementButton.setIconId(PluginImage.NEW_ICON.getImageKey());
      data.getDomainSpecificContextButtons().add(otherElementButton);

      addContextButton(otherElementButton, new CreateServiceTaskFeature(getFeatureProvider()), taskContext, "Create service task", "Create a new service task", PluginImage.IMG_SERVICETASK);
      addContextButton(otherElementButton, new CreateScriptTaskFeature(getFeatureProvider()), taskContext, "Create script task", "Create a new script task", PluginImage.IMG_SCRIPTTASK);
      addContextButton(otherElementButton, new CreateUserTaskFeature(getFeatureProvider()), taskContext, "Create user task", "Create a new user task", PluginImage.IMG_USERTASK);
      addContextButton(otherElementButton, new CreateMailTaskFeature(getFeatureProvider()), taskContext, "Create mail task", "Create a new mail task", PluginImage.IMG_MAILTASK);
      addContextButton(otherElementButton, new CreateBusinessRuleTaskFeature(getFeatureProvider()), taskContext, "Create business rule task",
          "Create a new business rule task", PluginImage.IMG_BUSINESSRULETASK);
      addContextButton(otherElementButton, new CreateManualTaskFeature(getFeatureProvider()), taskContext, "Create manual task", "Create a new manual task", PluginImage.IMG_MANUALTASK);
      addContextButton(otherElementButton, new CreateReceiveTaskFeature(getFeatureProvider()), taskContext, "Create receive task", "Create a new receive task", PluginImage.IMG_RECEIVETASK);
      addContextButton(otherElementButton, new CreateCallActivityFeature(getFeatureProvider()), taskContext, "Create call activity",
          "Create a new call activiti", PluginImage.IMG_CALLACTIVITY);
      addContextButton(otherElementButton, new CreateExclusiveGatewayFeature(getFeatureProvider()), taskContext, "Create exclusive gateway",
          "Create a new exclusive gateway", PluginImage.IMG_GATEWAY_EXCLUSIVE);
      addContextButton(otherElementButton, new CreateInclusiveGatewayFeature(getFeatureProvider()), taskContext, "Create inclusive gateway",
          "Create a new inclusive gateway", PluginImage.IMG_GATEWAY_INCLUSIVE);
      addContextButton(otherElementButton, new CreateParallelGatewayFeature(getFeatureProvider()), taskContext, "Create parallel gateway",
          "Create a new parallel gateway", PluginImage.IMG_GATEWAY_PARALLEL);
      addContextButton(otherElementButton, new CreateEndEventFeature(getFeatureProvider()), taskContext, "Create end event", "Create a new end event", PluginImage.IMG_ENDEVENT_NONE);
      addContextButton(otherElementButton, new CreateErrorEndEventFeature(getFeatureProvider()), taskContext, "Create error end event",
          "Create a new error end event", PluginImage.IMG_EVENT_ERROR);
      addContextButton(otherElementButton, new CreateTerminateEndEventFeature(getFeatureProvider()), taskContext, "Create terminate end event",
          "Create a new terminate end event", PluginImage.IMG_EVENT_TERMINATE);
      addContextButton(otherElementButton, new CreateTimerCatchingEventFeature(getFeatureProvider()), taskContext, "Create intermediate catch timer event",
          "Create a new intermediate catch timer event", PluginImage.IMG_EVENT_TIMER);
      addContextButton(otherElementButton, new CreateMessageCatchingEventFeature(getFeatureProvider()), taskContext, "Create intermediate catch message event",
          "Create a new intermediate catch message event", PluginImage.IMG_EVENT_MESSAGE);
      addContextButton(otherElementButton, new CreateSignalCatchingEventFeature(getFeatureProvider()), taskContext, "Create intermediate catch signal event",
          "Create a new intermediate catch signal event", PluginImage.IMG_EVENT_SIGNAL);
      addContextButton(otherElementButton, new CreateNoneThrowingEventFeature(getFeatureProvider()), taskContext, "Create intermediate throw none event",
          "Create a new intermediate throw none event", PluginImage.IMG_THROW_NONE);
      addContextButton(otherElementButton, new CreateSignalThrowingEventFeature(getFeatureProvider()), taskContext, "Create intermediate throw signal event",
          "Create a new intermediate throw signal event", PluginImage.IMG_THROW_SIGNAL);
      addContextButton(otherElementButton, new CreateCompensationThrowingEventFeature(getFeatureProvider()), taskContext, "Create intermediate throw compensation event",
          "Create a new intermediate throw compensation event", PluginImage.IMG_THROW_COMPENSATION);
      addContextButton(otherElementButton, new CreateAlfrescoScriptTaskFeature(getFeatureProvider()), taskContext, "Create alfresco script task",
          "Create a new alfresco script task", PluginImage.IMG_SERVICETASK);
      addContextButton(otherElementButton, new CreateAlfrescoUserTaskFeature(getFeatureProvider()), taskContext, "Create alfresco user task",
          "Create a new alfresco user task", PluginImage.IMG_USERTASK);
      addContextButton(otherElementButton, new CreateAlfrescoMailTaskFeature(getFeatureProvider()), taskContext, "Create alfresco mail task",
          "Create a new alfresco mail task", PluginImage.IMG_MAILTASK);
    }

    ContextButtonEntry editElementButton = new ContextButtonEntry(null, null);
    editElementButton.setText("change element type"); //$NON-NLS-1$
    editElementButton.setDescription("Change the element type to another type"); //$NON-NLS-1$
    editElementButton.setIconId(PluginImage.EDIT_ICON.getImageKey());
    data.getDomainSpecificContextButtons().add(editElementButton);

    CustomContext customContext = new CustomContext();
    customContext.putProperty("org.activiti.designer.changetype.pictogram", pe);

    if (bo instanceof Task) {
      addTaskButtons(editElementButton, (Task) bo, customContext);
    } else if (bo instanceof Gateway) {
      addGatewayButtons(editElementButton, (Gateway) bo, customContext);
    } else if (bo instanceof StartEvent) {
      addStartEventButtons(editElementButton, (StartEvent) bo, customContext);
    } else if (bo instanceof EndEvent) {
      addEndEventButtons(editElementButton, (EndEvent) bo, customContext);
    } else if (bo instanceof BoundaryEvent) {
      addBoundaryEventButtons(editElementButton, (BoundaryEvent) bo, customContext);
    } else if (bo instanceof ThrowEvent) {
      addThrowEventButtons(editElementButton, (ThrowEvent) bo, customContext);
    } else if (bo instanceof IntermediateCatchEvent) {
      addIntermediateCatchEventButtons(editElementButton, (IntermediateCatchEvent) bo, customContext);
    }

    return data;
  }

  private void addGatewayButtons(ContextButtonEntry otherElementButton, Gateway notGateway, CustomContext customContext) {
    if (notGateway == null || !(notGateway instanceof ExclusiveGateway)) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.GATEWAY_EXCLUSIVE), customContext,
          "Change to exclusive gateway", "Change to an exclusive gateway", PluginImage.IMG_GATEWAY_EXCLUSIVE);
    }
    if (notGateway == null || !(notGateway instanceof InclusiveGateway)) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.GATEWAY_INCLUSIVE), customContext,
          "Change to inclusive gateway", "Change to an inclusive gateway", PluginImage.IMG_GATEWAY_INCLUSIVE);
    }
    if (notGateway == null || !(notGateway instanceof ParallelGateway)) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.GATEWAY_PARALLEL), customContext, 
          "Change to parallel gateway", "Change to a parallel gateway", PluginImage.IMG_GATEWAY_PARALLEL);
    }
    if (notGateway == null || !(notGateway instanceof EventGateway)) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.GATEWAY_EVENT), customContext, 
          "Change to event gateway", "Change to a event gateway", PluginImage.IMG_GATEWAY_EVENT);
    }
  }

  private void addStartEventButtons(ContextButtonEntry otherElementButton, StartEvent notStartEvent, CustomContext customContext) {
    if (notStartEvent instanceof AlfrescoStartEvent) {
      return;
    }
    String startEventType = null;
    for (EventDefinition eventDefinition : notStartEvent.getEventDefinitions()) {
      if (eventDefinition instanceof TimerEventDefinition) {
        startEventType = "timer";
      } else if (eventDefinition instanceof MessageEventDefinition) {
        startEventType = "message";
      } else if (eventDefinition instanceof ErrorEventDefinition) {
        startEventType = "error";
      } else if (eventDefinition instanceof SignalEventDefinition) {
        startEventType = "signal";
      }
    }
    if (startEventType == null) {
      startEventType = "none";
    }

    if ("none".equals(startEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_START_NONE), customContext,
              "Change to none start event", "Change to a none start event", PluginImage.IMG_STARTEVENT_NONE);
    }
    if ("timer".equals(startEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_START_TIMER), customContext,
              "Change to timer start event", "Change to a timer start event", PluginImage.IMG_EVENT_TIMER);
    }
    if ("message".equals(startEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_START_MESSAGE), customContext,
              "Change to message start event", "Change to a message start event", PluginImage.IMG_EVENT_MESSAGE);
    }
    if ("error".equals(startEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_START_ERROR), customContext,
              "Change to error start event", "Change to an error start event", PluginImage.IMG_EVENT_ERROR);
    }
    if ("signal".equals(startEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_START_SIGNAL), customContext,
              "Change to signal start event", "Change to a signal start event", PluginImage.IMG_EVENT_SIGNAL);
    }
  }

  private void addEndEventButtons(ContextButtonEntry otherElementButton, EndEvent notEndEvent, CustomContext customContext) {
    String endEventType = null;
    for (EventDefinition eventDefinition : notEndEvent.getEventDefinitions()) {
      if (eventDefinition instanceof ErrorEventDefinition) {
        endEventType = "error";
      } else if (eventDefinition instanceof TerminateEventDefinition) {
        endEventType = "terminate";
      } else if (eventDefinition instanceof CancelEventDefinition) {
        endEventType = "cancel";
      }
    }
    if (endEventType == null) {
      endEventType = "none";
    }

    if ("none".equals(endEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_END_NONE), customContext,
              "Change to none end event", "Change to a none end event", PluginImage.IMG_ENDEVENT_NONE);
    }
    if ("error".equals(endEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_END_ERROR), customContext,
              "Change to error end event", "Change to an error end event", PluginImage.IMG_EVENT_ERROR);
    }
    if ("terminate".equals(endEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_END_TERMINATE), customContext,
              "Change to terminate end event", "Change to a terminate end event", PluginImage.IMG_EVENT_TERMINATE);
    }
    if ("cancel".equals(endEventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_END_CANCEL), customContext,
              "Change to cancel end event", "Change to a cancel end event", PluginImage.IMG_EVENT_CANCEL);
    }
  }

  private void addBoundaryEventButtons(ContextButtonEntry otherElementButton, BoundaryEvent notBoundaryEvent, CustomContext customContext) {
    String eventType = null;
    for (EventDefinition eventDefinition : notBoundaryEvent.getEventDefinitions()) {
      if (eventDefinition instanceof TimerEventDefinition) {
        eventType = "timer";
      } else if (eventDefinition instanceof MessageEventDefinition) {
        eventType = "message";
      } else if (eventDefinition instanceof ErrorEventDefinition) {
        eventType = "error";
      } else if (eventDefinition instanceof SignalEventDefinition) {
        eventType = "signal";
      } else if (eventDefinition instanceof CancelEventDefinition) {
        eventType = "cancel";
      } else if (eventDefinition instanceof CompensateEventDefinition) {
        eventType = "compensate";
      }
    }
    if (eventType == null) {
      return;
    }

    if ("timer".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_TIMER), customContext,
              "Change to timer boundary event", "Change to a timer boundary event", PluginImage.IMG_EVENT_TIMER);
    }
    if ("message".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_MESSAGE), customContext,
              "Change to message boundary event", "Change to a message boundary event", PluginImage.IMG_EVENT_MESSAGE);
    }
    if ("error".equals(eventType) == false) {
      Object parentObject = notBoundaryEvent.getAttachedToRef();
      if (parentObject instanceof SubProcess || parentObject instanceof CallActivity || parentObject instanceof ServiceTask) {
        addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_ERROR), customContext,
                "Change to error boundary event", "Change to an error boundary event", PluginImage.IMG_EVENT_ERROR);
      }
    }
    if ("signal".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_SIGNAL), customContext,
              "Change to signal boundary event", "Change to a signal boundary event", PluginImage.IMG_EVENT_SIGNAL);
    }
    if ("cancel".equals(eventType) == false) {
      Object parentObject = notBoundaryEvent.getAttachedToRef();
      if (parentObject instanceof Transaction) {
        addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_CANCEL), customContext,
                "Change to cancel boundary event", "Change to a cancel boundary event", PluginImage.IMG_EVENT_CANCEL);
      }
    }
    if ("compensate".equals(eventType) == false) {
      Object parentObject = notBoundaryEvent.getAttachedToRef();
      if (parentObject instanceof Activity && parentObject instanceof SubProcess == false) {
        addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_BOUNDARY_COMPENSATION), customContext,
                "Change to compensation boundary event", "Change to a compensation boundary event", PluginImage.IMG_EVENT_COMPENSATION);
      }
    }
  }
  
  private void addIntermediateCatchEventButtons(ContextButtonEntry otherElementButton, IntermediateCatchEvent catchEvent, CustomContext customContext) {
    String eventType = null;
    for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
      if (eventDefinition instanceof TimerEventDefinition) {
        eventType = "timer";
      } else if (eventDefinition instanceof MessageEventDefinition) {
        eventType = "message";
      } else if (eventDefinition instanceof SignalEventDefinition) {
        eventType = "signal";
      }
    }
    if (eventType == null) {
      return;
    }

    if ("timer".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_CATCH_TIMER), customContext,
              "Change to intermediate catch timer event", "Change to an intermediate catch timer event", PluginImage.IMG_EVENT_TIMER);
    }
    if ("message".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_CATCH_MESSAGE), customContext,
              "Change to intermediate catch message event", "Change to an intermediate catch message event", PluginImage.IMG_EVENT_MESSAGE);
    }
    if ("signal".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_CATCH_SIGNAL), customContext,
              "Change to intermediate catch signal event", "Change to an intermediate catch signal event", PluginImage.IMG_EVENT_SIGNAL);
    }
  }
  
  private void addThrowEventButtons(ContextButtonEntry otherElementButton, ThrowEvent throwEvent, CustomContext customContext) {
    String eventType = null;
    for (EventDefinition eventDefinition : throwEvent.getEventDefinitions()) {
      if (eventDefinition instanceof SignalEventDefinition) {
        eventType = "signal";
      } else if (eventDefinition instanceof CompensateEventDefinition) {
        eventType = "compensate";
      }
    }
    
    if (eventType == null) {
      eventType = "none";
    }

    if ("none".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_THROW_NONE), customContext,
              "Change to intermediate throw none event", "Change to an intermediate throw none event", PluginImage.IMG_THROW_NONE);
    }
    if ("signal".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_THROW_SIGNAL), customContext,
              "Change to intermediate throw signal event", "Change to an intermediate throw signal event", PluginImage.IMG_THROW_SIGNAL);
    }
    if ("compensate".equals(eventType) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.EVENT_THROW_COMPENSATION), customContext,
              "Change to intermediate throw compensation event", "Change to an intermediate throw compensation event", PluginImage.IMG_THROW_COMPENSATION);
    }
  }

  private void addTaskButtons(ContextButtonEntry otherElementButton, Task notTask, CustomContext customContext) {
    if (notTask == null || notTask instanceof ServiceTask == false || ServiceTask.MAIL_TASK.equalsIgnoreCase(((ServiceTask) notTask).getType())) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_SERVICE), customContext, 
          "Change to service task", "Change to a service task", PluginImage.IMG_SERVICETASK);
    }
    if (notTask == null || notTask instanceof ScriptTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_SCRIPT), customContext, 
          "Change to script task", "Change to a script task", PluginImage.IMG_SCRIPTTASK);
    }
    if (notTask == null || notTask instanceof UserTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_USER), customContext, 
          "Change to user task", "Change to a user task", PluginImage.IMG_USERTASK);
    }
    if (notTask == null || notTask instanceof ServiceTask == false || ServiceTask.MAIL_TASK.equalsIgnoreCase(((ServiceTask) notTask).getType()) == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_MAIL), customContext, 
          "Change to mail task", "Change to a mail task", PluginImage.IMG_MAILTASK);
    }
    if (notTask == null || notTask instanceof BusinessRuleTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_BUSINESSRULE), customContext,
          "Change to business rule task", "Change to a business rule task", PluginImage.IMG_BUSINESSRULETASK);
    }
    if (notTask == null || notTask instanceof ManualTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_MANUAL), customContext, 
          "Change to manual task", "Change to a manual task", PluginImage.IMG_MANUALTASK);
    }
    if (notTask == null || notTask instanceof ReceiveTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), ChangeElementTypeFeature.TASK_RECEIVE), customContext, 
          "Change to receive task", "Change to a receive task", PluginImage.IMG_RECEIVETASK);
    }
  }

  private void addContextButton(ContextButtonEntry button, ChangeElementTypeFeature feature, CustomContext customContext, String text, String description,
          PluginImage image) {

    ContextButtonEntry newButton = new ContextButtonEntry(feature, customContext);
    newButton.setText(text);
    newButton.setDescription(description);
    newButton.setIconId(image.getImageKey());
    button.getContextButtonMenuEntries().add(newButton);
  }

  private void addContextButton(ContextButtonEntry button, AbstractCreateBPMNFeature feature, CreateContext context, String text, String description,
          PluginImage image) {

    ContextButtonEntry newButton = new ContextButtonEntry(feature, context);
    newButton.setText(text);
    newButton.setDescription(description);
    newButton.setIconId(image.getImageKey());
    button.getContextButtonMenuEntries().add(newButton);
  }
/**
 * 1.3 Providing a Context Menu 提供上下文菜单
 * Context Menu定义在tool behavior provider中。通过覆写方法getContextMenu创建返回context menu。
 */
  @Override
  public IContextMenuEntry[] getContextMenu(ICustomContext context) {
    List<IContextMenuEntry> menuList = new ArrayList<IContextMenuEntry>();

    if (context.getPictogramElements() != null) {
      for (PictogramElement pictogramElement : context.getPictogramElements()) {
        if (getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement) == null) {
          continue;
        }
        Object object = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
        if (object instanceof Pool) {
          ContextMenuEntry subMenuDelete = new ContextMenuEntry(new DeletePoolFeature(getFeatureProvider()), context);
          subMenuDelete.setText("Delete pool"); //$NON-NLS-1$
          subMenuDelete.setSubmenu(false);
          menuList.add(subMenuDelete);
        } else if (object instanceof CallActivity) {
          final CallActivity ca = (CallActivity) object;
          final String calledElement = ca.getCalledElement();

          if (calledElement != null && StringUtils.isNotBlank(calledElement)
                  && ActivitiWorkspaceUtil.getDiagramDataFilesByProcessId(calledElement).size() == 1) {
            final ContextMenuEntry openCalledElement
              = new ContextMenuEntry(new OpenCalledElementForCallActivity(getFeatureProvider()), context);
            openCalledElement.setText("Open Process '" + calledElement + "' in new diagram");
            openCalledElement.setSubmenu(false);
            menuList.add(openCalledElement);
          }
        }
      }
    }
    return menuList.toArray(new IContextMenuEntry[menuList.size()]);
  }
/**
 * 1.4.2 Creating a Palette
 * 在tool behavior provider中调用getPalette方法创建并返回一个palette。
 * 
 * DefaultToolBehaviorProvider类中已经默认实现了两个compartment，一个是放connection的，一个是放置用户的creature feature。
 */
  @Override
  public IPaletteCompartmentEntry[] getPalette() {

    final IProject project = ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram());

    final List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

    // add compartments from super class if not disabled
    IPaletteCompartmentEntry[] superCompartments = super.getPalette();//这里调用ActivitiBPMNFeatureProvider.getCreateFeatures()创建组件

    // create new compartments
    IPaletteCompartmentEntry startEventCompartmentEntry = new PaletteCompartmentEntry("Start event", null);
    IPaletteCompartmentEntry endEventCompartmentEntry = new PaletteCompartmentEntry("End event", null);
    IPaletteCompartmentEntry taskCompartmentEntry = new PaletteCompartmentEntry("Task", null);
    IPaletteCompartmentEntry gatewayCompartmentEntry = new PaletteCompartmentEntry("Gateway", null);
    IPaletteCompartmentEntry containerCompartmentEntry = new PaletteCompartmentEntry("Container", null);
    IPaletteCompartmentEntry boundaryEventCompartmentEntry = new PaletteCompartmentEntry("Boundary event", null);
    IPaletteCompartmentEntry intermediateEventCompartmentEntry = new PaletteCompartmentEntry("Intermediate event", null);
    IPaletteCompartmentEntry artifactsCompartmentEntry = new PaletteCompartmentEntry("Artifacts", null);
    IPaletteCompartmentEntry connectionCompartmentEntry = new PaletteCompartmentEntry("Connection", null);
    IPaletteCompartmentEntry alfrescoCompartmentEntry = new PaletteCompartmentEntry("Alfresco", PluginImage.IMG_ALFRESCO_LOGO.getImageKey());

    for (final IPaletteCompartmentEntry entry : superCompartments) {

      // Prune any disabled palette entries in the Objects compartment
      if ("Objects".equals(entry.getLabel())) {
        pruneDisabledPaletteEntries(project, entry);
      }
    }

    for (IPaletteCompartmentEntry iPaletteCompartmentEntry : superCompartments) {
      final List<IToolEntry> toolEntries = iPaletteCompartmentEntry.getToolEntries();

      for (IToolEntry toolEntry : toolEntries) {
        if ("sequenceflow".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("messageflow".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("association".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("startevent".equalsIgnoreCase(toolEntry.getLabel())) {
          startEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timerstartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          startEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorstartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          startEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("messagestartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          startEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("signalstartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          startEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("endevent".equalsIgnoreCase(toolEntry.getLabel())) {
          endEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorendevent".equalsIgnoreCase(toolEntry.getLabel())) {
          endEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("terminateendevent".equalsIgnoreCase(toolEntry.getLabel())) {
          endEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("cancelendevent".equalsIgnoreCase(toolEntry.getLabel())) {
          endEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("usertask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("scripttask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("servicetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("mailtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("manualtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("receivetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("businessruletask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timerboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("signalboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("messageboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("cancelboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("compensationboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timercatchingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("signalcatchingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("messagecatchingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("signalthrowingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("compensationthrowingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("nonethrowingevent".equalsIgnoreCase(toolEntry.getLabel())) {
          intermediateEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("parallelgateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("exclusivegateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("inclusivegateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("eventgateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("subprocess".equalsIgnoreCase(toolEntry.getLabel())) {
          containerCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("pool".equalsIgnoreCase(toolEntry.getLabel())) {
          containerCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("lane".equalsIgnoreCase(toolEntry.getLabel())) {
          containerCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("eventsubprocess".equalsIgnoreCase(toolEntry.getLabel())) {
          containerCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("transaction".equalsIgnoreCase(toolEntry.getLabel())) {
          containerCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("callactivity".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescousertask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescostartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescoscripttask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescomailtask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("annotation".equalsIgnoreCase(toolEntry.getLabel())) {
          artifactsCompartmentEntry.getToolEntries().add(toolEntry);
        }
      }
    }
    
    if (startEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(startEventCompartmentEntry);
    }
    if (endEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(endEventCompartmentEntry);
    }
    if (taskCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(taskCompartmentEntry);
    }
    if (containerCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(containerCompartmentEntry);
    }
    if (gatewayCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(gatewayCompartmentEntry);
    }
    if (boundaryEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(boundaryEventCompartmentEntry);
    }
    if (intermediateEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(intermediateEventCompartmentEntry);
    }
    if (artifactsCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(artifactsCompartmentEntry);
    }
    
    // Always add the connection compartment
    ret.add(connectionCompartmentEntry);
    
    if (PreferencesUtil.getBooleanPreference(Preferences.ALFRESCO_ENABLE, ActivitiPlugin.getDefault()) && 
        alfrescoCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(alfrescoCompartmentEntry);
    }
//----test code--arno---
//    IJavaProject javaProject = null;
//    IPackageFragment[] fragments= null;
//    try {
//    	javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
//		fragments = javaProject.getPackageFragments();
//	} catch (CoreException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    System.out.println(fragments);
     /**
      * 这个是ui和实现分离的，所以UI依赖与实现工程（xxx.jar）,需要从依赖的工程里拿到实现类
      */
    addCustomServiceTasks(project, ret);
    addCustomUserTasks(project, ret);

    return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
  }
  
  protected void addCustomServiceTasks(IProject project, List<IPaletteCompartmentEntry> ret) {
    final Map<String, List<CustomServiceTaskContext>> tasksInDrawers = new HashMap<String, List<CustomServiceTaskContext>>();

    final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil.getCustomServiceTaskContexts(project);

    // Graphiti sets the diagram type prover id with || in front of the image key
    String prefixId = getDiagramTypeProvider().getProviderId() + "||";
    @SuppressWarnings("restriction")
    final ImageRegistry reg = GraphitiUIPlugin.getDefault().getImageRegistry();
    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      try {
        if (reg.get(prefixId + taskContext.getSmallImageKey()) == null) {
          reg.put(prefixId + taskContext.getSmallImageKey(), 
              new Image(Display.getCurrent(), taskContext.getSmallIconStream()));
        }
        if (reg.get(prefixId + taskContext.getLargeImageKey()) == null) {
          reg.put(prefixId + taskContext.getLargeImageKey(),
                  new Image(Display.getCurrent(), taskContext.getLargeIconStream()));
        }
        if (reg.get(prefixId + taskContext.getShapeImageKey()) == null) {
          reg.put(prefixId + taskContext.getShapeImageKey(),
                  new Image(Display.getCurrent(), taskContext.getShapeIconStream()));
        }
      } catch (Exception e) {
        Logger.logError("Error loading image", e);
      }
    }

    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      if (!tasksInDrawers.containsKey(taskContext.getServiceTask().contributeToPaletteDrawer())) {
        tasksInDrawers.put(taskContext.getServiceTask().contributeToPaletteDrawer(), new ArrayList<CustomServiceTaskContext>());
      }
      tasksInDrawers.get(taskContext.getServiceTask().contributeToPaletteDrawer()).add(taskContext);
    }

    for (final Entry<String, List<CustomServiceTaskContext>> drawer : tasksInDrawers.entrySet()) {

      // Sort the list
      Collections.sort(drawer.getValue());

      final IPaletteCompartmentEntry paletteCompartmentEntry = new PaletteCompartmentEntry(drawer.getKey(), null);

      String defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, ActivitiPlugin.getDefault());
      for (final CustomServiceTaskContext currentDrawerItem : drawer.getValue()) {
        
        String name = null;
        if (StringUtils.isNotEmpty(defaultLanguage)) {
          TaskNames taskNames = currentDrawerItem.getServiceTask().getClass().getAnnotation(TaskNames.class);
          if (taskNames != null && taskNames.value() != null) {
            for (TaskName taskName : taskNames.value()) {
              if (taskName.locale().equalsIgnoreCase(defaultLanguage)) {
                name = taskName.name();
              }
            }
          }
        }
        
        if (StringUtils.isEmpty(name)) {
          name = currentDrawerItem.getServiceTask().getName();
        }
        
        final CreateCustomServiceTaskFeature feature = new CreateCustomServiceTaskFeature(getFeatureProvider(), name,
                currentDrawerItem.getServiceTask().getDescription(), currentDrawerItem.getServiceTask().getClass().getCanonicalName());
        
        final IToolEntry entry = new ObjectCreationToolEntry(name, currentDrawerItem.getServiceTask().getDescription(),
              currentDrawerItem.getSmallImageKey(), currentDrawerItem.getSmallImageKey(), feature);
        paletteCompartmentEntry.getToolEntries().add(entry);
      }
      ret.add(paletteCompartmentEntry);
    }
  }
  
  protected void addCustomUserTasks(IProject project, List<IPaletteCompartmentEntry> ret) {
    final Map<String, List<CustomUserTaskContext>> tasksInDrawers = new HashMap<String, List<CustomUserTaskContext>>();

    final List<CustomUserTaskContext> customUserTaskContexts = ExtensionUtil.getCustomUserTaskContexts(project);

    // Graphiti sets the diagram type prover id with || in front of the image key
    String prefixId = getDiagramTypeProvider().getProviderId() + "||";
    @SuppressWarnings("restriction")
    final ImageRegistry reg = GraphitiUIPlugin.getDefault().getImageRegistry();
    for (final CustomUserTaskContext taskContext : customUserTaskContexts) {
      try {
        if (reg.get(prefixId + taskContext.getSmallImageKey()) == null) {
          reg.put(prefixId + taskContext.getSmallImageKey(), 
              new Image(Display.getCurrent(), taskContext.getSmallIconStream()));
        }
        if (reg.get(prefixId + taskContext.getLargeImageKey()) == null) {
          reg.put(prefixId + taskContext.getLargeImageKey(),
                  new Image(Display.getCurrent(), taskContext.getLargeIconStream()));
        }
        if (reg.get(prefixId + taskContext.getShapeImageKey()) == null) {
          reg.put(prefixId + taskContext.getShapeImageKey(),
                  new Image(Display.getCurrent(), taskContext.getShapeIconStream()));
        }
      } catch (Exception e) {
        Logger.logError("Error loading image", e);
      }
    }

    for (final CustomUserTaskContext taskContext : customUserTaskContexts) {
      if (!tasksInDrawers.containsKey(taskContext.getUserTask().contributeToPaletteDrawer())) {
        tasksInDrawers.put(taskContext.getUserTask().contributeToPaletteDrawer(), new ArrayList<CustomUserTaskContext>());
      }
      tasksInDrawers.get(taskContext.getUserTask().contributeToPaletteDrawer()).add(taskContext);
    }

    for (final Entry<String, List<CustomUserTaskContext>> drawer : tasksInDrawers.entrySet()) {

      // Sort the list
      Collections.sort(drawer.getValue());

      final IPaletteCompartmentEntry paletteCompartmentEntry = new PaletteCompartmentEntry(drawer.getKey(), null);

      String defaultLanguage = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_DEFAULT_LANGUAGE, ActivitiPlugin.getDefault());
      for (final CustomUserTaskContext currentDrawerItem : drawer.getValue()) {
        
        String name = null;
        if (StringUtils.isNotEmpty(defaultLanguage)) {
          TaskNames taskNames = currentDrawerItem.getUserTask().getClass().getAnnotation(TaskNames.class);
          if (taskNames != null && taskNames.value() != null) {
            for (TaskName taskName : taskNames.value()) {
              if (taskName.locale().equalsIgnoreCase(defaultLanguage)) {
                name = taskName.name();
              }
            }
          }
        }
        
        if (StringUtils.isEmpty(name)) {
          name = currentDrawerItem.getUserTask().getName();
        }
        
        final CreateCustomUserTaskFeature feature = new CreateCustomUserTaskFeature(getFeatureProvider(), name,
                currentDrawerItem.getUserTask().getDescription(), currentDrawerItem.getUserTask().getClass().getCanonicalName());
        
        final IToolEntry entry = new ObjectCreationToolEntry(name, currentDrawerItem.getUserTask().getDescription(),
              currentDrawerItem.getSmallImageKey(), currentDrawerItem.getSmallImageKey(), feature);
        paletteCompartmentEntry.getToolEntries().add(entry);
      }
      ret.add(paletteCompartmentEntry);
    }
  }

  /**
   * Prunes the disabled palette entries from the
   * {@link IPaletteCompartmentEntry}.
   *
   * @param entry
   *          the compartment being pruned
   */
  private void pruneDisabledPaletteEntries(final IProject project, final IPaletteCompartmentEntry entry) {

    final Set<PaletteEntry> disabledPaletteEntries = ExtensionUtil.getDisabledPaletteEntries(project);

    if (!disabledPaletteEntries.isEmpty()) {

      final Iterator<IToolEntry> entryIterator = entry.getToolEntries().iterator();

      while (entryIterator.hasNext()) {

        final IToolEntry toolEntry = entryIterator.next();

        if (disabledPaletteEntries.contains(PaletteEntry.ALL)) {
          entryIterator.remove();
        } else {
          if (toolEntry instanceof ObjectCreationToolEntry) {
            final ObjectCreationToolEntry objToolEntry = (ObjectCreationToolEntry) toolEntry;
            if (toolMapping.containsKey(objToolEntry.getCreateFeature().getClass())) {
              if (disabledPaletteEntries.contains(toolMapping.get(objToolEntry.getCreateFeature().getClass()))) {
                entryIterator.remove();
              }
            }
          }
        }
      }
    }
  }

  private boolean subProcessDiagramExists(SubProcess subProcess) {
    Resource resource = getDiagramTypeProvider().getDiagram().eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    if (uriTrimmed.isPlatformResource()) {

      String platformString = uriTrimmed.toPlatformString(true);

      IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

      if (fileResource != null) {
        IProject project = fileResource.getProject();
        final String parentDiagramName = uriTrimmed.trimFileExtension().lastSegment();

        IFile file = project.getFile(String.format(ActivitiConstants.DIAGRAM_FOLDER + "%s.%s" + ActivitiConstants.DATA_FILE_EXTENSION,
                parentDiagramName, subProcess.getId()));

        Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(file, new ResourceSetImpl());

        return diagram != null;
      }
    }
    // Safe default assumption
    return true;
  }
  
  //method for open call activity called element
  public void openCallActivityCalledElement(ICustomContext context) {
    if (context.getPictogramElements() != null) {//Gets the pictogram elements
      for (PictogramElement pictogramElement : context.getPictogramElements()) {
        if (getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement) == null) {
          continue;
        }
        /**
         * 1、嵌入子流程
				子流程元素<subProcess>内嵌在主流程元素<process>之内，只能在该流程中使用该子流程，外部是无法访问到的。这种子流程一般作为局部通用逻辑处理，或者因为特定业务需要，使得比较复杂的单个主流程设计清晰直观；
		   2、调用子流程
			        首先实现一个流程，在另一个流程中可以调用该流程，通常可以定义一些通用的流程作为这种调用子流程，供其他多个流程定义复用。这种子流程使用<callActivity>元素来进行调用，间接地嵌入到主流程中，用起来比较方便
         */
        Object object = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
        if (object instanceof CallActivity) {//在editor中双击task中的Call activity控件
          final CallActivity ca = (CallActivity) object;
          final String calledElement = ca.getCalledElement();
          if (calledElement != null && StringUtils.isNotBlank(calledElement)
              && ActivitiWorkspaceUtil.getDiagramDataFilesByProcessId(calledElement).size() == 1) {
            
            OpenCalledElementForCallActivity openCalledElement = new OpenCalledElementForCallActivity(getFeatureProvider());
            openCalledElement.execute(context);
          }
        }
      }
    }
  }
}
