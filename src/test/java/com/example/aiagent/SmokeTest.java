package com.example.aiagent;

import com.example.aiagent.agentcore.ToolExecutionRecorder;
import com.example.aiagent.agenttools.LogAnalysisTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmokeTest {

    @Test
    void logToolShouldDetectNpe() {
        ToolExecutionRecorder recorder = new ToolExecutionRecorder();
        LogAnalysisTool tool = new LogAnalysisTool(recorder);
        String result = tool.analyze("java.lang.NullPointerException at demo");
        Assertions.assertTrue(result.contains("NullPointerException"));
        Assertions.assertEquals(1, recorder.snapshot().size());
    }
}
