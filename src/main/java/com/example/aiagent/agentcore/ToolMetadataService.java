package com.example.aiagent.agentcore;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ToolMetadataService {

    private final ToolRegistry toolRegistry;

    public ToolMetadataService(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    public List<ToolDescriptor> listTools() {
        List<ToolDescriptor> descriptors = new ArrayList<>();
        for (Object toolBean : toolRegistry.getToolBeans()) {
            Method[] methods = toolBean.getClass().getMethods();
            for (Method method : methods) {
                Tool tool = method.getAnnotation(Tool.class);
                if (tool == null) {
                    continue;
                }
                descriptors.add(ToolDescriptor.builder()
                        .toolName(toolBean.getClass().getSimpleName())
                        .methodName(method.getName())
                        .description(tool.description())
                        .beanType(toolBean.getClass().getName())
                        .build());
            }
        }
        descriptors.sort(Comparator.comparing(ToolDescriptor::getToolName)
                .thenComparing(ToolDescriptor::getMethodName));
        return descriptors;
    }
}
