package com.gk.zhixue.plan.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class PlanTaskResponse {
    private Long taskId;
    private String title;
    private String subject;
    private LocalDate plannedDate;
    private LocalTime plannedStartTime;
    private Integer plannedDuration;
    private Integer actualDuration;
    private String status;
    private Integer delayCount;
    private String source;
    private String imageUrl;

    public PlanTaskResponse() {}
    public PlanTaskResponse(Long taskId, String title, String subject, LocalDate plannedDate,
                             LocalTime plannedStartTime, Integer plannedDuration, Integer actualDuration,
                             String status, Integer delayCount, String source, String imageUrl) {
        this.taskId = taskId; this.title = title; this.subject = subject; this.plannedDate = plannedDate;
        this.plannedStartTime = plannedStartTime; this.plannedDuration = plannedDuration;
        this.actualDuration = actualDuration; this.status = status; this.delayCount = delayCount;
        this.source = source; this.imageUrl = imageUrl;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
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
}
