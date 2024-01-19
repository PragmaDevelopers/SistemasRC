package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "kanban_card_checklist_items")
public class KanbanCardChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToMany(mappedBy = "kanbanCardChecklistItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<KanbanNotification> kanbanCardChecklistItemNotifications;
    @ManyToOne
    @JoinColumn(nullable = false,name = "kanban_card_checklist_id")
    private KanbanCardChecklist kanbanCardChecklist;
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

    public KanbanCardChecklist getKanbanChecklist() {
        return kanbanCardChecklist;
    }

    public void setKanbanChecklist(KanbanCardChecklist kanbanCardChecklist) {
        this.kanbanCardChecklist = kanbanCardChecklist;
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
