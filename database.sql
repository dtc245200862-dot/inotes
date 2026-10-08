-- ========================================================
-- CƠ SỞ DỮ LIỆU iNotes (MySQL)
-- Áp dụng trong Case Study: Ứng dụng iNotes (Strategy Pattern)
-- ========================================================

CREATE DATABASE IF NOT EXISTS inotes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inotes_db;

-- 1. Bảng phân loại thể loại ghi chú
CREATE TABLE IF NOT EXISTS note_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- 2. Bảng lưu trữ ghi chú
CREATE TABLE IF NOT EXISTS notes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES note_type(id) ON DELETE SET NULL
);

-- 3. Dữ liệu mẫu bảng note_type
INSERT INTO note_type (id, name, description) VALUES
(1, 'Cá nhân', 'Ghi chú cho các công việc và sinh hoạt cá nhân'),
(2, 'Công việc', 'Nhiệm vụ, lịch họp và công việc dự án'),
(3, 'Học tập', 'Kiến thức, tài liệu và bài tập ôn luyện')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. Dữ liệu mẫu bảng notes
INSERT INTO notes (title, content, type_id) VALUES
('Họp giao ban đầu tuần', 'Báo cáo tiến độ hoàn thành các chức năng iNotes và kế hoạch tuần mới', 2),
('Ôn tập Design Pattern Servlet', 'Học sâu về Strategy Pattern, Factory Pattern, MVC và JDBC DAO', 3),
('Mua sắm cuối tuần', 'Mua sữa, cà phê, bánh mỳ và đồ dùng gia đình', 1);
