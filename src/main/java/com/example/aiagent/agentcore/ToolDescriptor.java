package com.example.aiagent.agentcore;

import lombok.Builder;
import lombok.Value;

/**
 * 工具元数据。
 */
@Value
@Builder
public class ToolDescriptor {
    String toolName;
    String methodName;
    String description;
    String beanType;
}
