package com.gk.zhixue.question.dto;

import java.time.LocalDateTime;

public class QuestionDto {
    private Long questionId;
    private String type;
    private String stem;
    private String options;
    private String correctAnswer;
    private String analysis;
    private String knowledgeTags;
    private String source;
    private String status;
    private LocalDateTime createdAt;

    public QuestionDto() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public String getKnowledgeTags() { return knowledgeTags; }
    public void setKnowledgeTags(String knowledgeTags) { this.knowledgeTags = knowledgeTags; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
