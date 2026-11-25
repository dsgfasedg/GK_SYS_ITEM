package com.gk.zhixue.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.zhixue.ai.ChatResult;
import com.gk.zhixue.ai.DeepSeekClient;
import com.gk.zhixue.ai.mapper.AiUsageLogMapper;
import com.gk.zhixue.ai.entity.AiUsageLog;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.plan.dto.*;
import com.gk.zhixue.plan.entity.PlanTask;
import com.gk.zhixue.plan.mapper.PlanTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanTaskService {

    private static final Logger log = LoggerFactory.getLogger(PlanTaskService.class);

    private final PlanTaskMapper planTaskMapper;
    private final DeepSeekClient deepSeekClient;
    private final AiUsageLogMapper aiUsageLogMapper;

    public PlanTaskService(PlanTaskMapper planTaskMapper, DeepSeekClient deepSeekClient,
                           AiUsageLogMapper aiUsageLogMapper) {
        this.planTaskMapper = planTaskMapper;
        this.deepSeekClient = deepSeekClient;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    @Transactional
    public PlanTaskResponse createTask(CreateTaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        PlanTask task = new PlanTask();
        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setSubject(request.getSubject());
        task.setPlannedDate(request.getPlannedDate());
        task.setPlannedStartTime(request.getPlannedStartTime());
        task.setPlannedDuration(request.getPlannedDuration() != null ? request.getPlannedDuration() : 60);
        task.setImageUrl(request.getImageUrl());
        task.setStatus("pending");
        task.setSource("manual");
        task.setSortOrder((int) (planTaskMapper.selectCount(null) + 1));
        planTaskMapper.insert(task);

        return toResponse(task);
    }

    public DayViewResponse getDayView(LocalDate date) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<PlanTask> wrapper = new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .eq(PlanTask::getPlannedDate, date)
                .orderByAsc(PlanTask::getSortOrder);
        List<PlanTask> tasks = planTaskMapper.selectList(wrapper);

        return new DayViewResponse(date,
                tasks.stream().map(this::toResponse).collect(Collectors.toList()),
                (long) tasks.size(),
                tasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
    }

    public List<PlanTaskResponse> getTasksByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<PlanTask> wrapper = new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .between(PlanTask::getPlannedDate, startDate, endDate)
                .orderByAsc(PlanTask::getSortOrder);
        return planTaskMapper.selectList(wrapper).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public WeekViewResponse getWeekView() {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<PlanTaskResponse> tasks = getTasksByDateRange(userId, weekStart, weekEnd);

        Map<LocalDate, List<PlanTaskResponse>> byDate = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getPlannedDate() != null ? t.getPlannedDate() : today));

        List<DayViewResponse> days = weekStart.datesUntil(weekEnd.plusDays(1))
                .map(date -> {
                    List<PlanTaskResponse> dayTasks = byDate.getOrDefault(date, List.of());
                    return new DayViewResponse(date, dayTasks, (long) dayTasks.size(),
                            dayTasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
                })
                .collect(Collectors.toList());

        long weekTotal = tasks.size();
        long weekCompleted = tasks.stream().filter(t -> "completed".equals(t.getStatus())).count();
        return new WeekViewResponse(days, weekTotal, weekCompleted,
                weekTotal == 0 ? 0.0 : (double) weekCompleted / weekTotal * 100);
    }

    public List<PlanStatsResponse> getWeekStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<PlanTaskResponse> tasks = getTasksByDateRange(userId, weekStart, weekEnd);

        Map<String, List<PlanTaskResponse>> bySubject = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getSubject() != null ? t.getSubject() : "未分类"));

        return bySubject.entrySet().stream().map(entry -> {
            String subject = entry.getKey();
            List<PlanTaskResponse> list = entry.getValue();
            long total = list.size();
            long completed = list.stream().filter(t -> "completed".equals(t.getStatus())).count();
            return new PlanStatsResponse(subject, total, completed,
                    total == 0 ? 0.0 : (double) completed / total * 100);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void batchSaveTasks(List<CreateTaskRequest> requests, String imageUrl) {
        Long userId = SecurityUtils.getCurrentUserId();
        int sortOrder = (int) (planTaskMapper.selectCount(null) + 1);
        for (CreateTaskRequest request : requests) {
            PlanTask task = new PlanTask();
            task.setUserId(userId);
            task.setTitle(request.getTitle());
            task.setSubject(request.getSubject());
            task.setPlannedDate(request.getPlannedDate());
            task.setPlannedStartTime(request.getPlannedStartTime());
            task.setPlannedDuration(request.getPlannedDuration() != null ? request.getPlannedDuration() : 60);
            task.setImageUrl(imageUrl);
            task.setStatus("pending");
            task.setSource("image_import");
            task.setSortOrder(sortOrder++);
            planTaskMapper.insert(task);
        }
    }

    @Transactional
    public PlanTaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlanTask task = planTaskMapper.selectById(taskId);
        if (task == null) throw BusinessException.notFound("任务不存在");
        if (!task.getUserId().equals(userId)) throw BusinessException.forbidden("无权修改此任务");

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getSubject() != null) task.setSubject(request.getSubject());
        if (request.getPlannedDate() != null) task.setPlannedDate(request.getPlannedDate());
        if (request.getPlannedStartTime() != null) task.setPlannedStartTime(request.getPlannedStartTime());
        if (request.getPlannedDuration() != null) task.setPlannedDuration(request.getPlannedDuration());
        if (request.getActualDuration() != null) task.setActualDuration(request.getActualDuration());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if ("completed".equals(request.getStatus()) && (task.getActualDuration() == null || task.getActualDuration() == 0)) {
                task.setActualDuration(task.getPlannedDuration());
            }
        }
        planTaskMapper.updateById(task);
        return toResponse(task);
    }

    @Transactional
    public void completeTask(Long taskId, Integer actualDuration) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlanTask task = planTaskMapper.selectById(taskId);
        if (task == null) throw BusinessException.notFound("任务不存在");
        if (!task.getUserId().equals(userId)) throw BusinessException.forbidden("无权修改此任务");
        task.setStatus("completed");
        task.setActualDuration(actualDuration != null ? actualDuration : task.getPlannedDuration());
        planTaskMapper.updateById(task);
    }

    @Transactional
    public void delayTask(Long taskId, String delayType) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlanTask task = planTaskMapper.selectById(taskId);
        if (task == null) throw BusinessException.notFound("任务不存在");
        if (!task.getUserId().equals(userId)) throw BusinessException.forbidden("无权修改此任务");

        LocalDate newDate = task.getPlannedDate();
        if ("tomorrow".equals(delayType)) newDate = task.getPlannedDate().plusDays(1);
        else if ("week".equals(delayType)) newDate = task.getPlannedDate().plusWeeks(1);

        task.setPlannedDate(newDate);
        task.setStatus("delayed");
        task.setDelayCount(task.getDelayCount() + 1);
        planTaskMapper.updateById(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        PlanTask task = planTaskMapper.selectById(taskId);
        if (task == null) throw BusinessException.notFound("任务不存在");
        if (!task.getUserId().equals(userId)) throw BusinessException.forbidden("无权删除此任务");
        planTaskMapper.deleteById(taskId);
    }

    // === 任务记录（流水） ===
    public Map<String, Object> getTaskRecords(LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<PlanTask> wrapper = new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .between(PlanTask::getPlannedDate, startDate, endDate)
                .orderByDesc(PlanTask::getPlannedDate)
                .orderByAsc(PlanTask::getSortOrder);
        List<PlanTask> tasks = planTaskMapper.selectList(wrapper);

        // Group by date
        Map<LocalDate, List<PlanTaskResponse>> byDate = tasks.stream()
                .collect(Collectors.groupingBy(PlanTask::getPlannedDate,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toResponse, Collectors.toList())));

        // Stats
        long total = tasks.size();
        long completed = tasks.stream().filter(t -> "completed".equals(t.getStatus())).count();
        long delayed = tasks.stream().filter(t -> "delayed".equals(t.getStatus())).count();
        long pending = tasks.stream().filter(t -> "pending".equals(t.getStatus())).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("delayed", delayed);
        result.put("pending", pending);
        result.put("records", byDate);
        return result;
    }

    // === 完成情况统计 ===
    public Map<String, Object> getCompletionStats(String period) {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LocalDate startDate, endDate;

        if ("month".equals(period)) {
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
        } else if ("week".equals(period)) {
            startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
            endDate = startDate.plusDays(6);
        } else {
            int weekOffset = Integer.parseInt(period.replace("week", ""));
            startDate = today.plusWeeks(weekOffset).minusDays(today.getDayOfWeek().getValue() - 1);
            endDate = startDate.plusDays(6);
        }

        LambdaQueryWrapper<PlanTask> wrapper = new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .between(PlanTask::getPlannedDate, startDate, endDate)
                .orderByAsc(PlanTask::getPlannedDate);
        List<PlanTask> tasks = planTaskMapper.selectList(wrapper);

        // Daily stats for bar chart
        Map<LocalDate, List<PlanTask>> byDate = tasks.stream()
                .collect(Collectors.groupingBy(PlanTask::getPlannedDate, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> dailyStats = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            List<PlanTask> dayTasks = byDate.getOrDefault(cursor, List.of());
            Map<String, Object> dayStat = new LinkedHashMap<>();
            dayStat.put("date", cursor.toString());
            dayStat.put("total", dayTasks.size());
            dayStat.put("completed", dayTasks.stream().filter(t -> "completed".equals(t.getStatus())).count());
            dayStat.put("pending", dayTasks.stream().filter(t -> "pending".equals(t.getStatus())).count());
            dayStat.put("delayed", dayTasks.stream().filter(t -> "delayed".equals(t.getStatus())).count());
            dailyStats.add(dayStat);
            cursor = cursor.plusDays(1);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", startDate.toString());
        result.put("endDate", endDate.toString());
        result.put("dailyStats", dailyStats);
        return result;
    }

    // === AI 规划生成（接入 DeepSeek 大模型） ===
    public List<CreateTaskRequest> aiGeneratePlan(String situation) {
        log.info("AI规划输入: {}", situation);

        // 1. Try DeepSeek API first
        String systemPrompt = "你是一个公考备考规划专家。根据用户描述的备考情况，生成个性化学习计划。"
                + "要求：1) 返回纯JSON数组，不要多余文字；"
                + "2) 每条计划包含 title(任务标题)、subject(科目:言语理解/判断推理/数量关系/资料分析/常识判断/申论/综合)、"
                + "plannedDate(日期YYYY-MM-DD)、plannedStartTime(开始时间HH:mm)、plannedDuration(时长分钟)；"
                + "3) 生成5-8条计划，合理分配时间，每天学习时间不超过用户说的可用时间；"
                + "4) 今天的任务从 plannedDate 设为今天(" + LocalDate.now() + ")，跨天任务依次排；"
                + "5) 任务标题要具体可执行，如'行测-言语理解 中心理解真题30道'";

        String userPrompt = "我的情况：" + situation;

        try {
            ChatResult aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 记录 AI 调用日志
            AiUsageLog logEntry = new AiUsageLog();
            logEntry.setUserId(SecurityUtils.getCurrentUserId());
            logEntry.setApiType("plan_ocr");
            logEntry.setInputTokens(aiResponse.getInputTokens());
            logEntry.setOutputTokens(aiResponse.getOutputTokens());
            logEntry.setCost(aiResponse.getCost());
            logEntry.setStatus(aiResponse.isSuccess() ? "success" : "failed");
            logEntry.setErrorMsg(aiResponse.getErrorMsg());
            logEntry.setCreatedAt(LocalDateTime.now());
            aiUsageLogMapper.insert(logEntry);

            if (aiResponse.isSuccess() && aiResponse.getContent() != null) {
                // Extract JSON array from response
                String jsonStr = aiResponse.getContent();
                int startIdx = jsonStr.indexOf('[');
                int endIdx = jsonStr.lastIndexOf(']');
                if (startIdx >= 0 && endIdx > startIdx) {
                    jsonStr = jsonStr.substring(startIdx, endIdx + 1);
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode arr = mapper.readTree(jsonStr);
                if (arr.isArray() && arr.size() > 0) {
                    List<CreateTaskRequest> plans = new ArrayList<>();
                    for (JsonNode node : arr) {
                        CreateTaskRequest req = new CreateTaskRequest();
                        req.setTitle(node.path("title").asText("学习任务"));
                        req.setSubject(node.path("subject").asText("综合"));
                        req.setPlannedDate(LocalDate.parse(node.path("plannedDate").asText(LocalDate.now().toString())));
                        String timeStr = node.path("plannedStartTime").asText("09:00");
                        req.setPlannedStartTime(LocalTime.parse(timeStr.length() > 5 ? timeStr.substring(0, 5) : timeStr));
                        req.setPlannedDuration(node.path("plannedDuration").asInt(60));
                        plans.add(req);
                    }
                    if (!plans.isEmpty()) {
                        log.info("AI规划成功: 生成 {} 条计划", plans.size());
                        return plans;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("AI规划API调用失败，使用模板回退: {}", e.getMessage());
        }

        // 2. Fallback: template-based
        return fallbackPlan(situation);
    }

    /** 关键词模板回退 */
    private List<CreateTaskRequest> fallbackPlan(String situation) {
        List<CreateTaskRequest> plans = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (situation.contains("冲刺") || situation.contains("快") || situation.contains("急")) {
            plans.add(makePlan("行测-言语理解 真题限时训练", "言语理解", today, LocalTime.of(8, 0), 60));
            plans.add(makePlan("行测-判断推理 专项突破", "判断推理", today, LocalTime.of(9, 30), 90));
            plans.add(makePlan("行测-资料分析 速算训练", "资料分析", today, LocalTime.of(11, 0), 60));
            plans.add(makePlan("行测-数量关系 核心题型", "数量关系", today, LocalTime.of(14, 0), 60));
            plans.add(makePlan("行测-常识判断 时政热点", "常识判断", today, LocalTime.of(15, 30), 30));
            plans.add(makePlan("申论-真题写作训练", "申论", today.plusDays(1), LocalTime.of(8, 0), 120));
        } else if (situation.contains("基础") || situation.contains("薄弱") || situation.contains("刚")) {
            plans.add(makePlan("行测-言语理解 基础知识点梳理", "言语理解", today, LocalTime.of(9, 0), 60));
            plans.add(makePlan("行测-判断推理 基础题型练习", "判断推理", today, LocalTime.of(10, 30), 60));
            plans.add(makePlan("行测-资料分析 公式记忆", "资料分析", today, LocalTime.of(14, 0), 45));
            plans.add(makePlan("行测-数量关系 基础运算", "数量关系", today.plusDays(1), LocalTime.of(9, 0), 60));
            plans.add(makePlan("申论-归纳概括 基础训练", "申论", today.plusDays(1), LocalTime.of(14, 0), 60));
        } else if (situation.contains("申论") || situation.contains("写作")) {
            plans.add(makePlan("申论-归纳概括 专项训练", "申论", today, LocalTime.of(8, 0), 60));
            plans.add(makePlan("申论-综合分析 范文精读", "申论", today, LocalTime.of(9, 30), 60));
            plans.add(makePlan("申论-大作文 结构训练", "申论", today, LocalTime.of(14, 0), 90));
            plans.add(makePlan("申论-真题完整作答", "申论", today.plusDays(1), LocalTime.of(8, 0), 120));
        } else if (situation.contains("行测") || situation.contains("做题") || situation.contains("刷题")) {
            plans.add(makePlan("行测-言语理解 40题限时", "言语理解", today, LocalTime.of(8, 0), 40));
            plans.add(makePlan("行测-判断推理 35题限时", "判断推理", today, LocalTime.of(9, 0), 40));
            plans.add(makePlan("行测-资料分析 20题限时", "资料分析", today, LocalTime.of(10, 0), 30));
            plans.add(makePlan("行测-数量关系 15题限时", "数量关系", today, LocalTime.of(11, 0), 30));
            plans.add(makePlan("行测-常识判断 20题", "常识判断", today, LocalTime.of(14, 0), 20));
            plans.add(makePlan("错题复盘整理", "综合", today, LocalTime.of(15, 0), 60));
        } else {
            plans.add(makePlan("行测-言语理解 模块练习", "言语理解", today, LocalTime.of(9, 0), 60));
            plans.add(makePlan("行测-判断推理 模块练习", "判断推理", today, LocalTime.of(10, 30), 60));
            plans.add(makePlan("行测-资料分析 模块练习", "资料分析", today, LocalTime.of(14, 0), 45));
            plans.add(makePlan("申论-素材积累", "申论", today, LocalTime.of(15, 30), 45));
            plans.add(makePlan("错题回顾整理", "综合", today, LocalTime.of(16, 30), 30));
        }
        return plans;
    }

    private CreateTaskRequest makePlan(String title, String subject, LocalDate date, LocalTime time, int duration) {
        CreateTaskRequest req = new CreateTaskRequest();
        req.setTitle(title);
        req.setSubject(subject);
        req.setPlannedDate(date);
        req.setPlannedStartTime(time);
        req.setPlannedDuration(duration);
        return req;
    }

    private PlanTaskResponse toResponse(PlanTask task) {
        return new PlanTaskResponse(
                task.getTaskId(), task.getTitle(), task.getSubject(),
                task.getPlannedDate(), task.getPlannedStartTime(),
                task.getPlannedDuration(), task.getActualDuration(),
                task.getStatus(), task.getDelayCount(),
                task.getSource(), task.getImageUrl());
    }
}
