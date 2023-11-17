package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanColumns;
import com.api.sistema_rc.model.KanbanUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanColumnsRepository extends JpaRepository<KanbanColumns,Integer> {
    @Query(value = "SELECT * FROM kanban_columns WHERE kanban_id = :id",nativeQuery = true)
    List<KanbanColumns> findAllByKanbanId(@Param("id") Integer kanbanId);
}
