package com.gk.zhixue.note.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("knowledge_tree")
public class KnowledgeTree {

    private Long nodeId;
    private Long parentId;
    private String name;
    private Integer level;
    private Integer sortOrder = 0;

    public KnowledgeTree() {}

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
