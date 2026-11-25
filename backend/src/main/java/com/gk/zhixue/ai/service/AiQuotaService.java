package com.gk.zhixue.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.zhixue.ai.entity.AiUsageLog;
import com.gk.zhixue.ai.mapper.AiUsageLogMapper;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.user.entity.User;
import com.gk.zhixue.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiQuotaService {

    private static final Logger log = LoggerFactory.getLogger(AiQuotaService.class);
    private final UserMapper userMapper;
    private final AiUsageLogMapper aiUsageLogMapper;

    public AiQuotaService(UserMapper userMapper, AiUsageLogMapper aiUsageLogMapper) {
        this.userMapper = userMapper;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    public Map<String, Object> getQuota() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) throw BusinessException.notFound("用户不存在");

        boolean isVip = "vip".equals(user.getMembershipType());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("membershipType", user.getMembershipType());
        result.put("aiCallsQuota", isVip ? -1 : 5);
        result.put("aiCallsUsed", user.getMonthlyAiCallsUsed());
        result.put("essayQuota", isVip ? 30 : 2);
        result.put("essayUsed", user.getMonthlyEssayUsed());
        return result;
    }

    @Transactional
    public void recordCall(String apiType, boolean success) {
        Long userId = SecurityUtils.getCurrentUserId();
        AiUsageLog logEntry = new AiUsageLog();
        logEntry.setUserId(userId);
        logEntry.setApiType(apiType);
        logEntry.setStatus(success ? "success" : "failed");
        logEntry.setCreatedAt(LocalDateTime.now());
        aiUsageLogMapper.insert(logEntry);
    }

    public Map<String, Object> getUsageLogs(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<AiUsageLog> p = aiUsageLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<AiUsageLog>()
                        .eq(AiUsageLog::getUserId, userId)
                        .orderByDesc(AiUsageLog::getCreatedAt));
        List<Map<String, Object>> list = p.getRecords().stream().map(log -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("logId", log.getLogId());
            m.put("apiType", log.getApiType());
            m.put("inputTokens", log.getInputTokens());
            m.put("outputTokens", log.getOutputTokens());
            m.put("cost", log.getCost());
            m.put("status", log.getStatus());
            m.put("errorMsg", log.getErrorMsg());
            m.put("createdAt", log.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        // 汇总统计
        LambdaQueryWrapper<AiUsageLog> countWrapper = new LambdaQueryWrapper<AiUsageLog>()
                .eq(AiUsageLog::getUserId, userId);
        long totalCalls = aiUsageLogMapper.selectCount(countWrapper);
        long successCalls = aiUsageLogMapper.selectCount(
                countWrapper.clone().eq(AiUsageLog::getStatus, "success"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCalls", totalCalls);
        result.put("successCalls", successCalls);
        result.put("records", list);
        return result;
    }
}
