package com.gk.zhixue.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.note.dto.NoteDto;
import com.gk.zhixue.note.entity.KnowledgeTree;
import com.gk.zhixue.note.entity.Note;
import com.gk.zhixue.note.mapper.KnowledgeTreeMapper;
import com.gk.zhixue.note.mapper.NoteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteMapper noteMapper;
    private final KnowledgeTreeMapper knowledgeTreeMapper;

    public NoteService(NoteMapper noteMapper, KnowledgeTreeMapper knowledgeTreeMapper) {
        this.noteMapper = noteMapper;
        this.knowledgeTreeMapper = knowledgeTreeMapper;
    }

    @Transactional
    public NoteDto createNote(String type, String rawContent) {
        Long userId = SecurityUtils.getCurrentUserId();
        Note note = new Note();
        note.setUserId(userId);
        note.setType(type);
        note.setRawContent(rawContent);
        noteMapper.insert(note);
        return toDto(note);
    }

    public NoteDto getNoteDetail(Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) throw BusinessException.notFound("笔记不存在");
        return toDto(note);
    }

    public List<NoteDto> listNotes(int page, int size, Long knowledgeNodeId, String keyword) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId)
                .orderByDesc(Note::getCreatedAt);

        if (knowledgeNodeId != null) wrapper.eq(Note::getKnowledgeNodeId, knowledgeNodeId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Note::getRawContent, keyword).or().like(Note::getFormattedContent, keyword));
        }

        Page<Note> p = noteMapper.selectPage(new Page<>(page, size), wrapper);
        return p.getRecords().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public NoteDto updateNote(Long noteId, String formattedContent, String tags) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) throw BusinessException.notFound("笔记不存在");
        if (formattedContent != null) note.setFormattedContent(formattedContent);
        if (tags != null) note.setTags(tags);
        noteMapper.updateById(note);
        return toDto(note);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        noteMapper.deleteById(noteId);
    }

    // Knowledge Tree
    public List<Map<String, Object>> getKnowledgeTree() {
        List<KnowledgeTree> allNodes = knowledgeTreeMapper.selectList(null);
        Map<Long, List<KnowledgeTree>> childrenMap = allNodes.stream()
                .filter(n -> n.getParentId() != null)
                .collect(Collectors.groupingBy(KnowledgeTree::getParentId));

        return allNodes.stream()
                .filter(n -> n.getParentId() == null)
                .sorted(Comparator.comparingInt(KnowledgeTree::getSortOrder))
                .map(root -> buildTree(root, childrenMap))
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildTree(KnowledgeTree node, Map<Long, List<KnowledgeTree>> childrenMap) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nodeId", node.getNodeId());
        m.put("name", node.getName());
        m.put("level", node.getLevel());

        List<Map<String, Object>> children = childrenMap.getOrDefault(node.getNodeId(), List.of())
                .stream().sorted(Comparator.comparingInt(KnowledgeTree::getSortOrder))
                .map(child -> buildTree(child, childrenMap))
                .collect(Collectors.toList());
        m.put("children", children);

        Long userId = SecurityUtils.getCurrentUserId();
        long noteCount = noteMapper.selectCount(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId).eq(Note::getKnowledgeNodeId, node.getNodeId()));
        m.put("noteCount", noteCount);
        return m;
    }

    private NoteDto toDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setNoteId(note.getNoteId());
        dto.setType(note.getType());
        dto.setRawContent(note.getRawContent());
        dto.setFormattedContent(note.getFormattedContent());
        dto.setTags(note.getTags());
        dto.setSourceFileUrl(note.getSourceFileUrl());
        dto.setKnowledgeNodeId(note.getKnowledgeNodeId());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
