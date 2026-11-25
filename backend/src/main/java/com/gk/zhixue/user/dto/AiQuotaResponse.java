package com.gk.zhixue.user.dto;

import java.time.LocalDateTime;

public class AiQuotaResponse {
    private Integer totalCalls;
    private Integer usedCalls;
    private Integer remainingCalls;
    private Integer essayTotal;
    private Integer essayUsed;
    private Integer essayRemaining;
    private LocalDateTime resetTime;

    public AiQuotaResponse() {}
    public AiQuotaResponse(Integer totalCalls, Integer usedCalls, Integer remainingCalls, Integer essayTotal,
                           Integer essayUsed, Integer essayRemaining, LocalDateTime resetTime) {
        this.totalCalls = totalCalls; this.usedCalls = usedCalls; this.remainingCalls = remainingCalls;
        this.essayTotal = essayTotal; this.essayUsed = essayUsed; this.essayRemaining = essayRemaining; this.resetTime = resetTime;
    }

    public Integer getTotalCalls() { return totalCalls; }
    public void setTotalCalls(Integer totalCalls) { this.totalCalls = totalCalls; }
    public Integer getUsedCalls() { return usedCalls; }
    public void setUsedCalls(Integer usedCalls) { this.usedCalls = usedCalls; }
    public Integer getRemainingCalls() { return remainingCalls; }
    public void setRemainingCalls(Integer remainingCalls) { this.remainingCalls = remainingCalls; }
    public Integer getEssayTotal() { return essayTotal; }
    public void setEssayTotal(Integer essayTotal) { this.essayTotal = essayTotal; }
    public Integer getEssayUsed() { return essayUsed; }
    public void setEssayUsed(Integer essayUsed) { this.essayUsed = essayUsed; }
    public Integer getEssayRemaining() { return essayRemaining; }
    public void setEssayRemaining(Integer essayRemaining) { this.essayRemaining = essayRemaining; }
    public LocalDateTime getResetTime() { return resetTime; }
    public void setResetTime(LocalDateTime resetTime) { this.resetTime = resetTime; }
}
