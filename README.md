# Ứng Dụng Quản Lý Ghi Chú iNotes (Strategy Pattern)

> **CodeGym Module 3** - [Case Study] Ứng dụng iNotes  
> Ứng dụng Web Java hoàn chỉnh xây dựng với **Servlet, JSP, JSTL, JDBC MySQL, và File System**, áp dụng mẫu thiết kế **Strategy Pattern** cho phép hoán đổi tầng lưu trữ dữ liệu linh hoạt lúc runtime.

---

## 1. Mục tiêu & Ý tưởng kiến trúc

Hệ thống cung cấp các nghiệp vụ quản lý ghi chú cá nhân:
- **Thêm mới ghi chú**: Tiêu đề, phân loại (Cá nhân, Công việc, Học tập), nội dung.
- **Xem danh sách & Dashboard**: Hiển thị bảng tổng hợp kèm badge phân loại và thời gian.
- **Xem chi tiết, Sửa, Xóa**: Xóa kèm modal xác nhận.
- **Tìm kiếm**: Tìm kiếm theo từ khóa trong tiêu đề hoặc nội dung.
- **Strategy Pattern (Runtime Pluggable Storage)**:
  - 🗄️ **MySQL Database (`NoteDBStrategy`)**: Lưu trữ và truy vấn qua JDBC Driver vào cơ sở dữ liệu `inotes_db`.
  - 📁 **File System (`NoteFileStrategy`)**: Đọc và ghi dữ liệu ra tệp tin văn bản phân tách (`data/inotes_data.txt` định dạng `id|title|content|typeId|createdAt`).
  - Giao diện Web có thanh chuyển đổi tức thì (`Storage Switcher`) mà không làm thay đổi logic giao diện và controller.

---

## 2. Biểu đồ thiết kế Strategy Pattern

```
                       +-------------------------+
                       |     <<interface>>       |
                       |      NoteStrategy       |
                       +-------------------------+
                       | + save(Note): void      |
                       | + delete(int): void     |
                       | + findAll(): List<Note> |
                       | + findById(int): Note   |
                       | + search(kw): List<Note>|
                       +-------------------------+
                                    ▲
                                    |
          +-------------------------+-------------------------+
          |                                                   |
+---------------------+                             +---------------------+
|   NoteDBStrategy    |                             |  NoteFileStrategy   |
+---------------------+                             +---------------------+
| - JDBC Connection   |                             | - File IO (UTF-8)   |
| - PreparedStatement |                             | - Delimited Format  |
+---------------------+                             +---------------------+
                                    ▲
                                    | delegates to
                       +-------------------------+
                       |     NoteManagement      |  (Context)
                       +-------------------------+
                       | - strategy: NoteStrategy|
                       | + setStrategy(strategy) |
                       | + searchNotes(keyword)  |
                       | + addNote(...)          |
                       | + deleteNote(id)        |
                       +-------------------------+
```

---

## 3. Cấu trúc thư mục dự án

```text
inotes/
├── database.sql                               # Script tạo CSDL inotes_db và bảng mẫu
├── pom.xml                                    # Cấu hình Maven (Jakarta Servlet 6.0, MySQL Connector 8.3)
├── build.gradle                               # Cấu hình Gradle build WAR
├── settings.gradle
├── data/
│   └── inotes_data.txt                        # Tệp lưu trữ ghi chú dạng phân tách
├── src/
│   ├── main/
│   │   ├── java/com/codegym/inotes/
│   │   │   ├── model/
│   │   │   │   ├── Note.java                  # Model Ghi chú (setTitle, getContent, save, delete...)
│   │   │   │   └── NoteType.java              # Model Phân loại ghi chú
│   │   │   ├── strategy/
│   │   │   │   ├── NoteStrategy.java          # Interface chiến lược lưu trữ
│   │   │   │   ├── NoteDBStrategy.java        # Triển khai lưu trữ MySQL qua JDBC
│   │   │   │   ├── NoteFileStrategy.java      # Triển khai lưu trữ File text/csv
│   │   │   │   └── DBConnection.java          # Tiện ích kết nối MySQL inotes_db
│   │   │   ├── controller/
│   │   │   │   ├── NoteManagement.java        # Context điều khiển nghiệp vụ Strategy Pattern
│   │   │   │   └── NoteServlet.java           # Servlet xử lý HTTP request/response
│   │   │   └── Main.java                      # Demo Console kiểm thử Strategy Pattern
│   │   └── webapp/
│   │       ├── index.jsp                      # Chuyển hướng tới /notes
│   │       ├── WEB-INF/
│   │       │   └── web.xml                    # Cấu hình Deployment Descriptor
│   │       └── notes/
│   │           ├── list.jsp                   # Dashboard danh sách & Switcher tầng lưu trữ
│   │           ├── create.jsp                 # Form thêm mới ghi chú
│   │           ├── edit.jsp                   # Form chỉnh sửa ghi chú
│   │           └── view.jsp                   # Xem chi tiết ghi chú
│   └── test/
│       └── java/com/codegym/inotes/
│           └── NoteStrategyTest.java          # Bộ kiểm thử tự động JUnit 5
```

---

## 4. Hướng dẫn thiết lập Cơ sở dữ liệu

1. Mở MySQL CLI hoặc MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS inotes_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inotes_db;

CREATE TABLE IF NOT EXISTS note_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS notes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES note_type(id) ON DELETE SET NULL
);

INSERT INTO note_type (id, name, description) VALUES
(1, 'Cá nhân', 'Ghi chú cho các công việc và sinh hoạt cá nhân'),
(2, 'Công việc', 'Nhiệm vụ, lịch họp và công việc dự án'),
(3, 'Học tập', 'Kiến thức, tài liệu và bài tập ôn luyện');
```
*(Hoặc import trực tiếp file `database.sql` đính kèm).*

---

## 5. Hướng dẫn Đóng gói và Chạy ứng dụng

### 1. Đóng gói file `.war` bằng Maven:
```bash
mvn clean package
```
Tệp `.war` được sinh ra tại: `target/inotes.war`.

### 2. Triển khai lên Apache Tomcat:
1. Sao chép `target/inotes.war` vào thư mục `webapps/` của Tomcat.
2. Khởi động Tomcat (`bin/startup.bat` hoặc qua IntelliJ IDEA).
3. Mở trình duyệt và truy cập:
   ```text
   http://localhost:8080/inotes/
   ```

### 3. Chạy kiểm thử tự động (Unit Test):
```bash
mvn test
```
Toàn bộ các test cases cho cả 2 chiến lược `NoteDBStrategy` và `NoteFileStrategy` cùng tính năng hoán đổi Strategy đều vượt qua 100%.
