package com.ruoyi.activiti.controller;


import com.ruoyi.activiti.component.ActJumpNode;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ruoyi.common.core.domain.AjaxResult.success;

@RestController
@RequestMapping("/act")
public class ActJumpNodeController {

    @Autowired
    ActJumpNode actJumpNode;


    @GetMapping("/jump/upOneNode/taskId")
    public AjaxResult rollbackOneNode(@PathVariable("taskId")String taskId){
        actJumpNode.rollbackOneNode(taskId);
        return success("回退到上一个任务成功");
    }

    @GetMapping("/jump/upToFirstNode/taskId")
    public AjaxResult rollbackToFirstNode(@PathVariable("taskId")String taskId){
        actJumpNode.rollbackToFirstNode(taskId);
        return success("回退到第一个任务成功");
    }

    @GetMapping("/jump/jumpToEndNode/taskId")
    public AjaxResult jumpToEndNode(@PathVariable("taskId")String taskId){
        actJumpNode.jumpToEndNode(taskId);
        return success("流程成功结束");
    }

    @GetMapping("/jump/finishProcess/taskId")
    public AjaxResult finishProcess(@PathVariable("taskId")String taskId){
        actJumpNode.finishProcess(taskId);
        return success("流程成功结束");
    }

}
