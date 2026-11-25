package com.gk.zhixue.practice.dto;

import java.time.LocalDateTime;

public class PracticeDto {
    private Long sessionId;
    private String status;
    private Integer totalCount;
    private Integer correctCount;
    private String tagFilter;
    private LocalDateTime createdAt;

    public PracticeDto() {}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }
    public String getTagFilter() { return tagFilter; }
    public void setTagFilter(String tagFilter) { this.tagFilter = tagFilter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
