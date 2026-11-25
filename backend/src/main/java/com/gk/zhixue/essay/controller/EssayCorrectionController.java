package com.gk.zhixue.essay.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.essay.dto.EssayDto;
import com.gk.zhixue.essay.service.EssayCorrectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/essay")
public class EssayCorrectionController {

    private static final Logger log = LoggerFactory.getLogger(EssayCorrectionController.class);
    private final EssayCorrectionService essayService;

    public EssayCorrectionController(EssayCorrectionService essayService) { this.essayService = essayService; }

    @PostMapping("/correct")
    public Result<EssayDto> correct(@RequestParam String questionText, @RequestParam String essayText) {
        return Result.success(essayService.submit(questionText, essayText));
    }

    @GetMapping("/history")
    public Result<List<EssayDto>> history(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return Result.success(essayService.history(page, size));
    }

    @GetMapping("/{correctionId}")
    public Result<EssayDto> detail(@PathVariable Long correctionId) {
        return Result.success(essayService.detail(correctionId));
    }
}
