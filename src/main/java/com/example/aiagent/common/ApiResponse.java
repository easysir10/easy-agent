package com.example.aiagent.common;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * 统一 API 响应。
 */
@Value
@Builder
public class ApiResponse<T> {

    int code;
    String message;
    T data;
    String traceId;
    Instant timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(0)
                .message("success")
                .data(data)
                .traceId(TraceIdHolder.getTraceId())
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .traceId(TraceIdHolder.getTraceId())
                .timestamp(Instant.now())
                .build();
    }
}
