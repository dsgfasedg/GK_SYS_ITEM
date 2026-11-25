package com.gk.zhixue.question.controller;

import com.gk.zhixue.common.result.Result;
import com.gk.zhixue.question.dto.QuestionDto;
import com.gk.zhixue.question.entity.Question;
import com.gk.zhixue.question.mapper.QuestionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionMapper questionMapper;

    public QuestionController(QuestionMapper questionMapper) { this.questionMapper = questionMapper; }

    @PostMapping
    @Transactional
    public Result<QuestionDto> create(@RequestBody QuestionDto dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Question q = new Question();
        q.setUserId(userId);
        q.setStem(dto.getStem());
        q.setOptions(dto.getOptions());
        q.setCorrectAnswer(dto.getCorrectAnswer());
        q.setAnalysis(dto.getAnalysis());
        q.setKnowledgeTags(dto.getKnowledgeTags());
        q.setType(dto.getType() != null ? dto.getType() : "single_choice");
        q.setSource("manual");
        questionMapper.insert(q);
        return Result.success(toDto(q));
    }

    @GetMapping
    public Result<List<QuestionDto>> list(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String tag) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getUserId, userId).orderByDesc(Question::getCreatedAt);
        if (tag != null && !tag.isEmpty()) wrapper.like(Question::getKnowledgeTags, tag);
        Page<Question> p = questionMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(p.getRecords().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{questionId}")
    public Result<QuestionDto> detail(@PathVariable Long questionId) {
        Question q = questionMapper.selectById(questionId);
        if (q == null) throw BusinessException.notFound("题目不存在");
        return Result.success(toDto(q));
    }

    @DeleteMapping("/{questionId}")
    public Result<String> delete(@PathVariable Long questionId) {
        questionMapper.deleteById(questionId);
        return Result.success("已删除");
    }

    private QuestionDto toDto(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.setQuestionId(q.getQuestionId());
        dto.setType(q.getType());
        dto.setStem(q.getStem());
        dto.setOptions(q.getOptions());
        dto.setCorrectAnswer(q.getCorrectAnswer());
        dto.setKnowledgeTags(q.getKnowledgeTags());
        dto.setSource(q.getSource());
        dto.setStatus(q.getStatus());
        dto.setCreatedAt(q.getCreatedAt());
        return dto;
    }
}
