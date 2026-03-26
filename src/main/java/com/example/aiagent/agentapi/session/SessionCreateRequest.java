package com.example.aiagent.agentapi.session;

import lombok.Data;

@Data
public class SessionCreateRequest {
    private String userId;
    private String title;
}
