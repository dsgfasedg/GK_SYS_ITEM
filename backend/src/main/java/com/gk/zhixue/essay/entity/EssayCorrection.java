package com.gk.zhixue.essay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("essay_corrections")
public class EssayCorrection {

    @TableId(type = IdType.AUTO)
    private Long correctionId;
    private Long userId;
    private String questionText;
    private String essayText;
    private Integer logicScore;
    private Integer contentScore;
    private Integer languageScore;
    private Integer standardScore;
    private String suggestions;
    private LocalDateTime createdAt;

    public EssayCorrection() {}

    public Long getCorrectionId() { return correctionId; }
    public void setCorrectionId(Long correctionId) { this.correctionId = correctionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getEssayText() { return essayText; }
    public void setEssayText(String essayText) { this.essayText = essayText; }
    public Integer getLogicScore() { return logicScore; }
    public void setLogicScore(Integer logicScore) { this.logicScore = logicScore; }
    public Integer getContentScore() { return contentScore; }
    public void setContentScore(Integer contentScore) { this.contentScore = contentScore; }
    public Integer getLanguageScore() { return languageScore; }
    public void setLanguageScore(Integer languageScore) { this.languageScore = languageScore; }
    public Integer getStandardScore() { return standardScore; }
    public void setStandardScore(Integer standardScore) { this.standardScore = standardScore; }
    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
