package com.example.aiagent.agenttools;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 日志分析工具：快速识别常见 Java 异常。
 */
@Component
public class LogAnalysisTool {

    @Tool(description = "分析 Java 错误日志并给出可能原因")
    public String analyze(String logText) {
        String text = StrUtil.nullToEmpty(logText);
        String lower = text.toLowerCase();

        if (ReUtil.contains("nullpointerexception", lower)) {
            return "检测到 NullPointerException：排查对象是否为 null、Bean 注入是否成功、外部返回值是否判空。";
        }
        if (ReUtil.contains("outofmemoryerror", lower)) {
            return "检测到 OutOfMemoryError：排查堆大小参数、缓存增长、线程泄漏与大对象分配。";
        }
        if (ReUtil.contains("sqlsyntaxerrorexception|bad sql grammar", lower)) {
            return "检测到 SQL 语法问题：检查 SQL 关键字、字段名、表名及数据库方言。";
        }
        if (ReUtil.contains("connection refused|connect timed out", lower)) {
            return "检测到连接异常：检查目标服务可达性、端口、防火墙及连接池配置。";
        }
        return "未识别出典型异常模式。建议先定位第一条 Caused by，再回溯调用链与入参。";
    }
}
