package com.example.aiagent.agentcore;

import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentapi.AgentChatRequest;
import com.example.aiagent.agentapi.AgentChatResponse;
import com.example.aiagent.agentmemory.ConversationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentService {

    private static final String DEFAULT_USER_ID = "default";

    private final AgentExecutor agentExecutor;
    private final SessionService sessionService;

    public AgentChatResponse chat(AgentChatRequest request) {
        String userId = StrUtil.blankToDefault(request.getUserId(), DEFAULT_USER_ID);
        ConversationSession session = sessionService.getOrCreate(userId, request.getSessionId());
        AgentExecutionResult result = agentExecutor.execute(userId, session.getSessionId(), request.getMessage());
        return AgentChatResponse.builder()
                .userId(userId)
                .sessionId(session.getSessionId())
                .reply(result.getFinalReply())
                .toolsUsed(result.getToolsUsed())
                .toolExecutions(result.getToolExecutions())
                .durationMs(result.getDurationMs())
                .build();
    }
}
