package com.gk.zhixue.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long questionId;
    private Long userId;
    private String type = "single_choice";
    private String stem;
    private String options;
    private String correctAnswer;
    private String analysis;
    private String knowledgeTags;
    private String source = "import";
    private String importBatchId;
    private String hashValue;
    private String status = "active";
    private LocalDateTime createdAt;

    public Question() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public String getImportBatchId() { return importBatchId; }
    public void setImportBatchId(String importBatchId) { this.importBatchId = importBatchId; }
    public String getHashValue() { return hashValue; }
    public void setHashValue(String hashValue) { this.hashValue = hashValue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
