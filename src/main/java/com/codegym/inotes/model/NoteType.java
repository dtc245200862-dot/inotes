package com.codegym.inotes.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Đại diện cho bảng note_type (Phân loại ghi chú).
 */
public class NoteType implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;

    public NoteType() {}

    public NoteType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public NoteType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteType noteType = (NoteType) o;
        return id == noteType.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NoteType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
