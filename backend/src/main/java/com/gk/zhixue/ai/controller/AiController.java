package com.gk.zhixue.ai.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.ai.service.AiQuotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final AiQuotaService aiQuotaService;

    public AiController(AiQuotaService aiQuotaService) { this.aiQuotaService = aiQuotaService; }

    @GetMapping("/quota")
    public Result<Map<String, Object>> getQuota() {
        return Result.success(aiQuotaService.getQuota());
    }

    @GetMapping("/usage-logs")
    public Result<Map<String, Object>> getUsageLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(aiQuotaService.getUsageLogs(page, size));
    }
}
