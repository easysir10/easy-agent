package com.example.aiagent.agentapi;

import com.example.aiagent.agentcore.ToolExecutionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatResponse {

    private String userId;
    private String sessionId;
    private String reply;
    private List<String> toolsUsed;
    private List<ToolExecutionResult> toolExecutions;
    private long durationMs;
}
