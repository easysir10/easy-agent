package com.example.aiagent.agentcore;

import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentapi.session.SessionCreateRequest;
import com.example.aiagent.agentmemory.ConversationMemory;
import com.example.aiagent.agentmemory.ConversationMessage;
import com.example.aiagent.agentmemory.ConversationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final String DEFAULT_USER_ID = "default";

    private final ConversationMemory conversationMemory;

    public ConversationSession create(SessionCreateRequest request) {
        return conversationMemory.createSession(normalizeUserId(request.getUserId()), request.getTitle());
    }

    public ConversationSession getOrCreate(String userId, String sessionId) {
        return conversationMemory.getOrCreateSession(normalizeUserId(userId), sessionId);
    }

    public List<ConversationSession> list(String userId) {
        return conversationMemory.listSessions(normalizeUserId(userId));
    }

    public ConversationSession get(String userId, String sessionId) {
        return conversationMemory.getSession(normalizeUserId(userId), sessionId);
    }

    public List<ConversationMessage> getMessages(String userId, String sessionId) {
        conversationMemory.getSession(normalizeUserId(userId), sessionId);
        return conversationMemory.getMessageViews(sessionId);
    }

    public ConversationSession clear(String userId, String sessionId) {
        return conversationMemory.clearSession(normalizeUserId(userId), sessionId);
    }

    public void delete(String userId, String sessionId) {
        conversationMemory.deleteSession(normalizeUserId(userId), sessionId);
    }

    private String normalizeUserId(String userId) {
        return StrUtil.blankToDefault(userId, DEFAULT_USER_ID);
    }
}
