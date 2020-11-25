package com.ruoyi.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class MyActEventListener implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent event) {
        System.out.println("=============================================================");
        switch (event.getType()) {

            case ACTIVITY_COMPLETED:
                System.out.println("An activity done:");
                System.out.println("activity execution id: "+ event.getExecutionId());
                break;

            case PROCESS_COMPLETED:
                System.out.println("A Process finished:");
                System.out.println("process definition id: "+ event.getProcessDefinitionId());
                System.out.println("process instance id: "+ event.getProcessInstanceId());
                System.out.println("process execution id: "+ event.getExecutionId());
                break;

            default:
                System.out.println("Event received: " + event.getType());
        }
    }

    @Override
    public boolean isFailOnException() {
        // The logic in the onEvent method of this listener is not critical, exceptions
        // can be ignored if logging fails...
        return false;
    }
    /*<bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
            ...
            <property name="eventListeners">
          <list>
             <bean class="org.activiti.engine.example.MyEventListener" />
          </list>
        </property>
    </bean>*/
}
