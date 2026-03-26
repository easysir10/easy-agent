package com.example.aiagent.agentmemory;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * 会话中的一条消息。
 */
@Value
@Builder
public class ConversationMessage {
    String role;
    String content;
    Instant createdAt;
}
