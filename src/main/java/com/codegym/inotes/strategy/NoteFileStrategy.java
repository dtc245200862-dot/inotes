package com.codegym.inotes.strategy;

import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

/**
 * Chiến lược lưu trữ trên Tệp tin văn bản (File System / CSV / Delimited txt).
 * Định dạng dòng: id|title|content|typeId|createdAt
 */
public class NoteFileStrategy implements NoteStrategy {

    private final File dataFile;
    private final List<NoteType> defaultTypes;

    public NoteFileStrategy() {
        this(resolveDefaultFile());
    }

    public NoteFileStrategy(File file) {
        this.dataFile = file;
        this.defaultTypes = new ArrayList<>();
        this.defaultTypes.add(new NoteType(1, "Cá nhân", "Ghi chú cho các công việc và sinh hoạt cá nhân"));
        this.defaultTypes.add(new NoteType(2, "Công việc", "Nhiệm vụ, lịch họp và công việc dự án"));
        this.defaultTypes.add(new NoteType(3, "Học tập", "Kiến thức, tài liệu và bài tập ôn luyện"));

        ensureFileExists();
    }

    private static File resolveDefaultFile() {
        // Ưu tiên thư mục dự án "data/inotes_data.txt" hoặc thư mục home
        String userDir = System.getProperty("user.dir");
        File candidate = new File(userDir, "data" + File.separator + "inotes_data.txt");
        if (candidate.getParentFile() != null && candidate.getParentFile().canWrite()) {
            return candidate;
        }
        String homeDir = System.getProperty("user.home");
        return new File(homeDir, "inotes_data.txt");
    }

    private void ensureFileExists() {
        try {
            if (dataFile.getParentFile() != null && !dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }
            if (!dataFile.exists() || dataFile.length() == 0) {
                dataFile.createNewFile();
                // Khởi tạo một số dữ liệu mẫu
                List<Note> sampleNotes = new ArrayList<>();
                sampleNotes.add(new Note(1, "Họp giao ban đầu tuần (File)", "Báo cáo tiến độ hoàn thành các chức năng iNotes và kế hoạch tuần mới", 2));
                sampleNotes.add(new Note(2, "Ôn tập Design Pattern Servlet (File)", "Học sâu về Strategy Pattern, Factory Pattern, MVC và JDBC DAO", 3));
                sampleNotes.add(new Note(3, "Mua sắm cuối tuần (File)", "Mua sữa, cà phê, bánh mỳ và đồ dùng gia đình", 1));
                writeAllNotes(sampleNotes);
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi tạo tệp tin lưu trữ: " + e.getMessage());
        }
    }

    @Override
    public synchronized void save(Note note) {
        if (note == null) return;
        List<Note> notes = readAllNotes();

        if (note.getId() <= 0) {
            // Thêm mới: Tự động tìm ID lớn nhất + 1
            int maxId = 0;
            for (Note n : notes) {
                if (n.getId() > maxId) {
                    maxId = n.getId();
                }
            }
            note.setId(maxId + 1);
            if (note.getCreatedAt() == null) {
                note.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }
            notes.add(note);
        } else {
            // Cập nhật
            boolean updated = false;
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getId() == note.getId()) {
                    Note existing = notes.get(i);
                    existing.setTitle(note.getTitle());
                    existing.setContent(note.getContent());
                    existing.setTypeId(note.getTypeId());
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                notes.add(note);
            }
        }

        writeAllNotes(notes);
    }

    @Override
    public synchronized void delete(int id) {
        List<Note> notes = readAllNotes();
        notes.removeIf(n -> n.getId() == id);
        writeAllNotes(notes);
    }

    @Override
    public synchronized List<Note> findAll() {
        List<Note> list = readAllNotes();
        // Sắp xếp ID giảm dần (mới nhất lên đầu)
        list.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return list;
    }

    @Override
    public synchronized Note findById(int id) {
        List<Note> notes = readAllNotes();
        for (Note n : notes) {
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Note> search(String keyword) {
        List<Note> all = findAll();
        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }
        String lowerKw = keyword.trim().toLowerCase();
        List<Note> result = new ArrayList<>();
        for (Note n : all) {
            boolean matchTitle = n.getTitle() != null && n.getTitle().toLowerCase().contains(lowerKw);
            boolean matchContent = n.getContent() != null && n.getContent().toLowerCase().contains(lowerKw);
            if (matchTitle || matchContent) {
                result.add(n);
            }
        }
        return result;
    }

    @Override
    public List<NoteType> findAllTypes() {
        return new ArrayList<>(defaultTypes);
    }

    @Override
    public NoteType findTypeById(int id) {
        for (NoteType type : defaultTypes) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getStrategyName() {
        return "Tập tin Văn bản (File System txt/csv)";
    }

    @Override
    public String getStorageTypeKey() {
        return "FILE";
    }

    public File getDataFile() {
        return dataFile;
    }

    // --- Các hàm tiện ích đọc / ghi file phân tách dấu '|' ---

    private List<Note> readAllNotes() {
        List<Note> list = new ArrayList<>();
        if (!dataFile.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(dataFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Note note = parseLine(line);
                if (note != null) {
                    list.add(note);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file ghi chú: " + e.getMessage());
        }
        return list;
    }

    private void writeAllNotes(List<Note> notes) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dataFile), StandardCharsets.UTF_8))) {
            for (Note note : notes) {
                writer.write(formatLine(note));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file ghi chú: " + e.getMessage());
        }
    }

    private Note parseLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String title = unescapeText(parts[1]);
            String content = unescapeText(parts[2]);
            int typeId = Integer.parseInt(parts[3].trim());
            Timestamp createdAt = null;
            if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                try {
                    createdAt = Timestamp.valueOf(parts[4].trim());
                } catch (Exception ex) {
                    createdAt = new Timestamp(System.currentTimeMillis());
                }
            } else {
                createdAt = new Timestamp(System.currentTimeMillis());
            }

            NoteType type = findTypeById(typeId);
            String typeName = type != null ? type.getName() : "Khác";

            return new Note(id, title, content, typeId, typeName, createdAt);
        } catch (Exception e) {
            System.err.println("Bỏ qua dòng dữ liệu lỗi trong file: " + line);
            return null;
        }
    }

    private String formatLine(Note note) {
        int id = note.getId();
        String title = escapeText(note.getTitle());
        String content = escapeText(note.getContent());
        int typeId = note.getTypeId();
        String createdStr = note.getCreatedAt() != null ? note.getCreatedAt().toString() : "";
        return String.format("%d|%s|%s|%d|%s", id, title, content, typeId, createdStr);
    }

    private String escapeText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("|", "\\p")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }

    private String unescapeText(String text) {
        if (text == null) return "";
        return text.replace("\\n", "\n")
                   .replace("\\p", "|")
                   .replace("\\\\", "\\");
    }
}
