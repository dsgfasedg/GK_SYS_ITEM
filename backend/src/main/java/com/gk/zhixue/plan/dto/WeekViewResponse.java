package com.gk.zhixue.plan.dto;

import java.util.List;

public class WeekViewResponse {
    private List<DayViewResponse> days;
    private Long weekTotal;
    private Long weekCompleted;
    private Double weekCompletionRate;

    public WeekViewResponse() {}
    public WeekViewResponse(List<DayViewResponse> days, Long weekTotal, Long weekCompleted, Double weekCompletionRate) {
        this.days = days; this.weekTotal = weekTotal; this.weekCompleted = weekCompleted; this.weekCompletionRate = weekCompletionRate;
    }

    public List<DayViewResponse> getDays() { return days; }
    public void setDays(List<DayViewResponse> days) { this.days = days; }
    public Long getWeekTotal() { return weekTotal; }
    public void setWeekTotal(Long weekTotal) { this.weekTotal = weekTotal; }
    public Long getWeekCompleted() { return weekCompleted; }
    public void setWeekCompleted(Long weekCompleted) { this.weekCompleted = weekCompleted; }
    public Double getWeekCompletionRate() { return weekCompletionRate; }
    public void setWeekCompletionRate(Double weekCompletionRate) { this.weekCompletionRate = weekCompletionRate; }
}
