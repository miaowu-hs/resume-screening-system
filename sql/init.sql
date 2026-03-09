-- 智能简历筛选系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS resume_screening DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE resume_screening;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role VARCHAR(20) DEFAULT 'HR' COMMENT '角色: HR/ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 岗位表
CREATE TABLE IF NOT EXISTS job_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    title VARCHAR(100) NOT NULL COMMENT '岗位名称',
    department VARCHAR(100) COMMENT '部门',
    requirements TEXT COMMENT '岗位要求（原始文本）',
    skills JSON COMMENT '技能要求 ["Java","MySQL"]',
    experience_min INT DEFAULT 0 COMMENT '最小工作年限',
    salary_range VARCHAR(50) COMMENT '薪资范围',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-招聘中 0-已关闭',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_status (status),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 简历表
CREATE TABLE IF NOT EXISTS resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    file_name VARCHAR(255) COMMENT '原始文件名',
    file_path VARCHAR(500) COMMENT '存储路径',
    candidate_name VARCHAR(100) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    education VARCHAR(50) COMMENT '学历',
    experience_years INT COMMENT '工作年限',
    skills JSON COMMENT '技能列表',
    work_history TEXT COMMENT '工作经历',
    parsed_content TEXT COMMENT '解析后的完整内容',
    upload_user_id BIGINT COMMENT '上传人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_candidate_name (candidate_name),
    INDEX idx_education (education),
    FOREIGN KEY (upload_user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 匹配记录表
CREATE TABLE IF NOT EXISTS match_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '匹配ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    position_id BIGINT NOT NULL COMMENT '岗位ID',
    match_score DECIMAL(5,2) COMMENT '匹配度分数 0-100',
    match_reason TEXT COMMENT 'AI 匹配理由',
    skill_match JSON COMMENT '技能匹配详情',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/viewed/accepted/rejected',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_resume_id (resume_id),
    INDEX idx_position_id (position_id),
    INDEX idx_match_score (match_score),
    FOREIGN KEY (resume_id) REFERENCES resume(id),
    FOREIGN KEY (position_id) REFERENCES job_position(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='匹配记录表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    action VARCHAR(50) COMMENT '操作类型',
    target_type VARCHAR(50) COMMENT '目标类型',
    target_id BIGINT COMMENT '目标ID',
    detail TEXT COMMENT '详情',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入默认管理员账号 (密码: admin123，需要加密)
INSERT INTO user (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN');

-- 插入示例岗位数据
INSERT INTO job_position (title, department, requirements, skills, experience_min, salary_range, status) VALUES
('Java开发工程师', '技术部', '熟练掌握Java语言，熟悉Spring Boot框架，有MySQL数据库开发经验，了解微服务架构', '["Java", "Spring Boot", "MySQL", "Redis"]', 2, '15k-25k', 1),
('前端开发工程师', '技术部', '熟练掌握Vue或React框架，熟悉TypeScript，有移动端开发经验优先', '["Vue", "JavaScript", "TypeScript", "CSS"]', 2, '12k-20k', 1),
('Python开发工程师', '技术部', '熟练掌握Python语言，熟悉Django或Flask框架，有数据分析经验优先', '["Python", "Django", "Pandas", "MySQL"]', 2, '15k-25k', 1),
('测试工程师', '质量部', '熟悉软件测试理论，掌握Selenium等自动化测试工具，会Python或Java优先', '["Selenium", "Python", "JMeter", "SQL"]', 1, '10k-18k', 1),
('运维工程师', '运维部', '熟悉Linux系统，掌握Docker容器技术，有Kubernetes经验优先', '["Linux", "Docker", "Kubernetes", "CI/CD"]', 3, '15k-25k', 1);
