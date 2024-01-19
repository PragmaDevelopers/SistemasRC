package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "kanban_card_comments")
public class KanbanCardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToMany(mappedBy = "kanbanCardComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<KanbanNotification> kanbanCardCommentNotifications;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_card_id")
    private KanbanCard kanbanCard;
    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "kanban_card_comment_answered_id")
    private KanbanCardComment kanbanCardCommentAnswered;
    @Column(nullable = false,length = 255)
    private String content;
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime registrationDate;
    @Column(nullable = false)
    private boolean edited;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KanbanCard getKanbanCard() {
        return kanbanCard;
    }

    public void setKanbanCard(KanbanCard kanbanCard) {
        this.kanbanCard = kanbanCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KanbanCardComment getKanbanCommentAnswered() {
        return kanbanCardCommentAnswered;
    }

    public void setKanbanCommentAnswered(KanbanCardComment kanbanCardCommentAnswered) {
        this.kanbanCardCommentAnswered = kanbanCardCommentAnswered;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
