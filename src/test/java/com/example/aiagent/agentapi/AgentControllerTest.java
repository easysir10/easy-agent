package com.example.aiagent.agentapi;

import com.example.aiagent.agentcore.AgentService;
import com.example.aiagent.common.TraceIdFilter;
import com.example.aiagent.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AgentController.class)
@Import({GlobalExceptionHandler.class, TraceIdFilter.class})
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Test
    void shouldWrapChatResponse() throws Exception {
        when(agentService.chat(any())).thenReturn(AgentChatResponse.builder()
                .userId("u1001")
                .reply("ok")
                .durationMs(12L)
                .build());

        mockMvc.perform(post("/agent/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"u1001","message":"hello"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceIdFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value("u1001"))
                .andExpect(jsonPath("$.data.reply").value("ok"));
    }

    @Test
    void shouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/agent/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("message 不能为空"));
    }
}
