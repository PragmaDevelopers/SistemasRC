package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table
public class KanbanCheckListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_check_list_id")
    private KanbanCheckList kanbanCheckList;
    @Column(length = 255,nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean completed;
}
