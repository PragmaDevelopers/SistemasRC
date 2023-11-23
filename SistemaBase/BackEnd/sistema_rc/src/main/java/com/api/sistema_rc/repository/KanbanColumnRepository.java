package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanColumnRepository extends JpaRepository<KanbanColumn,Integer> {
    @Query(value = "SELECT * FROM kanban_columns WHERE kanban_id = :id ORDER BY index",nativeQuery = true)
    List<KanbanColumn> findAllByKanbanId(@Param("id") Integer kanbanId);
}
