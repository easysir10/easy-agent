package com.example.aiagent.agentapi.session;

import com.example.aiagent.agentmemory.ConversationSession;
import lombok.Builder;
import lombok.Value;

/**
 * 创建会话响应。
 */
@Value
@Builder
public class SessionCreateResponse {
    ConversationSession session;
}
