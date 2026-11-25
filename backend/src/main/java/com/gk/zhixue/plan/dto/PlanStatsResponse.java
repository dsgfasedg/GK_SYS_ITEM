package com.gk.zhixue.plan.dto;

public class PlanStatsResponse {
    private String subject;
    private Long total;
    private Long completed;
    private Double completionRate;

    public PlanStatsResponse() {}
    public PlanStatsResponse(String subject, Long total, Long completed, Double completionRate) {
        this.subject = subject; this.total = total; this.completed = completed; this.completionRate = completionRate;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Long getCompleted() { return completed; }
    public void setCompleted(Long completed) { this.completed = completed; }
    public Double getCompletionRate() { return completionRate; }
    public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
}
