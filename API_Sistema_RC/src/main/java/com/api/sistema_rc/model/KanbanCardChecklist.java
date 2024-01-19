package com.api.sistema_rc.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "kanban_card_checklists")
public class KanbanCardChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToMany(mappedBy = "kanbanCardChecklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<KanbanCardChecklistItem> checklistItems;
    @OneToMany(mappedBy = "kanbanCardChecklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<KanbanDeadline> deadlines;
    @OneToMany(mappedBy = "kanbanCardChecklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<KanbanNotification> kanbanCardChecklistNotifications;
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