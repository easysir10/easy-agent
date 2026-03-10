package com.example.aiagent.agentmemory;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的会话记忆。
 */
@Component
public class ConversationMemory {

    private static final int MAX_MESSAGES = 20;

    private final Map<String, List<Message>> memoryStore = new ConcurrentHashMap<>();

    public List<Message> getMessages(String userId) {
        String key = normalize(userId);
        List<Message> messages = memoryStore.getOrDefault(key, new ArrayList<>());
        return new ArrayList<>(messages);
    }

    public void append(String userId, Message message) {
        String key = normalize(userId);
        List<Message> messages = memoryStore.computeIfAbsent(key, k -> new ArrayList<>());
        messages.add(message);
        if (messages.size() > MAX_MESSAGES) {
            List<Message> tail = ListUtil.sub(messages, messages.size() - MAX_MESSAGES, messages.size());
            memoryStore.put(key, new ArrayList<>(tail));
        }
    }

    private String normalize(String userId) {
        return StrUtil.blankToDefault(userId, "default");
    }
}
