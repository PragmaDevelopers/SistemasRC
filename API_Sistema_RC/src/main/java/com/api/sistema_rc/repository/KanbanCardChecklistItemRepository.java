package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCardChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardChecklistItemRepository extends JpaRepository<KanbanCardChecklistItem,Integer> {
    @Query(value = "SELECT * FROM kanban_card_checklist_items WHERE kanban_card_checklist_id = :id",nativeQuery = true)
    List<KanbanCardChecklistItem> findAllByChecklistId(@Param("id") Integer checkListId);
}
