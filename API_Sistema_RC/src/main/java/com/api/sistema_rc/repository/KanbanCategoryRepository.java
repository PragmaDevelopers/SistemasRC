package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface KanbanCategoryRepository extends JpaRepository<KanbanCategory,Integer> {
}
