package com.gk.zhixue.plan.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateTaskRequest {
    private String title;
    private String subject;
    private LocalDate plannedDate;
    private LocalTime plannedStartTime;
    private Integer plannedDuration;
    private Integer actualDuration;
    private String status;

    public UpdateTaskRequest() {}

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
}
