package com.example.aiagent.agenttools;

import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentcore.ToolExecutionRecorder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * SQL 生成工具（演示版）。
 */
@Component
public class SqlGenerateTool {

    private final ToolExecutionRecorder toolExecutionRecorder;

    public SqlGenerateTool(ToolExecutionRecorder toolExecutionRecorder) {
        this.toolExecutionRecorder = toolExecutionRecorder;
    }

    @Tool(description = "根据自然语言描述生成 SQL")
    public String generateSql(String requirement) {
        long start = System.currentTimeMillis();
        String result;
        if (StrUtil.isBlank(requirement)) {
            result = "请提供查询需求，例如：查询最近7天已支付订单。";
        } else {
            result = StrUtil.format("""
                    需求：{}
                    示例 SQL：
                    SELECT id, user_id, total_amount, status, created_at
                    FROM orders
                    WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                      AND status = 'PAID'
                    ORDER BY created_at DESC;
                    """, requirement);
        }
        toolExecutionRecorder.record("SqlGenerateTool", result, true, System.currentTimeMillis() - start);
        return result;
    }
}
