package com.example.aiagent.agentcore;

import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentapi.AgentChatRequest;
import com.example.aiagent.agentapi.AgentChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentService {

    private static final String DEFAULT_USER_ID = "default";

    private final AgentExecutor agentExecutor;

    public AgentChatResponse chat(AgentChatRequest request) {
        String userId = StrUtil.blankToDefault(request.getUserId(), DEFAULT_USER_ID);
        AgentExecutionResult result = agentExecutor.execute(userId, request.getMessage());
        return AgentChatResponse.builder()
                .userId(userId)
                .reply(result.getFinalReply())
                .toolsUsed(result.getToolsUsed())
                .toolExecutions(result.getToolExecutions())
                .durationMs(result.getDurationMs())
                .build();
    }
}
