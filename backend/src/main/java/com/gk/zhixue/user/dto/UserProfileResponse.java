package com.gk.zhixue.user.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long userId;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String membershipType;
    private LocalDateTime membershipExpire;
    private Integer monthlyAiCallsUsed;
    private Integer monthlyEssayUsed;
    private LocalDateTime lastLogin;

    public UserProfileResponse() {}
    public UserProfileResponse(Long userId, String phone, String nickname, String avatarUrl,
                                String membershipType, LocalDateTime membershipExpire,
                                Integer monthlyAiCallsUsed, Integer monthlyEssayUsed, LocalDateTime lastLogin) {
        this.userId = userId; this.phone = phone; this.nickname = nickname; this.avatarUrl = avatarUrl;
        this.membershipType = membershipType; this.membershipExpire = membershipExpire;
        this.monthlyAiCallsUsed = monthlyAiCallsUsed; this.monthlyEssayUsed = monthlyEssayUsed; this.lastLogin = lastLogin;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
    public LocalDateTime getMembershipExpire() { return membershipExpire; }
    public void setMembershipExpire(LocalDateTime membershipExpire) { this.membershipExpire = membershipExpire; }
    public Integer getMonthlyAiCallsUsed() { return monthlyAiCallsUsed; }
    public void setMonthlyAiCallsUsed(Integer monthlyAiCallsUsed) { this.monthlyAiCallsUsed = monthlyAiCallsUsed; }
    public Integer getMonthlyEssayUsed() { return monthlyEssayUsed; }
    public void setMonthlyEssayUsed(Integer monthlyEssayUsed) { this.monthlyEssayUsed = monthlyEssayUsed; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
