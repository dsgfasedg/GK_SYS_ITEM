CREATE TABLE essay_corrections (
    correction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    essay_text TEXT NOT NULL,
    logic_score TINYINT COMMENT '逻辑结构1-10',
    content_score TINYINT COMMENT '内容要点1-10',
    language_score TINYINT COMMENT '语言表达1-10',
    standard_score TINYINT COMMENT '规范评分1-10',
    suggestions JSON COMMENT '["建议1","建议2"]',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
);
