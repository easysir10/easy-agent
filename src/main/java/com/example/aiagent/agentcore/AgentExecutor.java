package com.example.aiagent.agentcore;

import cn.hutool.core.util.ReUtil;
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

import java.util.ArrayList;
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

    public AgentExecutionResult execute(String userId, String userInput) {
        List<Message> history = conversationMemory.getMessages(userId);

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

        List<String> toolsUsed = detectTools(userInput);
        conversationMemory.append(userId, new UserMessage(userInput));
        conversationMemory.append(userId, new AssistantMessage(answer));

        return AgentExecutionResult.builder()
                .finalReply(answer)
                .toolsUsed(toolsUsed)
                .build();
    }

    private List<String> detectTools(String userInput) {
        String text = StrUtil.nullToEmpty(userInput).toLowerCase();
        List<String> toolsUsed = new ArrayList<>();

        if (ReUtil.contains("天气|weather|温度", text)) {
            toolsUsed.add("WeatherTool");
        }
        if (ReUtil.contains("日志|error|exception|异常|堆栈", text)) {
            toolsUsed.add("LogAnalysisTool");
        }
        if (ReUtil.contains("sql|查询|表结构|数据库", text)) {
            toolsUsed.add("SqlGenerateTool");
        }
        if (ReUtil.contains("http|接口|api|请求", text)) {
            toolsUsed.add("HttpRequestTool");
        }
        return toolsUsed;
    }
}
