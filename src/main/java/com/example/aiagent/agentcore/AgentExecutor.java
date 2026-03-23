package com.example.aiagent.agentcore;

import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentmemory.ConversationMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Agent 执行器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentExecutor {

    private final ChatClient chatClient;
    private final ToolRegistry toolRegistry;
    private final ConversationMemory conversationMemory;
    private final ToolExecutionRecorder toolExecutionRecorder;

    public AgentExecutionResult execute(String userId, String userInput) {
        long start = System.currentTimeMillis();
        List<Message> history = conversationMemory.getMessages(userId);
        toolExecutionRecorder.clear();
        log.info("Start agent execution, userId={}, historySize={}", userId, history.size());

        String answer = chatClient.prompt()
                .messages(history)
                .messages(new SystemMessage("""
                        你是一个工程化 AI Agent。
                        - 优先复用已注册工具解决问题；
                        - 输出结论时给出简洁步骤和建议；
                        - 若信息不足，先指出缺失信息。
                        """))
                .user(userInput)
                .tools(toolRegistry.getToolBeans().toArray())
                .call()
                .content();

        if (StrUtil.isBlank(answer)) {
            answer = "抱歉，我暂时无法给出结果，请稍后重试。";
        }

        List<ToolExecutionResult> toolExecutions = toolExecutionRecorder.snapshot();
        List<String> toolsUsed = toolExecutions.stream()
                .map(ToolExecutionResult::getToolName)
                .distinct()
                .toList();
        long durationMs = System.currentTimeMillis() - start;

        conversationMemory.append(userId, new UserMessage(userInput));
        conversationMemory.append(userId, new AssistantMessage(answer));

        log.info("Finish agent execution, userId={}, toolCount={}, durationMs={}", userId, toolExecutions.size(), durationMs);
        return AgentExecutionResult.builder()
                .finalReply(answer)
                .toolsUsed(toolsUsed)
                .toolExecutions(toolExecutions)
                .durationMs(durationMs)
                .build();
    }
}
