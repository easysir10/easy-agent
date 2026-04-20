package com.example.aiagent.agentapi;

import com.example.aiagent.agentcore.SessionService;
import com.example.aiagent.agentmemory.ConversationMessage;
import com.example.aiagent.agentmemory.ConversationSession;
import com.example.aiagent.common.TraceIdFilter;
import com.example.aiagent.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SessionController.class)
@Import({GlobalExceptionHandler.class, TraceIdFilter.class})
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @Test
    void shouldCreateSession() throws Exception {
        ConversationSession session = ConversationSession.builder()
                .sessionId("s-1")
                .userId("u1001")
                .title("日志排查")
                .messageCount(0)
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2025-01-01T00:00:00Z"))
                .build();
        when(sessionService.create(any())).thenReturn(session);

        mockMvc.perform(post("/agent/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"u1001","title":"日志排查"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.session.sessionId").value("s-1"))
                .andExpect(jsonPath("$.data.session.title").value("日志排查"));
    }

    @Test
    void shouldQueryMessagesAndDeleteSession() throws Exception {
        when(sessionService.getMessages(eq("u1001"), eq("s-1"))).thenReturn(List.of(
                ConversationMessage.builder()
                        .role("user")
                        .content("hello")
                        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                        .build()
        ));
        doNothing().when(sessionService).delete("u1001", "s-1");

        mockMvc.perform(get("/agent/sessions/s-1/messages").queryParam("userId", "u1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].role").value("user"))
                .andExpect(jsonPath("$.data[0].content").value("hello"));

        mockMvc.perform(delete("/agent/sessions/s-1").queryParam("userId", "u1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
