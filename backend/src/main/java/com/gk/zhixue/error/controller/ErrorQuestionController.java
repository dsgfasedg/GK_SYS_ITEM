package com.gk.zhixue.error.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.error.dto.ErrorQuestionDto;
import com.gk.zhixue.error.service.ErrorQuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/errors")
public class ErrorQuestionController {

    private static final Logger log = LoggerFactory.getLogger(ErrorQuestionController.class);
    private final ErrorQuestionService errorService;

    public ErrorQuestionController(ErrorQuestionService errorService) { this.errorService = errorService; }

    @PostMapping
    public Result<ErrorQuestionDto> create(@RequestBody ErrorQuestionDto dto) {
        return Result.success(errorService.create(dto));
    }

    @GetMapping
    public Result<List<ErrorQuestionDto>> list(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String tag) {
        return Result.success(errorService.list(page, size, tag));
    }

    @GetMapping("/{errorId}")
    public Result<ErrorQuestionDto> detail(@PathVariable Long errorId) {
        return Result.success(errorService.detail(errorId));
    }

    @GetMapping("/review/today")
    public Result<List<ErrorQuestionDto>> todayReview() {
        return Result.success(errorService.todayReview());
    }

    @PostMapping("/{errorId}/review")
    public Result<String> submitReview(@PathVariable Long errorId, @RequestParam boolean correct) {
        errorService.submitReview(errorId, correct);
        return Result.success("复习完成");
    }

    @PatchMapping("/{errorId}/tag")
    public Result<String> updateTag(@PathVariable Long errorId, @RequestParam String tag) {
        errorService.updateTag(errorId, tag);
        return Result.success("标签已更新");
    }

    @DeleteMapping("/{errorId}")
    public Result<String> delete(@PathVariable Long errorId) {
        errorService.delete(errorId);
        return Result.success("已删除");
    }

    @GetMapping("/stats")
    public Result<List<Map<String, Object>>> getStats() {
        return Result.success(errorService.getStats());
    }
}
