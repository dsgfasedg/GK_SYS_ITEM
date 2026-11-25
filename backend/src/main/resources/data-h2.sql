-- 插入测试用户（密码/验证码: 123456）
INSERT INTO users (phone, nickname, membership_type, monthly_ai_calls_used, monthly_essay_used, last_login, created_at)
VALUES ('13800138000', '测试用户', 'free', 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (phone, nickname, membership_type, membership_expire, monthly_ai_calls_used, monthly_essay_used, last_login, created_at)
VALUES ('13900139000', 'VIP用户', 'vip', DATEADD('YEAR', 1, CURRENT_TIMESTAMP), 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入示例计划任务
INSERT INTO plan_tasks (user_id, title, subject, planned_date, planned_start_time, planned_duration, status, sort_order, created_at)
VALUES (1, '行测-言语理解 真题练习', '言语理解', CURRENT_DATE, TIME '09:00:00', 60, 'pending', 1, CURRENT_TIMESTAMP);

INSERT INTO plan_tasks (user_id, title, subject, planned_date, planned_start_time, planned_duration, status, sort_order, created_at)
VALUES (1, '行测-判断推理 技巧复习', '判断推理', CURRENT_DATE, TIME '10:30:00', 45, 'pending', 2, CURRENT_TIMESTAMP);

INSERT INTO plan_tasks (user_id, title, subject, planned_date, planned_start_time, planned_duration, status, sort_order, created_at)
VALUES (1, '申论-归纳概括 范文阅读', '申论', CURRENT_DATE, TIME '14:00:00', 90, 'pending', 3, CURRENT_TIMESTAMP);
