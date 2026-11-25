package com.gk.zhixue.error.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.error.dto.ErrorQuestionDto;
import com.gk.zhixue.error.entity.ErrorQuestion;
import com.gk.zhixue.error.mapper.ErrorQuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ErrorQuestionService {

    private static final Logger log = LoggerFactory.getLogger(ErrorQuestionService.class);
    private final ErrorQuestionMapper errorQuestionMapper;

    public ErrorQuestionService(ErrorQuestionMapper errorQuestionMapper) {
        this.errorQuestionMapper = errorQuestionMapper;
    }

    @Transactional
    public ErrorQuestionDto create(ErrorQuestionDto dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        ErrorQuestion eq = new ErrorQuestion();
        eq.setUserId(userId);
        eq.setStem(dto.getStem());
        eq.setOptions(dto.getOptions());
        eq.setCorrectAnswer(dto.getCorrectAnswer());
        eq.setUserAnswer(dto.getUserAnswer());
        eq.setAnalysis(dto.getAnalysis());
        eq.setKnowledgeTags(dto.getKnowledgeTags());
        eq.setUserErrorTag(dto.getUserErrorTag());
        eq.setSource(dto.getSource() != null ? dto.getSource() : "manual");
        eq.setImageUrl(dto.getImageUrl());
        eq.setNextReviewDate(LocalDate.now().plusDays(1));
        errorQuestionMapper.insert(eq);
        return toDto(eq);
    }

    public ErrorQuestionDto detail(Long errorId) {
        ErrorQuestion eq = errorQuestionMapper.selectById(errorId);
        if (eq == null) throw BusinessException.notFound("错题不存在");
        return toDto(eq);
    }

    public List<ErrorQuestionDto> list(int page, int size, String tag) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ErrorQuestion> wrapper = new LambdaQueryWrapper<ErrorQuestion>()
                .eq(ErrorQuestion::getUserId, userId).orderByDesc(ErrorQuestion::getCreatedAt);
        if (tag != null && !tag.isEmpty()) wrapper.like(ErrorQuestion::getKnowledgeTags, tag);
        Page<ErrorQuestion> p = errorQuestionMapper.selectPage(new Page<>(page, size), wrapper);
        return p.getRecords().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ErrorQuestionDto> todayReview() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ErrorQuestion> wrapper = new LambdaQueryWrapper<ErrorQuestion>()
                .eq(ErrorQuestion::getUserId, userId)
                .le(ErrorQuestion::getNextReviewDate, LocalDate.now())
                .orderByAsc(ErrorQuestion::getNextReviewDate);
        return errorQuestionMapper.selectList(wrapper).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void submitReview(Long errorId, boolean isCorrect) {
        ErrorQuestion eq = errorQuestionMapper.selectById(errorId);
        if (eq == null) throw BusinessException.notFound("错题不存在");
        int mastery = eq.getMastery() != null ? eq.getMastery() : 0;
        mastery = isCorrect ? Math.min(100, mastery + 10) : Math.max(0, mastery - 20);
        eq.setMastery(mastery);
        eq.setReviewCount(eq.getReviewCount() + 1);

        // Ebbinghaus schedule: 1,2,4,7,15,30 days
        int[] intervals = {1, 2, 4, 7, 15, 30};
        int idx = Math.min(eq.getReviewCount(), intervals.length - 1);
        eq.setNextReviewDate(LocalDate.now().plusDays(intervals[idx]));

        errorQuestionMapper.updateById(eq);
    }

    @Transactional
    public void updateTag(Long errorId, String tag) {
        ErrorQuestion eq = errorQuestionMapper.selectById(errorId);
        if (eq == null) throw BusinessException.notFound("错题不存在");
        eq.setUserErrorTag(tag);
        errorQuestionMapper.updateById(eq);
    }

    @Transactional
    public void delete(Long errorId) {
        errorQuestionMapper.deleteById(errorId);
    }

    public List<Map<String, Object>> getStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ErrorQuestion> all = errorQuestionMapper.selectList(
                new LambdaQueryWrapper<ErrorQuestion>().eq(ErrorQuestion::getUserId, userId));
        Map<String, Long> tagCount = all.stream()
                .filter(e -> e.getKnowledgeTags() != null)
                .flatMap(e -> Arrays.stream(e.getKnowledgeTags().split(",")))
                .collect(Collectors.groupingBy(String::trim, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        tagCount.forEach((tag, count) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tag", tag); m.put("count", count);
            result.add(m);
        });
        result.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));
        return result;
    }

    private ErrorQuestionDto toDto(ErrorQuestion eq) {
        ErrorQuestionDto dto = new ErrorQuestionDto();
        dto.setErrorId(eq.getErrorId());
        dto.setStem(eq.getStem());
        dto.setOptions(eq.getOptions());
        dto.setCorrectAnswer(eq.getCorrectAnswer());
        dto.setUserAnswer(eq.getUserAnswer());
        dto.setAnalysis(eq.getAnalysis());
        dto.setKnowledgeTags(eq.getKnowledgeTags());
        dto.setUserErrorTag(eq.getUserErrorTag());
        dto.setMastery(eq.getMastery());
        dto.setNextReviewDate(eq.getNextReviewDate());
        dto.setReviewCount(eq.getReviewCount());
        dto.setSource(eq.getSource());
        dto.setImageUrl(eq.getImageUrl());
        dto.setCreatedAt(eq.getCreatedAt());
        return dto;
    }
}
