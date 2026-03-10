package com.example.aiagent.agentcore;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ToolExecutionResult {
    String toolName;
    String output;
}
