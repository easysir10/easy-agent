package com.example.aiagent.agentmemory;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * 会话元信息。
 */
@Value
@Builder(toBuilder = true)
public class ConversationSession {
    String sessionId;
    String userId;
    String title;
    int messageCount;
    Instant createdAt;
    Instant updatedAt;
}
