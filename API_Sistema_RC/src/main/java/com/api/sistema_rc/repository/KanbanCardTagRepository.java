package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCard;
import com.api.sistema_rc.model.KanbanCardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardTagRepository extends JpaRepository<KanbanCardTag,Integer> {
    @Query(value = "SELECT * FROM kanban_card_tags WHERE kanban_card_id = :id",nativeQuery = true)
    List<KanbanCardTag> findAllByCardId(@Param("id") Integer cardId);
    @Query(value = "SELECT * FROM kanban_card_tags WHERE name = :name",nativeQuery = true)
    KanbanCard findByName(@Param("name") String name);
}
