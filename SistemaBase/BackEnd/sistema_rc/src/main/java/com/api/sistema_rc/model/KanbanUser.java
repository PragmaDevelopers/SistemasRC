package com.api.sistema_rc.model;

import com.google.gson.JsonObject;
import jakarta.persistence.*;

@Entity
@Table
public class KanbanUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "kanban_id")
    private Kanban kanban;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(length = 255,nullable = false)
    private String permissionLevel;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
