package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kanban_cards")
public class KanbanCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_column_id")
    private KanbanColumn kanbanColumn;
    @ManyToOne
    @JoinColumn(name = "kanban_inner_card_id")
    private KanbanCard kanbanInnerCard;
    @Column(length = 255,nullable = false)
    private String title;
    @Column(length = 255)
    private String description;
    @Column(length = 255)
    private String members;
    @Column(nullable = false)
    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KanbanColumn getKanbanColumn() {
        return kanbanColumn;
    }

    public void setKanbanColumn(KanbanColumn kanbanColumn) {
        this.kanbanColumn = kanbanColumn;
    }

    public KanbanCard getKanbanInnerCard() {
        return kanbanInnerCard;
    }

    public void setKanbanInnerCard(KanbanCard kanbanInnerCard) {
        this.kanbanInnerCard = kanbanInnerCard;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
