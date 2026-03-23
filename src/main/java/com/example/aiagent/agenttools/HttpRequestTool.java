package com.example.aiagent.agenttools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.aiagent.agentcore.ToolExecutionRecorder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * HTTP 请求工具（基于 Hutool HttpRequest）。
 */
@Component
public class HttpRequestTool {

    private final ToolExecutionRecorder toolExecutionRecorder;

    public HttpRequestTool(ToolExecutionRecorder toolExecutionRecorder) {
        this.toolExecutionRecorder = toolExecutionRecorder;
    }

    @Tool(description = "发送 HTTP GET 请求并返回响应结果")
    public String doGet(String url) {
        long start = System.currentTimeMillis();
        String result;
        boolean success = true;
        if (StrUtil.isBlank(url)) {
            result = "url 不能为空";
            success = false;
        } else {
            try (HttpResponse response = HttpRequest.get(url)
                    .timeout(5000)
                    .execute()) {
                String body = StrUtil.maxLength(response.body(), 500);
                result = StrUtil.format("status={}, body={}", response.getStatus(), body);
            } catch (Exception e) {
                success = false;
                result = "HTTP 请求失败: " + e.getMessage();
            }
        }
        toolExecutionRecorder.record("HttpRequestTool", result, success, System.currentTimeMillis() - start);
        return result;
    }
}
