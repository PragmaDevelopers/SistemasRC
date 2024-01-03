package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanDeadline;
import com.api.sistema_rc.model.KanbanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanDeadlineRepository extends JpaRepository<KanbanDeadline,Integer> {
    @Query(value = "SELECT * FROM kanban_deadlines WHERE kanban_card_id = :id",nativeQuery = true)
    Optional<KanbanDeadline> findByCardId(@Param("id") Integer cardId);
    @Query(value = "SELECT * FROM kanban_deadlines WHERE kanban_card_checklist_id = :id",nativeQuery = true)
    List<KanbanDeadline> findAllByChecklistId(@Param("id") Integer checklistId);
    @Query(value = "SELECT * FROM kanban_deadlines WHERE overdue = :overdue",nativeQuery = true)
    List<KanbanDeadline> findAllByOverdue(@Param("overdue") boolean overdue);
}
