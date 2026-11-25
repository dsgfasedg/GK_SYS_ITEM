package com.gk.zhixue.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.practice.dto.PracticeDto;
import com.gk.zhixue.practice.entity.PracticeSession;
import com.gk.zhixue.practice.mapper.PracticeSessionMapper;
import com.gk.zhixue.question.entity.Question;
import com.gk.zhixue.question.mapper.QuestionMapper;
import com.gk.zhixue.error.entity.ErrorQuestion;
import com.gk.zhixue.error.mapper.ErrorQuestionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PracticeService {

    private static final Logger log = LoggerFactory.getLogger(PracticeService.class);
    private final PracticeSessionMapper sessionMapper;
    private final QuestionMapper questionMapper;
    private final ErrorQuestionMapper errorQuestionMapper;

    public PracticeService(PracticeSessionMapper sessionMapper, QuestionMapper questionMapper,
                           ErrorQuestionMapper errorQuestionMapper) {
        this.sessionMapper = sessionMapper;
        this.questionMapper = questionMapper;
        this.errorQuestionMapper = errorQuestionMapper;
    }

    @Transactional
    public PracticeDto startPractice(String tagFilter, int count) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getUserId, userId).eq(Question::getStatus, "active");
        if (tagFilter != null && !tagFilter.isEmpty()) wrapper.like(Question::getKnowledgeTags, tagFilter);
        wrapper.last("ORDER BY RAND() LIMIT " + count);
        List<Question> questions = questionMapper.selectList(wrapper);

        PracticeSession session = new PracticeSession();
        session.setUserId(userId);
        session.setStatus("in_progress");
        session.setTotalCount(questions.size());
        session.setTagFilter(tagFilter);
        sessionMapper.insert(session);

        return toDto(session);
    }

    public Question getNextQuestion(Long sessionId) {
        PracticeSession session = sessionMapper.selectById(sessionId);
        if (session == null) throw BusinessException.notFound("练习会话不存在");
        // Return a random question
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>().eq(Question::getUserId, session.getUserId())
                        .eq(Question::getStatus, "active").last("ORDER BY RAND() LIMIT 1"));
        return questions.isEmpty() ? null : questions.get(0);
    }

    @Transactional
    public Map<String, Object> submitAnswer(Long sessionId, Long questionId, String answer) {
        Long userId = SecurityUtils.getCurrentUserId();
        Question question = questionMapper.selectById(questionId);
        if (question == null) throw BusinessException.notFound("题目不存在");

        boolean isCorrect = question.getCorrectAnswer() != null && question.getCorrectAnswer().equals(answer);
        PracticeSession session = sessionMapper.selectById(sessionId);
        if (session != null && isCorrect) {
            session.setCorrectCount(session.getCorrectCount() + 1);
            sessionMapper.updateById(session);
        }

        // Auto-collect error
        if (!isCorrect) {
            ErrorQuestion eq = new ErrorQuestion();
            eq.setUserId(userId);
            eq.setQuestionId(questionId);
            eq.setStem(question.getStem());
            eq.setOptions(question.getOptions());
            eq.setCorrectAnswer(question.getCorrectAnswer());
            eq.setUserAnswer(answer);
            eq.setAnalysis(question.getAnalysis());
            eq.setKnowledgeTags(question.getKnowledgeTags());
            eq.setSource("exercise");
            eq.setNextReviewDate(LocalDateTime.now().plusDays(1).toLocalDate());
            errorQuestionMapper.insert(eq);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("correct", isCorrect);
        result.put("correctAnswer", question.getCorrectAnswer());
        result.put("analysis", question.getAnalysis());
        return result;
    }

    @Transactional
    public PracticeDto finishPractice(Long sessionId) {
        PracticeSession session = sessionMapper.selectById(sessionId);
        if (session == null) throw BusinessException.notFound("练习会话不存在");
        session.setStatus("completed");
        sessionMapper.updateById(session);
        return toDto(session);
    }

    public Map<String, Object> getReport(Long sessionId) {
        PracticeSession session = sessionMapper.selectById(sessionId);
        if (session == null) throw BusinessException.notFound("练习会话不存在");
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalCount", session.getTotalCount());
        report.put("correctCount", session.getCorrectCount());
        report.put("wrongCount", session.getTotalCount() - session.getCorrectCount());
        report.put("accuracy", session.getTotalCount() == 0 ? 0 :
                (double) session.getCorrectCount() / session.getTotalCount() * 100);
        return report;
    }

    private PracticeDto toDto(PracticeSession session) {
        PracticeDto dto = new PracticeDto();
        dto.setSessionId(session.getSessionId());
        dto.setStatus(session.getStatus());
        dto.setTotalCount(session.getTotalCount());
        dto.setCorrectCount(session.getCorrectCount());
        dto.setTagFilter(session.getTagFilter());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }
}
