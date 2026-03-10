package com.example.aiagent.agentcore;

import com.example.aiagent.agenttools.HttpRequestTool;
import com.example.aiagent.agenttools.LogAnalysisTool;
import com.example.aiagent.agenttools.SqlGenerateTool;
import com.example.aiagent.agenttools.WeatherTool;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 工具注册中心：统一管理可被 Agent 调用的工具 Bean。
 */
@Component
@Getter
public class ToolRegistry {

    private final List<Object> toolBeans;

    public ToolRegistry(WeatherTool weatherTool,
                        LogAnalysisTool logAnalysisTool,
                        SqlGenerateTool sqlGenerateTool,
                        HttpRequestTool httpRequestTool) {
        this.toolBeans = List.of(weatherTool, logAnalysisTool, sqlGenerateTool, httpRequestTool);
    }
}
