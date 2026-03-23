package com.example.aiagent.agentcore;

import com.example.aiagent.agenttools.HttpRequestTool;
import com.example.aiagent.agenttools.LogAnalysisTool;
import com.example.aiagent.agenttools.SqlGenerateTool;
import com.example.aiagent.agenttools.WeatherTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ToolMetadataServiceTest {

    @Test
    void shouldListAnnotatedTools() {
        ToolExecutionRecorder recorder = new ToolExecutionRecorder();
        ToolRegistry registry = new ToolRegistry(
                new WeatherTool(recorder),
                new LogAnalysisTool(recorder),
                new SqlGenerateTool(recorder),
                new HttpRequestTool(recorder)
        );
        ToolMetadataService service = new ToolMetadataService(registry);

        Assertions.assertEquals(4, service.listTools().size());
        Assertions.assertTrue(service.listTools().stream().anyMatch(tool -> tool.getToolName().equals("WeatherTool")));
    }
}
