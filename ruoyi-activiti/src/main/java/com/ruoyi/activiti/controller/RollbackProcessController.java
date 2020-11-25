package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.component.ActJumpNode;
import com.ruoyi.activiti.domain.BizLeaveVo;
import com.ruoyi.activiti.service.IBizLeaveService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/process")
public class RollbackProcessController extends BaseController {
    @Autowired
    private IBizLeaveService bizLeaveService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    ActJumpNode actJumpNode;

    @GetMapping("/rollBackProcess/{type}/{taskId}")
    public String rollBackProcess(@PathVariable("type") int type,
                                  @PathVariable("taskId") String taskId, ModelMap mmap){
        //回退到上一个流程节点
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BizLeaveVo bizLeave = bizLeaveService.selectBizLeaveById(new Long(processInstance.getBusinessKey()));
        mmap.put("bizLeave", bizLeave);
        mmap.put("taskId", taskId);
        mmap.put("type", type);
        return "leave/rollBack";
    }

    @PostMapping("/rollBackProcess/{taskId}")
    @ResponseBody
    public AjaxResult rollBackProcess(@PathVariable("taskId") String taskId){
        //回退到上一个流程节点
        actJumpNode.rollbackOneNode(taskId);
        System.out.println(taskId+"回退到上一个流程节点-成功");
        return success("回退成功");
    }

    @PostMapping("/rollBackProcessFirst/{taskId}")
    @ResponseBody
    public AjaxResult rollBackProcessFirst(@PathVariable("taskId") String taskId){
        //回退到第一个流程节点
        actJumpNode.rollbackToFirstNode(taskId);
        System.out.println(taskId+"回退到第一个流程节点-成功");
        return success("回退成功");
    }

}
