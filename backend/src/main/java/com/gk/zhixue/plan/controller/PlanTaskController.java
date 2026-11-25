package com.gk.zhixue.plan.controller;

import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.plan.dto.*;
import com.gk.zhixue.plan.service.PlanTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plans")
public class PlanTaskController {

    private static final Logger log = LoggerFactory.getLogger(PlanTaskController.class);

    private final PlanTaskService planTaskService;

    public PlanTaskController(PlanTaskService planTaskService) {
        this.planTaskService = planTaskService;
    }

    @PostMapping("/tasks")
    public Result<PlanTaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        log.info("创建计划任务: {}", request.getTitle());
        PlanTaskResponse response = planTaskService.createTask(request);
        return Result.success(response);
    }

    @GetMapping("/tasks")
    public Result<?> getTasks(
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "day") String view,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        if ("record".equals(view) && startDate != null && endDate != null) {
            return Result.success(planTaskService.getTaskRecords(
                    LocalDate.parse(startDate), LocalDate.parse(endDate)));
        }
        if ("day".equals(view)) {
            LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            return Result.success(planTaskService.getDayView(queryDate));
        } else if ("week".equals(view)) {
            return Result.success(planTaskService.getWeekView());
        }
        throw BusinessException.parameterError("不支持的视图类型");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(
            @RequestParam(required = false, defaultValue = "week") String period) {
        Map<String, Object> stats = planTaskService.getCompletionStats(period);
        return Result.success(stats);
    }

    @PatchMapping("/tasks/{taskId}")
    public Result<PlanTaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request) {
        log.info("更新计划任务: {}", taskId);
        PlanTaskResponse response = planTaskService.updateTask(taskId, request);
        return Result.success(response);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public Result<String> completeTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) Integer actualDuration) {
        planTaskService.completeTask(taskId, actualDuration);
        return Result.success("任务已完成");
    }

    @PostMapping("/tasks/{taskId}/delay")
    public Result<String> delayTask(
            @PathVariable Long taskId,
            @RequestParam String delayType) {
        planTaskService.delayTask(taskId, delayType);
        return Result.success("任务已延迟");
    }

    @DeleteMapping("/tasks/{taskId}")
    public Result<String> deleteTask(@PathVariable Long taskId) {
        planTaskService.deleteTask(taskId);
        return Result.success("任务已删除");
    }

    @PostMapping("/confirm-tasks")
    public Result<String> confirmTasks(
            @RequestBody List<CreateTaskRequest> requests,
            @RequestParam String imageUrl) {
        planTaskService.batchSaveTasks(requests, imageUrl);
        return Result.success("计划任务已保存");
    }

    // === AI 规划 ===
    @PostMapping("/ai-generate")
    public Result<List<CreateTaskRequest>> aiGenerate(@RequestBody Map<String, String> body) {
        String situation = body.get("situation");
        log.info("AI生成计划: {}", situation);
        List<CreateTaskRequest> plans = planTaskService.aiGeneratePlan(situation);
        return Result.success(plans);
    }

    @PostMapping("/ai-import")
    public Result<String> aiImport(@RequestBody List<CreateTaskRequest> requests) {
        log.info("AI计划一键导入: {} 条", requests.size());
        planTaskService.batchSaveTasks(requests, "ai_generated");
        return Result.success("计划已导入");
    }
}
