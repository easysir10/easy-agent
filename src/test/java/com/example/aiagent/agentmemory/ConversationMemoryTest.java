package com.example.aiagent.agentmemory;

import com.example.aiagent.exception.SessionAccessDeniedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;

class ConversationMemoryTest {

    @Test
    void shouldCreateSessionAndStoreMessagesBySessionId() {
        ConversationMemory memory = new ConversationMemory();
        ConversationSession session = memory.createSession("u1001", "日志排查");

        memory.append(session.getSessionId(), new UserMessage("hello"));
        memory.append(session.getSessionId(), new AssistantMessage("world"));

        Assertions.assertEquals(1, memory.sessionCount());
        Assertions.assertEquals(2, memory.getMessages(session.getSessionId()).size());
        Assertions.assertEquals(2, memory.getSession("u1001", session.getSessionId()).getMessageCount());
        Assertions.assertEquals("日志排查", memory.getSession("u1001", session.getSessionId()).getTitle());
    }

    @Test
    void shouldClearAndDeleteSession() {
        ConversationMemory memory = new ConversationMemory();
        ConversationSession session = memory.getOrCreateSession("u1001", "s-1");
        memory.append(session.getSessionId(), new UserMessage("hello"));

        ConversationSession cleared = memory.clearSession("u1001", session.getSessionId());
        Assertions.assertEquals(0, cleared.getMessageCount());
        Assertions.assertTrue(memory.getMessageViews(session.getSessionId()).isEmpty());

        memory.deleteSession("u1001", session.getSessionId());
        Assertions.assertEquals(0, memory.sessionCount());
    }

    @Test
    void shouldRejectAccessFromDifferentUser() {
        ConversationMemory memory = new ConversationMemory();
        ConversationSession session = memory.getOrCreateSession("u1001", "s-locked");

        Assertions.assertThrows(SessionAccessDeniedException.class,
                () -> memory.getOrCreateSession("u1002", session.getSessionId()));
        Assertions.assertThrows(SessionAccessDeniedException.class,
                () -> memory.getSession("u1002", session.getSessionId()));
    }
}
