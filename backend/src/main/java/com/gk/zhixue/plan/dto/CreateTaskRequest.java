package com.gk.zhixue.plan.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateTaskRequest {
    private String title;
    private String subject;
    private LocalDate plannedDate;
    private LocalTime plannedStartTime;
    private Integer plannedDuration;
    private String imageUrl;

    public CreateTaskRequest() {}

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
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
