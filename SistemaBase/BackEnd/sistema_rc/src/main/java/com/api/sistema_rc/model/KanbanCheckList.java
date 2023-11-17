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
    private KanbanCards kanbanCards;
    @Column(length = 255,nullable = false)
    private String name;
}