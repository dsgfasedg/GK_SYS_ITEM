package com.gk.zhixue.essay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.zhixue.ai.ChatResult;
import com.gk.zhixue.ai.DeepSeekClient;
import com.gk.zhixue.ai.entity.AiUsageLog;
import com.gk.zhixue.ai.mapper.AiUsageLogMapper;
import com.gk.zhixue.common.exception.BusinessException;
import com.gk.zhixue.common.security.SecurityUtils;
import com.gk.zhixue.essay.dto.EssayDto;
import com.gk.zhixue.essay.entity.EssayCorrection;
import com.gk.zhixue.essay.mapper.EssayCorrectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EssayCorrectionService {

    private static final Logger log = LoggerFactory.getLogger(EssayCorrectionService.class);
    private final EssayCorrectionMapper essayCorrectionMapper;
    private final DeepSeekClient deepSeekClient;
    private final AiUsageLogMapper aiUsageLogMapper;

    public EssayCorrectionService(EssayCorrectionMapper essayCorrectionMapper,
                                  DeepSeekClient deepSeekClient,
                                  AiUsageLogMapper aiUsageLogMapper) {
        this.essayCorrectionMapper = essayCorrectionMapper;
        this.deepSeekClient = deepSeekClient;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    @Transactional
    public EssayDto submit(String questionText, String essayText) {
        Long userId = SecurityUtils.getCurrentUserId();

        String systemPrompt = "你是一名资深申论阅卷官。请对以下申论作答进行评分。"
                + "从四个维度评分（每项1-10分）：logic_score（逻辑结构）、content_score（内容要点）、"
                + "language_score（语言表达）、standard_score（规范评分）。"
                + "给出2-3条简明修改建议（每条不超过30字）。"
                + "只输出JSON，不要额外内容："
                + "{\"logic_score\":7,\"content_score\":6,\"language_score\":8,\"standard_score\":7,\"brief_suggestion\":[\"建议1\",\"建议2\"]}";

        String userPrompt = "题目：\n" + questionText + "\n\n考生作答：\n" + essayText;

        int logic = 6, content = 6, language = 6, standard = 6;
        String suggestions = "[\"论点可以更明确\", \"增加实例支撑\"]";

        try {
            ChatResult aiResponse = deepSeekClient.chat(systemPrompt, userPrompt);

            // 记录 AI 调用日志
            AiUsageLog logEntry = new AiUsageLog();
            logEntry.setUserId(userId);
            logEntry.setApiType("essay_correct");
            logEntry.setInputTokens(aiResponse.getInputTokens());
            logEntry.setOutputTokens(aiResponse.getOutputTokens());
            logEntry.setCost(aiResponse.getCost());
            logEntry.setStatus(aiResponse.isSuccess() ? "success" : "failed");
            logEntry.setErrorMsg(aiResponse.getErrorMsg());
            logEntry.setCreatedAt(java.time.LocalDateTime.now());
            aiUsageLogMapper.insert(logEntry);

            if (aiResponse.isSuccess() && aiResponse.getContent() != null) {
                String json = aiResponse.getContent();
                int start = json.indexOf('{');
                int end = json.lastIndexOf('}');
                if (start >= 0 && end > start) json = json.substring(start, end + 1);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(json);
                logic = root.path("logic_score").asInt(6);
                content = root.path("content_score").asInt(6);
                language = root.path("language_score").asInt(6);
                standard = root.path("standard_score").asInt(6);

                JsonNode arr = root.path("brief_suggestion");
                if (arr.isArray()) {
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < arr.size(); i++) {
                        if (i > 0) sb.append(",");
                        sb.append("\"").append(arr.get(i).asText()).append("\"");
                    }
                    sb.append("]");
                    suggestions = sb.toString();
                }
            }
        } catch (Exception e) {
            log.warn("申论AI批改失败，使用默认评分: {}", e.getMessage());
        }

        EssayCorrection ec = new EssayCorrection();
        ec.setUserId(userId);
        ec.setQuestionText(questionText);
        ec.setEssayText(essayText);
        ec.setLogicScore(logic);
        ec.setContentScore(content);
        ec.setLanguageScore(language);
        ec.setStandardScore(standard);
        ec.setSuggestions(suggestions);
        essayCorrectionMapper.insert(ec);
        return toDto(ec);
    }

    public List<EssayDto> history(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<EssayCorrection> p = essayCorrectionMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<EssayCorrection>().eq(EssayCorrection::getUserId, userId).orderByDesc(EssayCorrection::getCreatedAt));
        return p.getRecords().stream().map(this::toDto).collect(Collectors.toList());
    }

    public EssayDto detail(Long correctionId) {
        EssayCorrection ec = essayCorrectionMapper.selectById(correctionId);
        if (ec == null) throw BusinessException.notFound("批改记录不存在");
        return toDto(ec);
    }

    private EssayDto toDto(EssayCorrection ec) {
        EssayDto dto = new EssayDto();
        dto.setCorrectionId(ec.getCorrectionId());
        dto.setQuestionText(ec.getQuestionText());
        dto.setEssayText(ec.getEssayText());
        dto.setLogicScore(ec.getLogicScore());
        dto.setContentScore(ec.getContentScore());
        dto.setLanguageScore(ec.getLanguageScore());
        dto.setStandardScore(ec.getStandardScore());
        dto.setSuggestions(ec.getSuggestions());
        dto.setCreatedAt(ec.getCreatedAt());
        return dto;
    }
}
