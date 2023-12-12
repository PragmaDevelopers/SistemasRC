package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCardChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardChecklistRepository extends JpaRepository<KanbanCardChecklist,Integer> {
    @Query(value = "SELECT * FROM kanban_card_checklists WHERE kanban_card_id = :id",nativeQuery = true)
    List<KanbanCardChecklist> findAllByCardId(@Param("id") Integer cardId);
}
