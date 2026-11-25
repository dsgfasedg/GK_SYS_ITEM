CREATE TABLE questions (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('single_choice','multi_choice') DEFAULT 'single_choice',
    stem TEXT NOT NULL,
    options JSON,
    correct_answer VARCHAR(10),
    analysis VARCHAR(300),
    knowledge_tags JSON,
    source ENUM('import','manual') DEFAULT 'import',
    import_batch_id VARCHAR(64),
    hash_value VARCHAR(64) COMMENT 'SimHash用于去重',
    status ENUM('pending_confirm','active') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_hash (hash_value)
);

CREATE TABLE practice_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status ENUM('in_progress','completed') DEFAULT 'in_progress',
    total_count INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    tag_filter VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE practice_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer VARCHAR(10),
    is_correct TINYINT COMMENT '0错误 1正确',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id)
);
