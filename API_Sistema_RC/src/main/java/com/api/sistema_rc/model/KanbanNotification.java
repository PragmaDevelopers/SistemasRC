package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kanban_notifications")
public class KanbanNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    private User user;
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime registration_date;
    @ManyToOne
    @JoinColumn(nullable = false,name = "sender_user_id")
    private User senderUser;
    @ManyToOne
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_category_id")
    private KanbanCategory kanbanCategory;
    @Column(length = 255,nullable = false)
    private String message;
    @Column(nullable = false)
    private boolean viewed;
    @ManyToOne
    @JoinColumn(name = "kanban_id")
    private Kanban kanban;
    @ManyToOne
    @JoinColumn(name = "kanban_column_id")
    private KanbanColumn kanbanColumn;
    @ManyToOne
    @JoinColumn(name = "kanban_card_id")
    private KanbanCard kanbanCard;
    @ManyToOne
    @JoinColumn(name = "kanban_card_checklist_id")
    private KanbanCardChecklist kanbanCardChecklist;
    @ManyToOne
    @JoinColumn(name = "kanban_card_checklist_item_id")
    private KanbanCardChecklistItem kanbanCardChecklistItem;
    @ManyToOne
    @JoinColumn(name = "kanban_card_comment_id")
    private KanbanCardComment kanbanCardComment;
    @ManyToOne
    @JoinColumn(name = "kanban_card_tag_id")
    private KanbanCardTag kanbanCardTag;
    @ManyToOne
    @JoinColumn(name = "kanban_card_custom_field_id")
    private KanbanCardCustomField kanbanCardCustomField;
    @ManyToOne
    @JoinColumn(name = "kanban_deadline_id")
    private KanbanDeadline kanbanDeadline;

    public KanbanNotification() {

    }

    public KanbanNotification(KanbanNotification kanbanNotification) {
        this.id = kanbanNotification.getId();
        this.user = kanbanNotification.getUser();
        this.registration_date = kanbanNotification.getRegistration_date();
        this.senderUser = kanbanNotification.getSenderUser();
        this.recipientUser = kanbanNotification.getRecipientUser();
        this.kanbanCategory = kanbanNotification.getKanbanCategory();
        this.message = kanbanNotification.getMessage();
        this.viewed = kanbanNotification.isViewed();
        this.kanban = kanbanNotification.getKanban();
        this.kanbanColumn = kanbanNotification.getKanbanColumn();
        this.kanbanCard = kanbanNotification.getKanbanCard();
        this.kanbanCardChecklist = kanbanNotification.getKanbanCardChecklist();
        this.kanbanCardChecklistItem = kanbanNotification.getKanbanCardChecklistItem();
        this.kanbanCardComment = kanbanNotification.getKanbanCardComment();
        this.kanbanCardTag = kanbanNotification.getKanbanCardTag();
        this.kanbanCardCustomField = kanbanNotification.getKanbanCardCustomField();
        this.kanbanDeadline = kanbanNotification.getKanbanDeadline();
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

    public LocalDateTime getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(LocalDateTime registration_date) {
        this.registration_date = registration_date;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public KanbanCategory getKanbanCategory() {
        return kanbanCategory;
    }

    public void setKanbanCategory(KanbanCategory kanbanCategory) {
        this.kanbanCategory = kanbanCategory;
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

    public Kanban getKanban() {
        return kanban;
    }

    public void setKanban(Kanban kanban) {
        this.kanban = kanban;
    }

    public KanbanColumn getKanbanColumn() {
        return kanbanColumn;
    }

    public void setKanbanColumn(KanbanColumn kanbanColumn) {
        this.kanbanColumn = kanbanColumn;
    }

    public KanbanCard getKanbanCard() {
        return kanbanCard;
    }

    public void setKanbanCard(KanbanCard kanbanCard) {
        this.kanbanCard = kanbanCard;
    }

    public KanbanCardChecklist getKanbanCardChecklist() {
        return kanbanCardChecklist;
    }

    public void setKanbanCardChecklist(KanbanCardChecklist kanbanCardChecklist) {
        this.kanbanCardChecklist = kanbanCardChecklist;
    }

    public KanbanCardChecklistItem getKanbanCardChecklistItem() {
        return kanbanCardChecklistItem;
    }

    public void setKanbanCardChecklistItem(KanbanCardChecklistItem kanbanCardChecklistItem) {
        this.kanbanCardChecklistItem = kanbanCardChecklistItem;
    }

    public KanbanCardComment getKanbanCardComment() {
        return kanbanCardComment;
    }

    public void setKanbanCardComment(KanbanCardComment kanbanCardComment) {
        this.kanbanCardComment = kanbanCardComment;
    }

    public KanbanCardTag getKanbanCardTag() {
        return kanbanCardTag;
    }

    public void setKanbanCardTag(KanbanCardTag kanbanCardTag) {
        this.kanbanCardTag = kanbanCardTag;
    }

    public KanbanCardCustomField getKanbanCardCustomField() {
        return kanbanCardCustomField;
    }

    public void setKanbanCardCustomField(KanbanCardCustomField kanbanCardCustomField) {
        this.kanbanCardCustomField = kanbanCardCustomField;
    }

    public KanbanDeadline getKanbanDeadline() {
        return kanbanDeadline;
    }

    public void setKanbanDeadline(KanbanDeadline kanbanDeadline) {
        this.kanbanDeadline = kanbanDeadline;
    }
}
