package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCard;
import com.api.sistema_rc.model.KanbanCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public interface KanbanCategoryRepository extends JpaRepository<KanbanCategory,Integer> {
    @Query(value = "SELECT * FROM kanban_categories WHERE name = :name",nativeQuery = true)
    Optional<KanbanCategory> findByName(@Param("name") String name);
}
