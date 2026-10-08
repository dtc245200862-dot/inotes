package com.codegym.inotes.strategy;

import com.codegym.inotes.model.Note;
import com.codegym.inotes.model.NoteType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chiến lược lưu trữ trên Cơ sở dữ liệu MySQL thông qua JDBC.
 */
public class NoteDBStrategy implements NoteStrategy {

    private static final String SELECT_ALL_NOTES = 
            "SELECT n.id, n.title, n.content, n.type_id, n.created_at, t.name AS type_name, t.description AS type_desc " +
            "FROM notes n LEFT JOIN note_type t ON n.type_id = t.id ORDER BY n.id DESC";

    private static final String SELECT_NOTE_BY_ID = 
            "SELECT n.id, n.title, n.content, n.type_id, n.created_at, t.name AS type_name, t.description AS type_desc " +
            "FROM notes n LEFT JOIN note_type t ON n.type_id = t.id WHERE n.id = ?";

    private static final String SEARCH_NOTES = 
            "SELECT n.id, n.title, n.content, n.type_id, n.created_at, t.name AS type_name, t.description AS type_desc " +
            "FROM notes n LEFT JOIN note_type t ON n.type_id = t.id " +
            "WHERE n.title LIKE ? OR n.content LIKE ? ORDER BY n.id DESC";

    private static final String INSERT_NOTE = 
            "INSERT INTO notes (title, content, type_id, created_at) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_NOTE = 
            "UPDATE notes SET title = ?, content = ?, type_id = ? WHERE id = ?";

    private static final String DELETE_NOTE = 
            "DELETE FROM notes WHERE id = ?";

    private static final String SELECT_ALL_TYPES = 
            "SELECT id, name, description FROM note_type ORDER BY id ASC";

    private static final String SELECT_TYPE_BY_ID = 
            "SELECT id, name, description FROM note_type WHERE id = ?";

    @Override
    public void save(Note note) {
        if (note == null) return;

        if (note.getId() <= 0) {
            // Thêm mới
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_NOTE, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, note.getTitle());
                ps.setString(2, note.getContent());
                if (note.getTypeId() > 0) {
                    ps.setInt(3, note.getTypeId());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                Timestamp createdAt = note.getCreatedAt() != null ? note.getCreatedAt() : new Timestamp(System.currentTimeMillis());
                ps.setTimestamp(4, createdAt);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        note.setId(rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi thêm mới Note vào DB: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Cập nhật
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(UPDATE_NOTE)) {
                ps.setString(1, note.getTitle());
                ps.setString(2, note.getContent());
                if (note.getTypeId() > 0) {
                    ps.setInt(3, note.getTypeId());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setInt(4, note.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Lỗi khi cập nhật Note vào DB: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_NOTE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa Note khỏi DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Note> findAll() {
        List<Note> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_NOTES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đọc danh sách Notes từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Note findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_NOTE_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNote(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm Note theo ID từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Note> search(String keyword) {
        List<Note> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_NOTES)) {
            String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNote(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm Note từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<NoteType> findAllTypes() {
        List<NoteType> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_TYPES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NoteType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách note_type từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public NoteType findTypeById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_TYPE_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NoteType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm NoteType từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getStrategyName() {
        return "Cơ sở dữ liệu (MySQL Database)";
    }

    @Override
    public String getStorageTypeKey() {
        return "DB";
    }

    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String content = rs.getString("content");
        int typeId = rs.getInt("type_id");
        String typeName = rs.getString("type_name");
        Timestamp createdAt = rs.getTimestamp("created_at");
        return new Note(id, title, content, typeId, typeName, createdAt);
    }
}
