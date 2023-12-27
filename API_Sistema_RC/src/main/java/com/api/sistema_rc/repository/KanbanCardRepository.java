package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanCardRepository extends JpaRepository<KanbanCard,Integer> {
    @Query(value = "SELECT * FROM kanban_cards WHERE kanban_column_id = :id ORDER BY index",nativeQuery = true)
    List<KanbanCard> findAllByColumnId(@Param("id") Integer columnId);
    @Query(value = "SELECT * FROM kanban_cards WHERE kanban_column_id = :id AND kanban_inner_card_id IS NULL ORDER BY index",nativeQuery = true)
    List<KanbanCard> findAllByColumnIdAndNotInnerCard(@Param("id") Integer columnId);
    @Query(value = "SELECT * FROM kanban_cards WHERE kanban_inner_card_id = :id ORDER BY index",nativeQuery = true)
    List<KanbanCard> findAllByInnerCardId(@Param("id") Integer innerCardId);
}
