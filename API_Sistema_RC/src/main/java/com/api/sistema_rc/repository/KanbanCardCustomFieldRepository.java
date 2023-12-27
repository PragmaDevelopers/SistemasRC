package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCardCustomField;
import com.api.sistema_rc.model.KanbanCardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardCustomFieldRepository extends JpaRepository<KanbanCardCustomField,Integer> {
    @Query(value = "SELECT * FROM kanban_card_custom_fields WHERE kanban_card_id = :id ORDER BY index",nativeQuery = true)
    List<KanbanCardCustomField> findAllByCardId(@Param("id") Integer cardId);
}
