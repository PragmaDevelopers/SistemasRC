package com.api.sistema_rc.model;

import com.google.gson.JsonObject;
import jakarta.persistence.*;

@Entity
@Table
public class KanbanColumns {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_id")
    private Kanban kanban;
    @Column(length = 255,nullable = false)
    private String title;

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

    public String toJson(JsonObject obj){
        obj.addProperty("id",getId());
        obj.addProperty("title",getTitle());
        return obj.toString();
    }
}
