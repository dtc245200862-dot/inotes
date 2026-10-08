package com.codegym.inotes;

import com.codegym.inotes.controller.NoteManagement;
import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;
import com.codegym.inotes.strategy.NoteDBStrategy;
import com.codegym.inotes.strategy.NoteFileStrategy;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NoteStrategyTest {

    private static File tempFile;
    private static NoteFileStrategy fileStrategy;
    private static NoteDBStrategy dbStrategy;

    @BeforeAll
    static void setup() throws IOException {
        tempFile = File.createTempFile("inotes_test_", ".txt");
        tempFile.deleteOnExit();
        fileStrategy = new NoteFileStrategy(tempFile);
        dbStrategy = new NoteDBStrategy();
    }

    @Test
    @Order(1)
    @DisplayName("Kiểm thử NoteFileStrategy: Thêm mới, tìm kiếm, sửa và xóa ghi chú trên File")
    void testFileStrategyCRUD() {
        // 1. Thêm mới
        Note note = new Note("Test File Note", "Nội dung ghi chú test file", 1);
        fileStrategy.save(note);
        assertTrue(note.getId() > 0, "ID phải được tự động sinh > 0");

        // 2. Tìm theo ID
        Note fetched = fileStrategy.findById(note.getId());
        assertNotNull(fetched, "Phải tìm thấy ghi chú vừa lưu trên file");
        assertEquals("Test File Note", fetched.getTitle());

        // 3. Tìm kiếm theo từ khóa
        List<Note> searchResult = fileStrategy.search("Test File");
        assertFalse(searchResult.isEmpty());

        // 4. Cập nhật
        fetched.setTitle("Updated File Note");
        fileStrategy.save(fetched);
        Note updated = fileStrategy.findById(note.getId());
        assertEquals("Updated File Note", updated.getTitle());

        // 5. Xóa
        fileStrategy.delete(note.getId());
        assertNull(fileStrategy.findById(note.getId()), "Ghi chú đã xóa không được tồn tại");
    }

    @Test
    @Order(2)
    @DisplayName("Kiểm thử NoteDBStrategy: Thao tác CRUD trên MySQL Database")
    void testDBStrategyCRUD() {
        // Kiểm tra danh sách loại ghi chú
        List<NoteType> types = dbStrategy.findAllTypes();
        assertNotNull(types);
        assertFalse(types.isEmpty(), "Bảng note_type phải có dữ liệu mẫu");

        // 1. Thêm mới
        Note note = new Note("Test DB Note", "Nội dung ghi chú test MySQL", 2);
        dbStrategy.save(note);
        assertTrue(note.getId() > 0, "ID phải được MySQL trả về > 0");

        // 2. Tìm theo ID
        Note fetched = dbStrategy.findById(note.getId());
        assertNotNull(fetched);
        assertEquals("Test DB Note", fetched.getTitle());

        // 3. Tìm kiếm
        List<Note> results = dbStrategy.search("Test DB");
        assertFalse(results.isEmpty());

        // 4. Cập nhật
        fetched.setTitle("Updated DB Note");
        dbStrategy.save(fetched);
        Note updated = dbStrategy.findById(note.getId());
        assertEquals("Updated DB Note", updated.getTitle());

        // 5. Xóa
        dbStrategy.delete(note.getId());
        assertNull(dbStrategy.findById(note.getId()), "Ghi chú đã xóa trong DB không được tồn tại");
    }

    @Test
    @Order(3)
    @DisplayName("Kiểm thử NoteManagement: Hoán đổi linh hoạt tầng lưu trữ (Strategy Pattern Runtime Pluggable Storage)")
    void testStrategySwitching() {
        NoteManagement manager = new NoteManagement();

        // Ban đầu là DB
        manager.setStrategy(dbStrategy);
        assertEquals("DB", manager.getStrategy().getStorageTypeKey());
        assertEquals("Cơ sở dữ liệu (MySQL Database)", manager.getStrategy().getStrategyName());

        // Chuyển sang File
        manager.setStrategy(fileStrategy);
        assertEquals("FILE", manager.getStrategy().getStorageTypeKey());
        assertEquals("Tập tin Văn bản (File System txt/csv)", manager.getStrategy().getStrategyName());

        // Chuyển lại DB qua switchStrategy helper
        manager.switchStrategy("DB");
        assertEquals("DB", manager.getStrategy().getStorageTypeKey());
    }
}
