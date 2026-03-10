package com.example.aiagent.agenttools;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
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

    @Tool(description = "根据城市查询天气信息")
    public String queryWeather(String city) {
        if (StrUtil.isBlank(city)) {
            return "请输入城市名称，例如：北京。";
        }
        return StrUtil.format("{}天气：{}", city, WEATHER_DATA.getOrDefault(city, "暂无数据，建议接入真实天气 API"));
    }
}
