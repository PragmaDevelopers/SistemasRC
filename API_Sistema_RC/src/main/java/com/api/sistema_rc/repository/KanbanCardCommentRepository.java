package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardCommentRepository extends JpaRepository<KanbanCardComment,Integer> {
    @Query(value = "SELECT * FROM kanban_card_comments WHERE kanban_card_id = :id ORDER BY registration_date",nativeQuery = true)
    List<KanbanCardComment> findAllByCardId(@Param("id") Integer cardId);
    @Query(value = "SELECT * FROM kanban_card_comments WHERE kanban_card_comment_answered_id = :id ORDER BY registration_date",nativeQuery = true)
    List<KanbanCardComment> findAllByCommentAnsweredId(@Param("id") Integer commentAnsweredId);
}
