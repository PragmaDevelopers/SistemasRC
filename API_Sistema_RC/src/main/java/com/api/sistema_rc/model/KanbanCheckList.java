package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table
public class KanbanCheckList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_card_id")
    private KanbanCard kanbanCard;
    @Column(length = 255,nullable = false)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}