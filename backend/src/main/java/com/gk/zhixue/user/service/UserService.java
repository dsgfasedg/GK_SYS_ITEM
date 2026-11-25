package com.gk.zhixue.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gk.zhixue.ai.mapper.AiUsageLogMapper;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.JwtTokenProvider;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.user.dto.*;
import com.gk.zhixue.user.entity.User;
import com.gk.zhixue.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final AiUsageLogMapper aiUsageLogMapper;

    public UserService(UserMapper userMapper, JwtTokenProvider tokenProvider, AiUsageLogMapper aiUsageLogMapper) {
        this.userMapper = userMapper;
        this.tokenProvider = tokenProvider;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    @Transactional
    public LoginResponse phoneLogin(LoginRequest request) {
        if (request.getPhone() == null || !request.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw BusinessException.parameterError("手机号格式错误");
        }

        if (request.getCode() == null || request.getCode().length() != 6) {
            throw BusinessException.parameterError("验证码错误");
        }

        User user = findByPhone(request.getPhone());
        if (user == null) {
            user = createUserByPhone(request.getPhone());
        }

        user.setLastLogin(LocalDateTime.now());
        userMapper.updateById(user);

        String token = tokenProvider.generateToken(user.getUserId(), user.getPhone(), user.getMembershipType());

        return new LoginResponse(token, user.getUserId(), user.getPhone(), user.getNickname(),
                user.getAvatarUrl(), user.getMembershipType(), user.getMembershipExpire());
    }

    @Transactional
    public LoginResponse wechatLogin(LoginRequest request) {
        if (request.getUnionid() == null) {
            throw BusinessException.parameterError("微信授权失败");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getWechatUnionid, request.getUnionid());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            user = createUserByWechat(request.getUnionid(), request.getNickname(), request.getAvatar());
        }

        user.setLastLogin(LocalDateTime.now());
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatarUrl(request.getAvatar());
        }
        userMapper.updateById(user);

        String token = tokenProvider.generateToken(user.getUserId(), user.getPhone(), user.getMembershipType());

        return new LoginResponse(token, user.getUserId(), user.getPhone(), user.getNickname(),
                user.getAvatarUrl(), user.getMembershipType(), user.getMembershipExpire());
    }

    public UserProfileResponse getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        return new UserProfileResponse(user.getUserId(), user.getPhone(), user.getNickname(), user.getAvatarUrl(),
                user.getMembershipType(), user.getMembershipExpire(), user.getMonthlyAiCallsUsed(),
                user.getMonthlyEssayUsed(), user.getLastLogin());
    }

    public AiQuotaResponse getAiQuota() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        boolean isVip = "vip".equals(user.getMembershipType());
        Integer totalCalls = isVip ? -1 : 5;
        Integer essayTotal = isVip ? 30 : 2;
        Integer essayUsed = user.getMonthlyEssayUsed() != null ? user.getMonthlyEssayUsed() : 0;
        Integer essayRemaining = isVip ? -1 : Math.max(0, essayTotal - essayUsed);

        return new AiQuotaResponse(totalCalls, user.getMonthlyAiCallsUsed(),
                isVip ? -1 : Math.max(0, totalCalls - user.getMonthlyAiCallsUsed()),
                essayTotal, essayUsed, essayRemaining,
                LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
    }

    @Transactional
    public void incrementAiCallUsage(String apiType) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        if ("free".equals(user.getMembershipType())) {
            user.setMonthlyAiCallsUsed(user.getMonthlyAiCallsUsed() != null ? user.getMonthlyAiCallsUsed() + 1 : 1);
            userMapper.updateById(user);
        }
    }

    @Transactional
    public void incrementEssayUsage() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        user.setMonthlyEssayUsed(user.getMonthlyEssayUsed() != null ? user.getMonthlyEssayUsed() + 1 : 1);
        userMapper.updateById(user);
    }

    @Transactional
    public void checkMembershipStatus() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        if (user.getMembershipExpire() != null && user.getMembershipExpire().isBefore(LocalDateTime.now())) {
            user.setMembershipType("free");
            user.setMembershipExpire(null);
            userMapper.updateById(user);
        }
    }

    // === 会员升级 ===
    @Transactional
    public UpgradeResult upgradeMembership(int months) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) throw BusinessException.notFound("用户不存在");

        // ¥29.9/月
        double unitPrice = 29.9;
        double totalAmount = unitPrice * months;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpire;
        if ("vip".equals(user.getMembershipType()) && user.getMembershipExpire() != null
                && user.getMembershipExpire().isAfter(now)) {
            newExpire = user.getMembershipExpire().plusMonths(months);
        } else {
            newExpire = now.plusMonths(months);
        }

        user.setMembershipType("vip");
        user.setMembershipExpire(newExpire);
        userMapper.updateById(user);

        log.info("用户 {} 升级为VIP, 续费{}个月, 到期{}, 金额¥{}", userId, months, newExpire, totalAmount);
        return new UpgradeResult("vip", newExpire, totalAmount);
    }

    public static class UpgradeResult {
        public String membershipType;
        public LocalDateTime membershipExpire;
        public double amount;
        public UpgradeResult(String membershipType, LocalDateTime membershipExpire, double amount) {
            this.membershipType = membershipType;
            this.membershipExpire = membershipExpire;
            this.amount = amount;
        }
    }

    public User getUserEntity(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw BusinessException.notFound("用户不存在");
        return user;
    }

    private User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    private User createUserByPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(7));
        user.setMembershipType("free");
        userMapper.insert(user);
        return user;
    }

    private User createUserByWechat(String unionid, String nickname, String avatar) {
        User user = new User();
        user.setWechatUnionid(unionid);
        user.setNickname(nickname != null ? nickname : "微信用户");
        user.setAvatarUrl(avatar);
        user.setMembershipType("free");
        userMapper.insert(user);
        return user;
    }
}
