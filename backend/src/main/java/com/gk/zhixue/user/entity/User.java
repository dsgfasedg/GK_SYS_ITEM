package com.gk.zhixue.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long userId;
    private String phone;
    private String wechatUnionid;
    private String nickname;
    private String avatarUrl;
    private String membershipType = "free";
    private LocalDateTime membershipExpire;
    private Integer monthlyAiCallsUsed = 0;
    private Integer monthlyEssayUsed = 0;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWechatUnionid() { return wechatUnionid; }
    public void setWechatUnionid(String wechatUnionid) { this.wechatUnionid = wechatUnionid; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
