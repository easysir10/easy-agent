package com.example.aiagent.agentapi;

import com.example.aiagent.agentcore.ToolDescriptor;
import com.example.aiagent.agentcore.ToolMetadataService;
import com.example.aiagent.agentcore.ToolRegistry;
import com.example.aiagent.common.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统信息接口。
 */
@RestController
@RequestMapping
public class SystemController {

    private final ToolMetadataService toolMetadataService;
    private final ToolRegistry toolRegistry;

    @Value("${spring.application.name:ai-agent-demo}")
    private String applicationName;

    @Value("${spring.ai.openai.chat.options.model:unknown}")
    private String modelName;

    public SystemController(ToolMetadataService toolMetadataService, ToolRegistry toolRegistry) {
        this.toolMetadataService = toolMetadataService;
        this.toolRegistry = toolRegistry;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "UP");
        payload.put("applicationName", applicationName);
        payload.put("modelName", modelName);
        payload.put("toolCount", toolRegistry.getToolBeans().size());
        return ApiResponse.success(payload);
    }

    @GetMapping("/agent/tools")
    public ApiResponse<List<ToolDescriptor>> tools() {
        return ApiResponse.success(toolMetadataService.listTools());
    }

    @GetMapping("/agent/info")
    public ApiResponse<Map<String, Object>> info() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("applicationName", applicationName);
        payload.put("modelName", modelName);
        payload.put("toolCount", toolRegistry.getToolBeans().size());
        payload.put("toolNames", toolMetadataService.listTools().stream().map(ToolDescriptor::getToolName).distinct().toList());
        return ApiResponse.success(payload);
    }
}
