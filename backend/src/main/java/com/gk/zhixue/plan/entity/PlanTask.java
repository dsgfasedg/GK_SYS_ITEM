package com.gk.zhixue.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@TableName("plan_tasks")
public class PlanTask {

    @TableId(type = IdType.AUTO)
    private Long taskId;
    private Long userId;
    private String title;
    private String subject;
    private LocalDate plannedDate;
    private LocalTime plannedStartTime;
    private Integer plannedDuration = 0;
    private Integer actualDuration = 0;
    private String status = "pending";
    private Integer delayCount = 0;
    private String source = "manual";
    private String imageUrl;
    private Integer sortOrder = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlanTask() {}

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }
    public LocalTime getPlannedStartTime() { return plannedStartTime; }
    public void setPlannedStartTime(LocalTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }
    public Integer getPlannedDuration() { return plannedDuration; }
    public void setPlannedDuration(Integer plannedDuration) { this.plannedDuration = plannedDuration; }
    public Integer getActualDuration() { return actualDuration; }
    public void setActualDuration(Integer actualDuration) { this.actualDuration = actualDuration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getDelayCount() { return delayCount; }
    public void setDelayCount(Integer delayCount) { this.delayCount = delayCount; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
