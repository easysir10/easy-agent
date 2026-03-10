package com.example.aiagent;

import com.example.aiagent.agenttools.LogAnalysisTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmokeTest {

    @Test
    void logToolShouldDetectNpe() {
        LogAnalysisTool tool = new LogAnalysisTool();
        String result = tool.analyze("java.lang.NullPointerException at demo");
        Assertions.assertTrue(result.contains("NullPointerException"));
    }
}
