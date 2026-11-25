package com.gk.zhixue.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.Duration;

@Component
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.deepseek.model-name:deepseek-chat}")
    private String modelName;

    @Value("${ai.deepseek.max-tokens:4096}")
    private int maxTokens;

    @Value("${ai.deepseek.temperature:0.1}")
    private double temperature;

    @Value("${ai.deepseek.retry-count:3}")
    private int retryCount;

    @Value("${ai.deepseek.timeout-ms:30000}")
    private int timeoutMs;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 调用 DeepSeek API，返回带 token 统计的结果
     */
    public ChatResult chat(String systemPrompt, String userPrompt) {
        String apiUrl = baseUrl + "/v1/chat/completions";

        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                ObjectNode body = mapper.createObjectNode();
                body.put("model", modelName);
                body.put("max_tokens", maxTokens);
                body.put("temperature", temperature);

                ArrayNode messages = body.putArray("messages");
                ObjectNode sysMsg = messages.addObject();
                sysMsg.put("role", "system");
                sysMsg.put("content", systemPrompt);

                ObjectNode userMsg = messages.addObject();
                userMsg.put("role", "user");
                userMsg.put("content", userPrompt);

                String jsonBody = mapper.writeValueAsString(body);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .timeout(Duration.ofMillis(timeoutMs))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonNode root = mapper.readTree(response.body());

                    // 解析 token 用量
                    JsonNode usage = root.path("usage");
                    int inputTokens = usage.path("prompt_tokens").asInt(0);
                    int outputTokens = usage.path("completion_tokens").asInt(0);

                    // 解析返回内容
                    String content = root.path("choices").get(0).path("message").path("content").asText();
                    if (content != null && !content.isEmpty()) {
                        log.info("DeepSeek API 调用成功: input={}tokens, output={}tokens, cost=¥{}",
                                inputTokens, outputTokens,
                                BigDecimal.valueOf(inputTokens).multiply(BigDecimal.valueOf(0.00014))
                                        .add(BigDecimal.valueOf(outputTokens).multiply(BigDecimal.valueOf(0.00028)))
                                        .divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP));
                        return new ChatResult(content, inputTokens, outputTokens);
                    }
                } else {
                    String err = String.format("DeepSeek API error (attempt %d/%d): status=%d, body=%s",
                            attempt, retryCount, response.statusCode(), response.body());
                    log.warn(err);
                    if (attempt == retryCount) return new ChatResult(err);
                }
            } catch (Exception e) {
                String err = String.format("DeepSeek API call failed (attempt %d/%d): %s",
                        attempt, retryCount, e.getMessage());
                log.warn(err);
                if (attempt == retryCount) return new ChatResult(err);
            }

            if (attempt < retryCount) {
                try { Thread.sleep(1000L * attempt); } catch (InterruptedException ignored) {}
            }
        }
        return new ChatResult("所有重试均失败");
    }
}
