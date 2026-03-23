package com.example.aiagent.agenttools;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aiagent.agentcore.ToolExecutionRecorder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 天气工具（演示版，静态数据）。
 */
@Component
public class WeatherTool {

    private static final Map<String, String> WEATHER_DATA = MapUtil.builder(new java.util.HashMap<String, String>())
            .put("北京", "晴，12~22℃，东北风2级")
            .put("上海", "多云，16~24℃，东南风3级")
            .put("深圳", "小雨，20~28℃，南风2级")
            .build();

    private final ToolExecutionRecorder toolExecutionRecorder;

    public WeatherTool(ToolExecutionRecorder toolExecutionRecorder) {
        this.toolExecutionRecorder = toolExecutionRecorder;
    }

    @Tool(description = "根据城市查询天气信息")
    public String queryWeather(String city) {
        long start = System.currentTimeMillis();
        String result;
        if (StrUtil.isBlank(city)) {
            result = "请输入城市名称，例如：北京。";
        } else {
            result = StrUtil.format("{}天气：{}", city, WEATHER_DATA.getOrDefault(city, "暂无数据，建议接入真实天气 API"));
        }
        toolExecutionRecorder.record("WeatherTool", result, true, System.currentTimeMillis() - start);
        return result;
    }
}
