package com.example.aiagent.agentapi;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentChatRequest {

    /**
     * 用户标识。为空时系统会自动回落到 default 用户。
     */
    private String userId;

    /**
     * 用户输入的自然语言消息。
     */
    @NotBlank(message = "message 不能为空")
    private String message;
}
