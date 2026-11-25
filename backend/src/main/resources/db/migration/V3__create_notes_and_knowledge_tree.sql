CREATE TABLE notes (
    note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('text','voice','image') DEFAULT 'text',
    raw_content TEXT,
    formatted_content TEXT,
    tags JSON,
    source_file_url VARCHAR(255),
    knowledge_node_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    FULLTEXT INDEX ft_content (raw_content, formatted_content)
);

CREATE TABLE knowledge_tree (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL,
    name VARCHAR(100) NOT NULL,
    level INT COMMENT '层级: 1学科/2模块/3考点/4要点',
    sort_order INT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES knowledge_tree(node_id)
);
