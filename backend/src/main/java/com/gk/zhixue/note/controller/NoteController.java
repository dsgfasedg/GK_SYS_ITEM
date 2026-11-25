package com.gk.zhixue.note.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.note.dto.NoteDto;
import com.gk.zhixue.note.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private static final Logger log = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;

    public NoteController(NoteService noteService) { this.noteService = noteService; }

    @PostMapping
    public Result<NoteDto> create(@RequestParam String type, @RequestParam String rawContent) {
        return Result.success(noteService.createNote(type, rawContent));
    }

    @GetMapping("/{noteId}")
    public Result<NoteDto> detail(@PathVariable Long noteId) {
        return Result.success(noteService.getNoteDetail(noteId));
    }

    @GetMapping
    public Result<List<NoteDto>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false) Long knowledgeNodeId,
                                       @RequestParam(required = false) String keyword) {
        return Result.success(noteService.listNotes(page, size, knowledgeNodeId, keyword));
    }

    @PatchMapping("/{noteId}")
    public Result<NoteDto> update(@PathVariable Long noteId,
                                   @RequestParam(required = false) String formattedContent,
                                   @RequestParam(required = false) String tags) {
        return Result.success(noteService.updateNote(noteId, formattedContent, tags));
    }

    @DeleteMapping("/{noteId}")
    public Result<String> delete(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
        return Result.success("已删除");
    }

    @GetMapping("/knowledge-tree")
    public Result<List<Map<String, Object>>> getKnowledgeTree() {
        return Result.success(noteService.getKnowledgeTree());
    }
}
