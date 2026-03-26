package com.example.aiagent.agentcore;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于线程上下文的工具执行记录器。
 */
@Component
public class ToolExecutionRecorder {

    private static final ThreadLocal<List<ToolExecutionResult>> RECORDS = ThreadLocal.withInitial(ArrayList::new);

    public void clear() {
        RECORDS.get().clear();
    }

    public void record(String toolName, String output, boolean success, long durationMs) {
        RECORDS.get().add(ToolExecutionResult.builder()
                .toolName(toolName)
                .output(StrUtil.maxLength(StrUtil.nullToEmpty(output), 200))
                .success(success)
                .durationMs(durationMs)
                .build());
    }

    public List<ToolExecutionResult> snapshot() {
        return List.copyOf(RECORDS.get());
    }
}
