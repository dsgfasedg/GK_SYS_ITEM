package com.gk.zhixue.plan.dto;

import java.time.LocalDate;
import java.util.List;

public class DayViewResponse {
    private LocalDate date;
    private List<PlanTaskResponse> tasks;
    private Long totalCount;
    private Long completedCount;

    public DayViewResponse() {}
    public DayViewResponse(LocalDate date, List<PlanTaskResponse> tasks, Long totalCount, Long completedCount) {
        this.date = date; this.tasks = tasks; this.totalCount = totalCount; this.completedCount = completedCount;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public List<PlanTaskResponse> getTasks() { return tasks; }
    public void setTasks(List<PlanTaskResponse> tasks) { this.tasks = tasks; }
    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Long getCompletedCount() { return completedCount; }
    public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
}
