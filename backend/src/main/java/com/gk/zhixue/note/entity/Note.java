package com.gk.zhixue.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("notes")
public class Note {

    @TableId(type = IdType.AUTO)
    private Long noteId;
    private Long userId;
    private String type = "text";
    private String rawContent;
    private String formattedContent;
    private String tags;
    private String sourceFileUrl;
    private Long knowledgeNodeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {}

    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public String getFormattedContent() { return formattedContent; }
    public void setFormattedContent(String formattedContent) { this.formattedContent = formattedContent; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getSourceFileUrl() { return sourceFileUrl; }
    public void setSourceFileUrl(String sourceFileUrl) { this.sourceFileUrl = sourceFileUrl; }
    public Long getKnowledgeNodeId() { return knowledgeNodeId; }
    public void setKnowledgeNodeId(Long knowledgeNodeId) { this.knowledgeNodeId = knowledgeNodeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
