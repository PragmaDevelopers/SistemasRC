package com.api.sistema_rc.model;

import com.google.gson.JsonObject;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "kanban_columns")
public class KanbanColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_id")
    private Kanban kanban;
    @Column(length = 255,nullable = false)
    private String title;
    @Column(nullable = false)
    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Kanban getKanban() {
        return kanban;
    }

    public void setKanban(Kanban kanban) {
        this.kanban = kanban;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
