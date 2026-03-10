package com.example.aiagent.agenttools;

import cn.hutool.core.util.StrUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * SQL 生成工具（演示版）。
 */
@Component
public class SqlGenerateTool {

    @Tool(description = "根据自然语言描述生成 SQL")
    public String generateSql(String requirement) {
        if (StrUtil.isBlank(requirement)) {
            return "请提供查询需求，例如：查询最近7天已支付订单。";
        }
        return StrUtil.format("""
                需求：{}
                示例 SQL：
                SELECT id, user_id, total_amount, status, created_at
                FROM orders
                WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                  AND status = 'PAID'
                ORDER BY created_at DESC;
                """, requirement);
    }
}
