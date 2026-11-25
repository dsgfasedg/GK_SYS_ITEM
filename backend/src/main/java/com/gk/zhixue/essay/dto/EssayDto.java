package com.gk.zhixue.essay.dto;

import java.time.LocalDateTime;

public class EssayDto {
    private Long correctionId;
    private String questionText;
    private String essayText;
    private Integer logicScore;
    private Integer contentScore;
    private Integer languageScore;
    private Integer standardScore;
    private String suggestions;
    private LocalDateTime createdAt;

    public EssayDto() {}

    public Long getCorrectionId() { return correctionId; }
    public void setCorrectionId(Long correctionId) { this.correctionId = correctionId; }
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
