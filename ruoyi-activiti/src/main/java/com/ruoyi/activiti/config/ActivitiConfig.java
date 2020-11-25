package com.ruoyi.activiti.config;

import com.ruoyi.activiti.listener.MyActEventListener;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Autowired
    private ICustomProcessDiagramGenerator customProcessDiagramGenerator;

    /**
     * 解決工作流生成图片乱码问题
     *
     * @param processEngineConfiguration
     */
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setProcessDiagramGenerator(customProcessDiagramGenerator);
        //添加事件监听器
        List<ActivitiEventListener> eventListeners = new ArrayList<>();
        eventListeners.add(new MyActEventListener());
        processEngineConfiguration.setEventListeners(eventListeners);
    }
}
