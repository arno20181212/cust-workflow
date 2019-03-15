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

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
/**
 * Diagram
3.1      创建插件工程
3.2      创建Diagram Type Provider类
Diagram是graphiti内置的显示模型的根节点，通过type D标示。

(1)     Diagram Type Provider类实现IDiagramTypeProvider.或继承AbstractDiagramTypeProvider.

(2)      如果为diagram type 创建了类型提供器，而这种diagram type不存在，则需要在plugin.xml中声明该类型的信息。

   <extension

         point="org.eclipse.graphiti.ui.diagramTypes">

      <diagramType

            id="chessdemo.ChessDiagramType"

            name="Diagram Type for Chess Demo"

            type="chessDemo">

      </diagramType>

</extension>

在Graphiti中的“add”是为业务对象（领域模型对象）创建图形表达。哪种业务（business）对象能够添加到特定类型的diagram里是由feature provider发布的 add feature决定的。

Graphiti支持Pictogram links，即领域模型的元素与定义图形化展示的模型（Pictogram model）的元素的链接。每个diagram有一个容器（图形，例如矩形）装载pictogram links。

 * @author Administrator
 *
 */
public class ActivitiBPMNDiagramTypeProvider extends AbstractDiagramTypeProvider {

  private ActivitiNotificationService activitiNotificationService;

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public ActivitiBPMNDiagramTypeProvider() {
		super();
		/**
		 * Features响应客户的操作，处理显示、展示、及连接模型。
		 * 
		 * 1.1      创建Feature Provider类
		 * (1)     从AbstractFeatureProvider/DefaultFeatureProvider扩展出自己的Feature Provider类。该类用于发布（deliver）features.
		 *
		 * (2)     在DagramTypeProvider()创建和设置FeatureProvider。
		 */
		setFeatureProvider(new ActivitiBPMNFeatureProvider(this));
	}
	
	 /**
	   * Description copied from interface: IDiagramTypeProvider
	   * 	Returns the notification service.
	   * Specified by:
	   * 	getNotificationService in interface IDiagramTypeProvider
	   * Returns:
	   * 	the notification service
	   */
  @Override
  public INotificationService getNotificationService() {
    if (activitiNotificationService == null) {
      activitiNotificationService = new ActivitiNotificationService(this);
    }

    return activitiNotificationService;
  }

  @Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
	  /**
	   * 工具行为提供器需要被集成到标准工作台工具中。这一般意味着在工作台现有的编辑概念（concept）上添加功能。
	   * 
	   * (1)      必须实现接口IToolBehaviorProvider，或者继承其子类如DefaultToolBehaviorProvider.。
	   * (2)      覆写方法：到剪贴板。
	   * (3)      在feature provider中覆写getAvailableToolBehaviorProviders发布。
	   */
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new ActivitiToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

  @Override
  public boolean isAutoUpdateAtStartup() {
    return true;
  }


}
