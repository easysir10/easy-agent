# ai-agent-demo

用于学习 AI Agent 架构并可作为面试展示的工程化示例项目。

## 技术栈

- Java 17
- Spring Boot 3
- Spring AI
- Maven
- Lombok
- Hutool（工具层实现）
- OpenAI 兼容接口（OpenAI / DeepSeek）

## 架构说明

执行主链路：

`用户输入 -> AgentController -> AgentService -> AgentExecutor -> ChatClient(大模型) -> Tool 调用 -> 汇总回答 -> 返回`

模块划分：

- `agentapi`：Controller + DTO
- `agentcore`：Agent 核心编排（Service/Executor/ToolRegistry）
- `agenttools`：工具系统（`@Tool`）
- `agentmemory`：会话记忆
- `config`：Spring AI 配置

## 目录结构

```text
src/main/java/com/example/aiagent
├── agentapi
├── agentcore
├── agentmemory
├── agenttools
└── config
```

## 运行

```bash
export OPENAI_API_KEY=sk-xxx
export OPENAI_BASE_URL=https://api.deepseek.com
export OPENAI_MODEL=deepseek-chat
mvn spring-boot:run
```

## 对话接口

- `POST /agent/chat`

请求：

```json
{
  "userId": "u1001",
  "message": "帮我分析这个错误日志：java.lang.NullPointerException"
}
```

`userId` 可选，为空时默认使用 `default`。

## 已实现工具

- `WeatherTool`：城市天气查询（演示版）
- `LogAnalysisTool`：Java 日志异常分析
- `SqlGenerateTool`：自然语言生成 SQL 模板
- `HttpRequestTool`：发送 HTTP GET 请求
