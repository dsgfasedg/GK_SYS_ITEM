package com.gk.zhixue.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("practice_sessions")
public class PracticeSession {

    @TableId(type = IdType.AUTO)
    private Long sessionId;
    private Long userId;
    private String status = "in_progress";
    private Integer totalCount = 0;
    private Integer correctCount = 0;
    private String tagFilter;
    private LocalDateTime createdAt;

    public PracticeSession() {}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
