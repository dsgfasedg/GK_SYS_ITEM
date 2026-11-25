CREATE TABLE ai_usage_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    api_type VARCHAR(30) NOT NULL COMMENT 'plan_ocr/note_organize/error_parse/essay_correct/question_parse',
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    cost DECIMAL(10,6) DEFAULT 0 COMMENT '本次调用费用',
    status VARCHAR(20) COMMENT 'success/failed',
    error_msg VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
);
