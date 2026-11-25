CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20),
    wechat_unionid VARCHAR(64),
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    membership_type ENUM('free','vip') DEFAULT 'free',
    membership_expire DATETIME,
    monthly_ai_calls_used INT DEFAULT 0,
    monthly_essay_used INT DEFAULT 0,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_wechat (wechat_unionid)
);
