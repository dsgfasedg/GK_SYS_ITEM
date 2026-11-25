-- H2 兼容建表脚本（开发环境使用）
-- 注意: H2 不支持 ENUM, JSON, ON UPDATE CURRENT_TIMESTAMP 语法

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20),
    wechat_unionid VARCHAR(64),
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    membership_type VARCHAR(10) DEFAULT 'free',
    membership_expire DATETIME,
    monthly_ai_calls_used INT DEFAULT 0,
    monthly_essay_used INT DEFAULT 0,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS plan_tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(50),
    planned_date DATE NOT NULL,
    planned_start_time TIME,
    planned_duration INT,
    actual_duration INT,
    status VARCHAR(20) DEFAULT 'pending',
    delay_count INT DEFAULT 0,
    source VARCHAR(20) DEFAULT 'manual',
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_plan_user_date ON plan_tasks(user_id, planned_date);

CREATE TABLE IF NOT EXISTS notes (
    note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(10) DEFAULT 'text',
    raw_content TEXT,
    formatted_content TEXT,
    tags TEXT,
    source_file_url VARCHAR(255),
    knowledge_node_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS knowledge_tree (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL,
    name VARCHAR(100) NOT NULL,
    level INT,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES knowledge_tree(node_id)
);

INSERT INTO knowledge_tree VALUES (1, NULL, '行测', 1, 1);
INSERT INTO knowledge_tree VALUES (2, 1, '言语理解', 2, 1);
INSERT INTO knowledge_tree VALUES (3, 2, '中心理解', 3, 1);
INSERT INTO knowledge_tree VALUES (4, 2, '逻辑填空', 3, 2);
INSERT INTO knowledge_tree VALUES (5, 1, '判断推理', 2, 2);
INSERT INTO knowledge_tree VALUES (6, 5, '逻辑判断', 3, 1);
INSERT INTO knowledge_tree VALUES (7, 5, '图形推理', 3, 2);
INSERT INTO knowledge_tree VALUES (8, 5, '定义判断', 3, 3);
INSERT INTO knowledge_tree VALUES (9, 5, '类比推理', 3, 4);
INSERT INTO knowledge_tree VALUES (10, 1, '数量关系', 2, 3);
INSERT INTO knowledge_tree VALUES (11, 10, '数学运算', 3, 1);
INSERT INTO knowledge_tree VALUES (12, 1, '资料分析', 2, 4);
INSERT INTO knowledge_tree VALUES (13, 1, '常识判断', 2, 5);
INSERT INTO knowledge_tree VALUES (14, NULL, '申论', 1, 2);
INSERT INTO knowledge_tree VALUES (15, 14, '归纳概括', 2, 1);
INSERT INTO knowledge_tree VALUES (16, 14, '综合分析', 2, 2);
INSERT INTO knowledge_tree VALUES (17, 14, '提出对策', 2, 3);
INSERT INTO knowledge_tree VALUES (18, 14, '贯彻执行', 2, 4);
INSERT INTO knowledge_tree VALUES (19, 14, '申发论述', 2, 5);

CREATE TABLE IF NOT EXISTS errors (
    error_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT,
    stem TEXT NOT NULL,
    options TEXT,
    correct_answer VARCHAR(10),
    user_answer VARCHAR(10),
    analysis VARCHAR(300),
    knowledge_tags TEXT,
    user_error_tag VARCHAR(30),
    mastery INT DEFAULT 0,
    next_review_date DATE,
    review_count INT DEFAULT 0,
    source VARCHAR(20) DEFAULT 'photo',
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS essay_corrections (
    correction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    essay_text TEXT NOT NULL,
    logic_score INT,
    content_score INT,
    language_score INT,
    standard_score INT,
    suggestions TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) DEFAULT 'single_choice',
    stem TEXT NOT NULL,
    options TEXT,
    correct_answer VARCHAR(10),
    analysis VARCHAR(300),
    knowledge_tags TEXT,
    source VARCHAR(20) DEFAULT 'import',
    import_batch_id VARCHAR(64),
    hash_value VARCHAR(64),
    status VARCHAR(20) DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS practice_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'in_progress',
    total_count INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    tag_filter VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS practice_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer VARCHAR(10),
    is_correct INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_usage_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    api_type VARCHAR(30) NOT NULL,
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    cost DECIMAL(10,6) DEFAULT 0,
    status VARCHAR(20),
    error_msg VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
