package com.codegym.inotes.controller;

import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;
import com.codegym.inotes.strategy.NoteDBStrategy;
import com.codegym.inotes.strategy.NoteFileStrategy;
import com.codegym.inotes.strategy.NoteStrategy;

import java.util.List;

/**
 * Lớp điều khiển chính (Context trong Strategy Pattern) quản lý các thao tác ghi chú.
 * Cho phép chuyển đổi linh hoạt tầng lưu trữ giữa MySQL Database và File System lúc runtime.
 */
public class NoteManagement {

    private static volatile NoteManagement instance;

    // Thuộc tính chiến lược theo yêu cầu Bước 6
    private NoteStrategy strategy;

    public NoteManagement() {
        // Mặc định khởi tạo với chiến lược Cơ sở dữ liệu MySQL
        this.strategy = new NoteDBStrategy();
    }

    public NoteManagement(NoteStrategy strategy) {
        this.strategy = strategy != null ? strategy : new NoteDBStrategy();
    }

    public static NoteManagement getInstance() {
        if (instance == null) {
            synchronized (NoteManagement.class) {
                if (instance == null) {
                    instance = new NoteManagement();
                }
            }
        }
        return instance;
    }

    /**
     * Phương thức thay đổi tầng lưu trữ lúc runtime (Runtime Pluggable Storage).
     *
     * @param strategy chiến lược lưu trữ mới
     */
    public void setStrategy(NoteStrategy strategy) {
        if (strategy != null) {
            this.strategy = strategy;
        }
    }

    /**
     * Lấy chiến lược lưu trữ hiện tại.
     *
     * @return đối tượng NoteStrategy
     */
    public NoteStrategy getStrategy() {
        return strategy;
    }

    /**
     * Chuyển đổi chiến lược lưu trữ qua mã khóa ("DB" hoặc "FILE").
     *
     * @param storageType "DB" hoặc "FILE"
     */
    public void switchStrategy(String storageType) {
        if ("FILE".equalsIgnoreCase(storageType)) {
            setStrategy(new NoteFileStrategy());
        } else {
            setStrategy(new NoteDBStrategy());
        }
    }

    // --- Các phương thức nghiệp vụ theo Bước 3 ---

    /**
     * Tìm kiếm danh sách ghi chú theo từ khóa xuất hiện trong tiêu đề hoặc nội dung.
     *
     * @param keyword từ khóa cần tìm
     * @return danh sách ghi chú thỏa mãn
     */
    public List<Note> searchNotes(String keyword) {
        return strategy.search(keyword);
    }

    /**
     * Tạo mới và lưu ghi chú vào tầng lưu trữ hiện tại.
     *
     * @param title tiêu đề ghi chú
     * @param content nội dung chi tiết
     * @param typeId mã phân loại
     * @return đối tượng Note vừa được tạo
     */
    public Note addNote(String title, String content, int typeId) {
        Note note = new Note(title, content, typeId);
        strategy.save(note);
        return note;
    }

    /**
     * Cập nhật ghi chú.
     *
     * @param note đối tượng Note
     */
    public void saveNote(Note note) {
        strategy.save(note);
    }

    /**
     * Xóa bỏ ghi chú tương ứng theo ID.
     *
     * @param id mã ghi chú cần xóa
     */
    public boolean deleteNote(int id) {
        try {
            strategy.delete(id);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa ghi chú: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách toàn bộ ghi chú theo tầng lưu trữ hiện tại.
     *
     * @return danh sách Note
     */
    public List<Note> getAllNotes() {
        return strategy.findAll();
    }

    /**
     * Lấy chi tiết một ghi chú theo ID.
     *
     * @param id mã ghi chú
     * @return đối tượng Note
     */
    public Note getNoteById(int id) {
        return strategy.findById(id);
    }

    /**
     * Lấy danh sách toàn bộ thể loại ghi chú (Cá nhân, Công việc, Học tập).
     *
     * @return danh sách NoteType
     */
    public List<NoteType> getAllNoteTypes() {
        return strategy.findAllTypes();
    }

    /**
     * Lấy chi tiết thể loại ghi chú theo ID.
     *
     * @param id mã thể loại
     * @return đối tượng NoteType
     */
    public NoteType getNoteTypeById(int id) {
        return strategy.findTypeById(id);
    }
}
