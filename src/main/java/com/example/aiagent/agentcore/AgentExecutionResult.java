package com.example.aiagent.agentcore;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class AgentExecutionResult {
    String finalReply;
    List<String> toolsUsed;
}
