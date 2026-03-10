package com.example.aiagent.agenttools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * HTTP 请求工具（基于 Hutool HttpRequest）。
 */
@Component
public class HttpRequestTool {

    @Tool(description = "发送 HTTP GET 请求并返回响应结果")
    public String doGet(String url) {
        if (StrUtil.isBlank(url)) {
            return "url 不能为空";
        }
        try (HttpResponse response = HttpRequest.get(url)
                .timeout(5000)
                .execute()) {
            String body = StrUtil.maxLength(response.body(), 500);
            return StrUtil.format("status={}, body={}", response.getStatus(), body);
        } catch (Exception e) {
            return "HTTP 请求失败: " + e.getMessage();
        }
    }
}
