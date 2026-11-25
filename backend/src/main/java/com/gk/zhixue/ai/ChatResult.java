package com.gk.zhixue.ai;

import java.math.BigDecimal;

/**
 * DeepSeek API 调用结果，包含响应内容和 token 消耗
 */
public class ChatResult {
    private final String content;
    private final int inputTokens;
    private final int outputTokens;
    private final BigDecimal cost;
    private final boolean success;
    private final String errorMsg;

    // DeepSeek V4 价格（元/1K tokens），可通过配置调整
    private static final BigDecimal INPUT_PRICE_PER_1K = new BigDecimal("0.00014");   // ¥0.14/1M = ¥0.00014/1K
    private static final BigDecimal OUTPUT_PRICE_PER_1K = new BigDecimal("0.00028");  // ¥0.28/1M = ¥0.00028/1K

    public ChatResult(String content, int inputTokens, int outputTokens) {
        this.content = content;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        // cost = (inputTokens * inputPrice + outputTokens * outputPrice) / 1000
        this.cost = BigDecimal.valueOf(inputTokens).multiply(INPUT_PRICE_PER_1K)
                .add(BigDecimal.valueOf(outputTokens).multiply(OUTPUT_PRICE_PER_1K))
                .setScale(6, BigDecimal.ROUND_HALF_UP);
        this.success = true;
        this.errorMsg = null;
    }

    public ChatResult(String errorMsg) {
        this.content = null;
        this.inputTokens = 0;
        this.outputTokens = 0;
        this.cost = BigDecimal.ZERO;
        this.success = false;
        this.errorMsg = errorMsg;
    }

    public String getContent() { return content; }
    public int getInputTokens() { return inputTokens; }
    public int getOutputTokens() { return outputTokens; }
    public BigDecimal getCost() { return cost; }
    public boolean isSuccess() { return success; }
    public String getErrorMsg() { return errorMsg; }
}
