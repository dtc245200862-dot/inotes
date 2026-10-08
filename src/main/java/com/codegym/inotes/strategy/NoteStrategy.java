package com.codegym.inotes.strategy;

import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;

import java.util.List;

/**
 * Interface Chiến lược lưu trữ NoteStrategy theo mẫu thiết kế Strategy Pattern.
 * Định nghĩa các hợp đồng chuẩn để thao tác dữ liệu độc lập với tầng lưu trữ vật lý.
 */
public interface NoteStrategy {

    /**
     * Lưu mới (nếu id = 0) hoặc cập nhật (nếu id > 0) một ghi chú.
     *
     * @param note đối tượng ghi chú cần lưu
     */
    void save(Note note);

    /**
     * Xóa bỏ một ghi chú theo ID.
     *
     * @param id mã ghi chú cần xóa
     */
    void delete(int id);

    /**
     * Lấy toàn bộ danh sách ghi chú.
     *
     * @return danh sách các ghi chú
     */
    List<Note> findAll();

    /**
     * Tìm một ghi chú theo ID.
     *
     * @param id mã ghi chú
     * @return đối tượng ghi chú hoặc null nếu không tìm thấy
     */
    Note findById(int id);

    /**
     * Tìm kiếm ghi chú theo từ khóa xuất hiện trong tiêu đề hoặc nội dung.
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách các ghi chú thỏa mãn
     */
    List<Note> search(String keyword);

    /**
     * Lấy danh sách các thể loại ghi chú (note_type).
     *
     * @return danh sách thể loại
     */
    List<NoteType> findAllTypes();

    /**
     * Tìm thể loại theo ID.
     *
     * @param id mã thể loại
     * @return đối tượng NoteType hoặc null
     */
    NoteType findTypeById(int id);

    /**
     * Tên mô tả của chiến lược lưu trữ (Ví dụ: "Cơ sở dữ liệu MySQL", "Tập tin Văn bản (File txt)").
     *
     * @return tên chiến lược
     */
    String getStrategyName();

    /**
     * Khóa mã định danh chiến lược ("DB" hoặc "FILE").
     *
     * @return mã chiến lược
     */
    String getStorageTypeKey();
}
