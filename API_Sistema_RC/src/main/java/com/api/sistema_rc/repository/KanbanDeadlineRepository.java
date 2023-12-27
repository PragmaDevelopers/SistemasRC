package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanDeadline;
import com.api.sistema_rc.model.KanbanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanDeadlineRepository extends JpaRepository<KanbanDeadline,Integer> {
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_tag_id = :id",nativeQuery = true)
    List<KanbanDeadline> findAllByCardId(@Param("id") Integer tagId);
}
