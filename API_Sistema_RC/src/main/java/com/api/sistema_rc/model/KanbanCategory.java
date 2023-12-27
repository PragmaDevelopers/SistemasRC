package com.api.sistema_rc.model;

import com.api.sistema_rc.enums.CategoryName;
import jakarta.persistence.*;

@Entity
@Table(name = "kanban_categories")
public class KanbanCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, unique = true)
    private CategoryName name;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CategoryName getName() {
        return name;
    }

    public void setName(CategoryName name) {
        this.name = name;
    }
}
