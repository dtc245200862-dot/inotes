package com.codegym.inotes.model;

import com.codegym.inotes.controller.NoteManagement;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Lớp Model Note đại diện cho bản ghi ghi chú.
 * Cung cấp đầy đủ các phương thức getter/setter theo Bước 2 của yêu cầu.
 */
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String content;
    private int typeId;
    private String typeName;
    private Timestamp createdAt;

    public Note() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Note(int id, String title, String content, int typeId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.typeId = typeId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Note(String title, String content, int typeId) {
        this.title = title;
        this.content = content;
        this.typeId = typeId;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Note(int id, String title, String content, int typeId, String typeName, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.typeId = typeId;
        this.typeName = typeName;
        this.createdAt = createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    // Các getter / setter theo đúng Bước 2
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(createdAt);
    }

    /**
     * Lưu/cập nhật ghi chú vào hệ thống lưu trữ hiện tại thông qua NoteManagement.
     */
    public void save() {
        NoteManagement.getInstance().saveNote(this);
    }

    /**
     * Xóa ghi chú theo ID từ hệ thống lưu trữ hiện tại.
     *
     * @param id mã ghi chú cần xóa
     */
    public static void delete(int id) {
        NoteManagement.getInstance().deleteNote(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
