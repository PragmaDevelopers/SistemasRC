package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "kanban_deadlines")
public class KanbanDeadline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_category_id")
    private KanbanCategory kanbanCategory;
    @ManyToOne
    @JoinColumn(name = "kanban_card_id")
    private KanbanCard kanbanCard;
    @ManyToOne
    @JoinColumn(name = "kanban_card_checklist_id")
    private KanbanCardChecklist kanbanCardChecklist;
    @Column(nullable = false)
    private boolean overdue;
    @ManyToOne
    @JoinColumn(name = "action_kanban_id")
    private Kanban actionKanban;
    @ManyToOne
    @JoinColumn(name = "action_kanban_column_id")
    private KanbanColumn actionKanbanColumn;
    @ManyToOne
    @JoinColumn(name = "action_kanban_card_id")
    private KanbanCard actionKanbanCard;
    @ManyToOne
    @JoinColumn(name = "action_kanban_card_checklist_id")
    private KanbanCardChecklist actionKanbanCardChecklist;
    @ManyToOne
    @JoinColumn(name = "action_kanban_card_checklist_item_id")
    private KanbanCardChecklistItem actionKanbanCardChecklistItem;
    @ManyToOne
    @JoinColumn(name = "action_kanban_card_comment_id")
    private KanbanCardComment actionKanbanCardComment;
    @ManyToOne
    @JoinColumn(name = "action_kanban_card_tag_id")
    private KanbanCardTag actionKanbanCardTag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public KanbanCategory getKanbanCategory() {
        return kanbanCategory;
    }

    public void setKanbanCategory(KanbanCategory kanbanCategory) {
        this.kanbanCategory = kanbanCategory;
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

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public Kanban getActionKanban() {
        return actionKanban;
    }

    public void setActionKanban(Kanban actionKanban) {
        this.actionKanban = actionKanban;
    }

    public KanbanColumn getActionKanbanColumn() {
        return actionKanbanColumn;
    }

    public void setActionKanbanColumn(KanbanColumn actionKanbanColumn) {
        this.actionKanbanColumn = actionKanbanColumn;
    }

    public KanbanCard getActionKanbanCard() {
        return actionKanbanCard;
    }

    public void setActionKanbanCard(KanbanCard actionKanbanCard) {
        this.actionKanbanCard = actionKanbanCard;
    }

    public KanbanCardChecklist getActionKanbanCardChecklist() {
        return actionKanbanCardChecklist;
    }

    public void setActionKanbanCardChecklist(KanbanCardChecklist actionKanbanCardChecklist) {
        this.actionKanbanCardChecklist = actionKanbanCardChecklist;
    }

    public KanbanCardChecklistItem getActionKanbanCardChecklistItem() {
        return actionKanbanCardChecklistItem;
    }

    public void setActionKanbanCardChecklistItem(KanbanCardChecklistItem actionKanbanCardChecklistItem) {
        this.actionKanbanCardChecklistItem = actionKanbanCardChecklistItem;
    }

    public KanbanCardComment getActionKanbanCardComment() {
        return actionKanbanCardComment;
    }

    public void setActionKanbanCardComment(KanbanCardComment actionKanbanCardComment) {
        this.actionKanbanCardComment = actionKanbanCardComment;
    }

    public KanbanCardTag getActionKanbanCardTag() {
        return actionKanbanCardTag;
    }

    public void setActionKanbanCardTag(KanbanCardTag actionKanbanCardTag) {
        this.actionKanbanCardTag = actionKanbanCardTag;
    }
}
