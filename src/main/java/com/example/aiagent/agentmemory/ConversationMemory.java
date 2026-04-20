package com.example.aiagent.agentmemory;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aiagent.exception.SessionAccessDeniedException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于内存的会话记忆。
 */
@Component
public class ConversationMemory {

    private static final int MAX_MESSAGES = 20;
    private static final String DEFAULT_USER_ID = "default";
    private static final String DEFAULT_TITLE_PREFIX = "新会话-";

    private final Map<String, List<ConversationMessage>> memoryStore = new ConcurrentHashMap<>();
    private final Map<String, ConversationSession> sessions = new ConcurrentHashMap<>();

    public ConversationSession createSession(String userId, String title) {
        String normalizedUserId = normalizeUserId(userId);
        String sessionId = IdUtil.fastSimpleUUID();
        Instant now = Instant.now();
        ConversationSession session = ConversationSession.builder()
                .sessionId(sessionId)
                .userId(normalizedUserId)
                .title(buildDefaultTitle(sessionId, title))
                .messageCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        sessions.put(sessionId, session);
        memoryStore.put(sessionId, new ArrayList<>());
        return session;
    }

    public ConversationSession getOrCreateSession(String userId, String sessionId) {
        String normalizedUserId = normalizeUserId(userId);
        if (StrUtil.isBlank(sessionId)) {
            return createSession(normalizedUserId, null);
        }

        ConversationSession existing = sessions.get(sessionId);
        if (existing != null) {
            ensureOwner(existing, normalizedUserId);
            return existing;
        }

        Instant now = Instant.now();
        ConversationSession created = ConversationSession.builder()
                .sessionId(sessionId)
                .userId(normalizedUserId)
                .title(buildDefaultTitle(sessionId, null))
                .messageCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        ConversationSession previous = sessions.putIfAbsent(sessionId, created);
        ConversationSession session = previous == null ? created : previous;
        ensureOwner(session, normalizedUserId);
        memoryStore.putIfAbsent(sessionId, new ArrayList<>());
        return session;
    }


    public ConversationSession getSession(String userId, String sessionId) {
        ConversationSession session = getSessionById(sessionId);
        ensureOwner(session, normalizeUserId(userId));
        return session;
    }

    public List<ConversationSession> listSessions(String userId) {
        String normalizedUserId = normalizeUserId(userId);
        return sessions.values().stream()
                .filter(session -> StrUtil.equals(normalizedUserId, session.getUserId()))
                .sorted((left, right) -> right.getUpdatedAt().compareTo(left.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public List<Message> getMessages(String sessionId) {
        return getStoredMessages(sessionId).stream()
                .map(this::toChatMessage)
                .toList();
    }

    public List<ConversationMessage> getMessageViews(String sessionId) {
        return List.copyOf(getStoredMessages(sessionId));
    }

    public void append(String sessionId, Message message) {
        ConversationSession session = getSessionById(sessionId);
        List<ConversationMessage> messages = memoryStore.computeIfAbsent(sessionId, key -> new ArrayList<>());
        messages.add(ConversationMessage.builder()
                .role(resolveRole(message))
                .content(message.getText())
                .createdAt(Instant.now())
                .build());
        if (messages.size() > MAX_MESSAGES) {
            List<ConversationMessage> tail = ListUtil.sub(messages, messages.size() - MAX_MESSAGES, messages.size());
            memoryStore.put(sessionId, new ArrayList<>(tail));
            messages = memoryStore.get(sessionId);
        }
        sessions.put(sessionId, refreshSession(session, messages, message));
    }

    public ConversationSession clearSession(String userId, String sessionId) {
        ConversationSession session = getSession(userId, sessionId);
        memoryStore.put(sessionId, new ArrayList<>());
        ConversationSession updated = session.toBuilder()
                .messageCount(0)
                .updatedAt(Instant.now())
                .build();
        sessions.put(sessionId, updated);
        return updated;
    }

    public void deleteSession(String userId, String sessionId) {
        getSession(userId, sessionId);
        sessions.remove(sessionId);
        memoryStore.remove(sessionId);
    }

    public int sessionCount() {
        return sessions.size();
    }

    private List<ConversationMessage> getStoredMessages(String sessionId) {
        return memoryStore.getOrDefault(getSessionById(sessionId).getSessionId(), new ArrayList<>());
    }

    private ConversationSession refreshSession(ConversationSession session, List<ConversationMessage> messages, Message message) {
        String title = session.getTitle();
        if (shouldRefreshTitle(title) && message instanceof UserMessage) {
            title = StrUtil.maxLength(StrUtil.blankToDefault(message.getText(), title), 24);
        }
        return session.toBuilder()
                .title(title)
                .messageCount(messages.size())
                .updatedAt(Instant.now())
                .build();
    }

    private Message toChatMessage(ConversationMessage message) {
        return switch (message.getRole()) {
            case "assistant" -> new AssistantMessage(message.getContent());
            case "system" -> new SystemMessage(message.getContent());
            default -> new UserMessage(message.getContent());
        };
    }

    private String resolveRole(Message message) {
        if (message instanceof AssistantMessage) {
            return "assistant";
        }
        if (message instanceof SystemMessage) {
            return "system";
        }
        return "user";
    }

    private boolean shouldRefreshTitle(String title) {
        return StrUtil.isBlank(title) || title.startsWith(DEFAULT_TITLE_PREFIX);
    }

    private void ensureOwner(ConversationSession session, String normalizedUserId) {
        if (!StrUtil.equals(session.getUserId(), normalizedUserId)) {
            throw new SessionAccessDeniedException(StrUtil.format("会话 {} 不属于用户 {}", session.getSessionId(), normalizedUserId));
        }
    }

    private ConversationSession getSessionById(String sessionId) {
        ConversationSession session = sessions.get(sessionId);
        if (session == null) {
            throw new NoSuchElementException("会话不存在: " + sessionId);
        }
        return session;
    }

    private String normalizeUserId(String userId) {
        return StrUtil.blankToDefault(userId, DEFAULT_USER_ID);
    }

    private String buildDefaultTitle(String sessionId, String title) {
        if (StrUtil.isNotBlank(title)) {
            return StrUtil.maxLength(title, 24);
        }
        String suffix = StrUtil.maxLength(sessionId, 8);
        return DEFAULT_TITLE_PREFIX + suffix;
    }
}
