package com.ruoyi.activiti.component;


import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ActJumpNode {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ManagementService managementService;

    @Autowired
    HistoryService historyService;

    public void jumpNode(String taskId, String targetFlowElementId) {
        // 获取当前任务
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId());
        // 获取流程定义
        Process process = bpmnModel.getMainProcess();
        // 获取目标节点定义  e.g. flowElementId = "sid-C24BA4F5-F744-4DD7-8D51-03C3698044D2"
        FlowNode targetNode = (FlowNode) process.getFlowElement(targetFlowElementId);
        // 删除当前运行任务，同时返回执行id，该id在并发情况下也是唯一的
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        // 流程执行到来源节点
        managementService.executeCommand(new SetFLowNodeAndGoCmd(targetNode, executionEntityId));
    }

    public void rollbackOneNode(String taskId){ //退回到上一个任务节点
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        HistoricTaskInstance historicTaskInstance = queryUpOneNodeMessage(currentTask.getProcessInstanceId());
        jumpNode(taskId,historicTaskInstance.getTaskDefinitionKey());
    }

    public void rollbackToFirstNode(String taskId){ //退回到第一个任务节点
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        HistoricTaskInstance historicTaskInstance = queryFirstNodeMessage(currentTask.getProcessInstanceId());
        jumpNode(taskId,historicTaskInstance.getTaskDefinitionKey());
    }

    public void jumpToEndNode(String taskId){ //通过跳到结束节点终止流程
        jumpNode(taskId,getActEndNodeId(taskId));
    }

    public void finishProcess(String processInstanceId){ //通过删除流程实例终止流程
        runtimeService.deleteProcessInstance(processInstanceId,"审批不通过");
    }

    public String getActEndNodeId(String taskId){ //获取流程定义的结束节点
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId());
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        //获取endEvent节点
        FlowElement flowElem = flowElements.stream().filter(flowElement ->
                flowElement instanceof EndEvent).findFirst().get();
        return flowElem.getId();
    }

    /**
     * 根据流程实例id获取上一个节点的信息(非并行网关版, 并行网关版需要使用act_hi_actinst获取数据并过滤掉网关节点)
     * act_hi_taskinst表中只记录userTask, act_hi_actinst记录流程流转过的所有节点
     * @param proInsId
     * @return
     */
    public HistoricTaskInstance queryUpOneNodeMessage(String proInsId) {
        //上一个节点
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(proInsId)
                .orderByHistoricTaskInstanceEndTime()
                .desc()    //根据EndTime降序,获取时间最大的即为上一个节点
                .list();
        HistoricTaskInstance taskInstance = null;
        if (!list.isEmpty()) {
            if (list.get(0).getEndTime() != null) {
                taskInstance = list.get(0);
            }
        }
        return taskInstance;
    }

    /**
     * 根据流程实例id第一个节点的信息
     *  act_hi_taskinst表中只记录userTask, act_hi_actinst记录流程流转过的所有节点
     * @param proInsId
     * @return
     */
    public HistoricTaskInstance queryFirstNodeMessage(String proInsId) {
        //上一个节点
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(proInsId)
                .orderByTaskCreateTime()
                .asc()    //根据StartTime升序,获取时间最小的即为第一个节点
                .list();
        HistoricTaskInstance taskInstance = null;
        if (!list.isEmpty()) {
            if (list.get(0).getStartTime() != null) {
                taskInstance = list.get(0);
            }
        }
        return taskInstance;
    }
}