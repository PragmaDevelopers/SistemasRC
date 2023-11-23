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
    private boolean completed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KanbanCheckList getKanbanCheckList() {
        return kanbanCheckList;
    }

    public void setKanbanCheckList(KanbanCheckList kanbanCheckList) {
        this.kanbanCheckList = kanbanCheckList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
