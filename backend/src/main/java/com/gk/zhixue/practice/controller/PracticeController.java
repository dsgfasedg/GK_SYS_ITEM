package com.gk.zhixue.practice.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.practice.dto.PracticeDto;
import com.gk.zhixue.practice.service.PracticeService;
import com.gk.zhixue.question.entity.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/practice")
public class PracticeController {

    private static final Logger log = LoggerFactory.getLogger(PracticeController.class);
    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) { this.practiceService = practiceService; }

    @PostMapping("/start")
    public Result<PracticeDto> start(@RequestParam(required = false) String tagFilter,
                                      @RequestParam(defaultValue = "10") int count) {
        return Result.success(practiceService.startPractice(tagFilter, count));
    }

    @GetMapping("/{sessionId}/next")
    public Result<Question> next(@PathVariable Long sessionId) {
        return Result.success(practiceService.getNextQuestion(sessionId));
    }

    @PostMapping("/{sessionId}/submit")
    public Result<Map<String, Object>> submit(@PathVariable Long sessionId,
                                               @RequestParam Long questionId,
                                               @RequestParam String answer) {
        return Result.success(practiceService.submitAnswer(sessionId, questionId, answer));
    }

    @PostMapping("/{sessionId}/finish")
    public Result<PracticeDto> finish(@PathVariable Long sessionId) {
        return Result.success(practiceService.finishPractice(sessionId));
    }

    @GetMapping("/{sessionId}/report")
    public Result<Map<String, Object>> report(@PathVariable Long sessionId) {
        return Result.success(practiceService.getReport(sessionId));
    }
}
