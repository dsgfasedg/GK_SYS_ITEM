package com.gk.zhixue.user.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.user.dto.*;
import com.gk.zhixue.user.entity.User;
import com.gk.zhixue.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: {}", request.getPhone());
        LoginResponse response = request.getUnionid() != null 
            ? userService.wechatLogin(request) 
            : userService.phoneLogin(request);
        return Result.success(response);
    }

    @GetMapping("/user/profile")
    public Result<UserProfileResponse> getProfile() {
        log.info("获取用户信息: {}", SecurityUtils.getCurrentUserId());
        UserProfileResponse profile = userService.getProfile();
        return Result.success(profile);
    }

    @GetMapping("/user/ai-quota")
    public Result<AiQuotaResponse> getAiQuota() {
        log.info("获取AI额度信息: {}", SecurityUtils.getCurrentUserId());
        AiQuotaResponse quota = userService.getAiQuota();
        return Result.success(quota);
    }

    @PostMapping("/user/check-membership")
    public Result<String> checkMembershipStatus() {
        log.info("检查会员状态: {}", SecurityUtils.getCurrentUserId());
        userService.checkMembershipStatus();
        return Result.success("会员状态检查完成");
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常运行");
    }

    // === 会员升级 ===
    @PostMapping("/user/upgrade")
    public Result<Map<String, Object>> upgrade(@RequestParam int months) {
        log.info("会员升级: userId={}, months={}", SecurityUtils.getCurrentUserId(), months);
        UserService.UpgradeResult result = userService.upgradeMembership(months);

        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("membershipType", result.membershipType);
        data.put("membershipExpire", result.membershipExpire.toString());
        data.put("amount", result.amount);
        data.put("message", "升级成功，到期时间: " + result.membershipExpire.toString());
        return Result.success(data);
    }

    @GetMapping("/vip-info")
    public Result<Map<String, Object>> vipInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserEntity(userId);
        Map<String, Object> info = new java.util.LinkedHashMap<>();
        info.put("membershipType", user.getMembershipType());
        info.put("membershipExpire", user.getMembershipExpire());
        info.put("isVip", "vip".equals(user.getMembershipType()));

        // VIP 权益说明
        java.util.List<Map<String, Object>> benefits = new java.util.ArrayList<>();
        benefits.add(Map.of("name", "AI智能规划", "free", "5次/月", "vip", "不限次"));
        benefits.add(Map.of("name", "申论批改", "free", "2次/月", "vip", "30次/月"));
        benefits.add(Map.of("name", "智能笔记", "free", "基础版", "vip", "完整版"));
        benefits.add(Map.of("name", "错题本", "free", "基础版", "vip", "完整版"));
        benefits.add(Map.of("name", "价格", "free", "免费", "vip", "¥29.9/月"));
        info.put("benefits", benefits);
        return Result.success(info);
    }
}