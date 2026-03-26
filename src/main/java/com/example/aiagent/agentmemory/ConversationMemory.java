package com.example.aiagent.agentmemory;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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
        if (StrUtil.isBlank(sessionId)) {
            return createSession(userId, null);
        }

        String normalizedUserId = normalizeUserId(userId);
        return sessions.computeIfAbsent(sessionId, key -> {
            Instant now = Instant.now();
            memoryStore.putIfAbsent(key, new ArrayList<>());
            return ConversationSession.builder()
                    .sessionId(key)
                    .userId(normalizedUserId)
                    .title(buildDefaultTitle(key, null))
                    .messageCount(0)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        });
    }

    public ConversationSession getSession(String sessionId) {
        return requireSession(sessionId);
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
        ConversationSession session = requireSession(sessionId);
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

    public ConversationSession clearSession(String sessionId) {
        ConversationSession session = requireSession(sessionId);
        memoryStore.put(sessionId, new ArrayList<>());
        ConversationSession updated = session.toBuilder()
                .messageCount(0)
                .updatedAt(Instant.now())
                .build();
        sessions.put(sessionId, updated);
        return updated;
    }

    public void deleteSession(String sessionId) {
        requireSession(sessionId);
        sessions.remove(sessionId);
        memoryStore.remove(sessionId);
    }

    public int sessionCount() {
        return sessions.size();
    }

    private List<ConversationMessage> getStoredMessages(String sessionId) {
        requireSession(sessionId);
        return memoryStore.getOrDefault(sessionId, new ArrayList<>());
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

    private ConversationSession requireSession(String sessionId) {
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
            return title;
        }
        String suffix = StrUtil.maxLength(sessionId, 8);
        return DEFAULT_TITLE_PREFIX + suffix;
    }
}
