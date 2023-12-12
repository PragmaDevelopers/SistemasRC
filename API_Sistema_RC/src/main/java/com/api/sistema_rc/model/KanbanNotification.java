package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kanban_notifications")
public class KanbanNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(length = 255,nullable = false)
    private String type;
    @Column(nullable = false)
    private String aux;
    @Column(length = 255,nullable = false)
    private String message;
    @Column(nullable = false)
    private boolean viewed;

    public KanbanNotification(Integer id, User user, String type, String aux, String message, boolean viewed) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.aux = aux;
        this.message = message;
        this.viewed = viewed;
    }

    public KanbanNotification() {

    }

    public KanbanNotification(KanbanNotification kanbanNotification) {
        this.id = kanbanNotification.getId();
        this.user = kanbanNotification.getUser();
        this.type = kanbanNotification.getType();
        this.aux = kanbanNotification.getAux();
        this.message = kanbanNotification.getMessage();
        this.viewed = kanbanNotification.isViewed();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAux() {
        return aux;
    }

    public void setAux(String aux) {
        this.aux = aux;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
