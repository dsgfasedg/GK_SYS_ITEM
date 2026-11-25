CREATE TABLE plan_tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(50),
    planned_date DATE NOT NULL,
    planned_start_time TIME,
    planned_duration INT COMMENT '分钟',
    actual_duration INT COMMENT '实际耗时（分钟）',
    status ENUM('pending','completed','delayed') DEFAULT 'pending',
    delay_count INT DEFAULT 0,
    source ENUM('manual','image_import') DEFAULT 'manual',
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, planned_date)
);
