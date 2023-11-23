package com.api.sistema_rc.model;

import com.api.sistema_rc.enums.KanbanRoleName;
import com.api.sistema_rc.enums.RoleName;
import jakarta.persistence.*;

@Entity
@Table(name = "kanban_roles")
public class KanbanRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, unique = true)
    private KanbanRoleName name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KanbanRoleName getName() {
        return name;
    }

    public void setName(KanbanRoleName name) {
        this.name = name;
    }
}
