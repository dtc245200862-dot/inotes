package com.codegym.inotes;

import com.codegym.inotes.controller.NoteManagement;
import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;
import com.codegym.inotes.strategy.NoteDBStrategy;
import com.codegym.inotes.strategy.NoteFileStrategy;

import java.util.List;

/**
 * Chương trình Demo Console minh họa hoạt động của Strategy Pattern trong ứng dụng iNotes.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("     iNotes - DEMO MẪU THIẾT KẾ STRATEGY PATTERN (CodeGym)     ");
        System.out.println("================================================================");

        NoteManagement noteManagement = NoteManagement.getInstance();

        // -------------------------------------------------------------
        // PHẦN 1: Chạy với Chiến lược Cơ sở dữ liệu MySQL (NoteDBStrategy)
        // -------------------------------------------------------------
        System.out.println("\n>>> [1] KHỞI CHẠY VỚI TẦNG LƯU TRỮ CƠ SỞ DỮ LIỆU MYSQL:");
        noteManagement.setStrategy(new NoteDBStrategy());
        System.out.println("-> Tầng lưu trữ hiện tại: " + noteManagement.getStrategy().getStrategyName());

        List<NoteType> dbTypes = noteManagement.getAllNoteTypes();
        System.out.println("\nDanh mục thể loại từ MySQL:");
        for (NoteType t : dbTypes) {
            System.out.println("   [" + t.getId() + "] " + t.getName() + " - " + t.getDescription());
        }

        List<Note> dbNotes = noteManagement.getAllNotes();
        System.out.println("\nDanh sách ghi chú từ MySQL (Tổng: " + dbNotes.size() + "):");
        for (Note n : dbNotes) {
            System.out.println("   - ID " + n.getId() + ": [" + n.getTypeName() + "] " + n.getTitle());
        }

        // -------------------------------------------------------------
        // PHẦN 2: Hoán đổi Runtime sang Chiến lược Tập tin (NoteFileStrategy)
        // -------------------------------------------------------------
        System.out.println("\n----------------------------------------------------------------");
        System.out.println(">>> [2] CHUYỂN ĐỔI LINH HOẠT SANG TẦNG LƯU TRỮ TẬP TIN (FILE TXT):");
        noteManagement.setStrategy(new NoteFileStrategy());
        System.out.println("-> Tầng lưu trữ hiện tại: " + noteManagement.getStrategy().getStrategyName());

        // Thêm mới một ghi chú vào file
        System.out.println("\nThêm mới một ghi chú vào tệp tin:");
        Note newFileNote = noteManagement.addNote("Ghi chú thử nghiệm File", "Nội dung ghi chú được ghi trực tiếp ra tệp tin", 1);
        System.out.println("   Đã lưu thành công ghi chú ID: " + newFileNote.getId() + " - " + newFileNote.getTitle());

        // Tìm kiếm trên file
        System.out.println("\nTìm kiếm từ khóa 'thử nghiệm' trên tệp tin:");
        List<Note> searchResult = noteManagement.searchNotes("thử nghiệm");
        for (Note n : searchResult) {
            System.out.println("   Kết quả tìm thấy: ID " + n.getId() + " - " + n.getTitle() + " | Nội dung: " + n.getContent());
        }

        // Đọc lại toàn bộ danh sách từ file
        List<Note> fileNotes = noteManagement.getAllNotes();
        System.out.println("\nDanh sách ghi chú từ Tệp tin (Tổng: " + fileNotes.size() + "):");
        for (Note n : fileNotes) {
            System.out.println("   - ID " + n.getId() + ": [" + n.getTypeName() + "] " + n.getTitle());
        }

        System.out.println("\n================================================================");
        System.out.println("   KẾT LUẬN: STRATEGY PATTERN HOẠT ĐỘNG HOÀN TOÀN CHÍNH XÁC!   ");
        System.out.println("   Caller không cần sửa code logic khi chuyển đổi nơi lưu trữ! ");
        System.out.println("================================================================");
    }
}
