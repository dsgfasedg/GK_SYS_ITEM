package com.gk.zhixue.user.dto;

import java.time.LocalDateTime;

public class LoginResponse {
    private String token;
    private Long userId;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String membershipType;
    private LocalDateTime membershipExpire;

    public LoginResponse() {}
    public LoginResponse(String token, Long userId, String phone, String nickname, String avatarUrl,
                         String membershipType, LocalDateTime membershipExpire) {
        this.token = token; this.userId = userId; this.phone = phone; this.nickname = nickname;
        this.avatarUrl = avatarUrl; this.membershipType = membershipType; this.membershipExpire = membershipExpire;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
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
}
