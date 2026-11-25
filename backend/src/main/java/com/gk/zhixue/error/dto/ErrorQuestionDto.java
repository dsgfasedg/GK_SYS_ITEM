package com.gk.zhixue.error.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ErrorQuestionDto {
    private Long errorId;
    private String stem;
    private String options;
    private String correctAnswer;
    private String userAnswer;
    private String analysis;
    private String knowledgeTags;
    private String userErrorTag;
    private Integer mastery;
    private LocalDate nextReviewDate;
    private Integer reviewCount;
    private String source;
    private String imageUrl;
    private LocalDateTime createdAt;

    public ErrorQuestionDto() {}

    public Long getErrorId() { return errorId; }
    public void setErrorId(Long errorId) { this.errorId = errorId; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public String getKnowledgeTags() { return knowledgeTags; }
    public void setKnowledgeTags(String knowledgeTags) { this.knowledgeTags = knowledgeTags; }
    public String getUserErrorTag() { return userErrorTag; }
    public void setUserErrorTag(String userErrorTag) { this.userErrorTag = userErrorTag; }
    public Integer getMastery() { return mastery; }
    public void setMastery(Integer mastery) { this.mastery = mastery; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
