package com.example.aiagent.agentapi;

import com.example.aiagent.agentapi.session.SessionCreateRequest;
import com.example.aiagent.agentapi.session.SessionCreateResponse;
import com.example.aiagent.agentcore.SessionService;
import com.example.aiagent.agentmemory.ConversationMessage;
import com.example.aiagent.agentmemory.ConversationSession;
import com.example.aiagent.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会话管理接口。
 */
@RestController
@RequestMapping("/agent/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ApiResponse<SessionCreateResponse> create(@RequestBody(required = false) SessionCreateRequest request) {
        SessionCreateRequest actualRequest = request == null ? new SessionCreateRequest() : request;
        ConversationSession session = sessionService.create(actualRequest);
        return ApiResponse.success(SessionCreateResponse.builder().session(session).build());
    }

    @GetMapping
    public ApiResponse<List<ConversationSession>> list(@RequestParam(required = false) String userId) {
        return ApiResponse.success(sessionService.list(userId));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<ConversationSession> get(@PathVariable String sessionId,
                                                @RequestParam(required = false) String userId) {
        return ApiResponse.success(sessionService.get(userId, sessionId));
    }

    @GetMapping("/{sessionId}/messages")
    public ApiResponse<List<ConversationMessage>> messages(@PathVariable String sessionId,
                                                           @RequestParam(required = false) String userId) {
        return ApiResponse.success(sessionService.getMessages(userId, sessionId));
    }

    @PostMapping("/{sessionId}/clear")
    public ApiResponse<ConversationSession> clear(@PathVariable String sessionId,
                                                  @RequestParam(required = false) String userId) {
        return ApiResponse.success(sessionService.clear(userId, sessionId));
    }

    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> delete(@PathVariable String sessionId,
                                    @RequestParam(required = false) String userId) {
        sessionService.delete(userId, sessionId);
        return ApiResponse.success(null);
    }
}
