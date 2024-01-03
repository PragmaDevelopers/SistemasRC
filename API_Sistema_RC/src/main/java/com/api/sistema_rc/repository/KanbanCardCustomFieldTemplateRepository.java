package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCardCustomField;
import com.api.sistema_rc.model.KanbanCardCustomFieldTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardCustomFieldTemplateRepository extends JpaRepository<KanbanCardCustomFieldTemplate,Integer> {
    @Query(value = "SELECT * FROM kanban_card_custom_field_templates WHERE name = :name ORDER BY index",nativeQuery = true)
    List<KanbanCardCustomFieldTemplate> findAllByName(@Param("name") String name);
    @Query(value = "SELECT * FROM kanban_card_custom_field_templates ORDER BY name,index",nativeQuery = true)
    List<KanbanCardCustomFieldTemplate> findAllOrderByName();
    @Query(value = "DELETE FROM kanban_card_custom_field_templates WHERE name = :name RETURNING id",nativeQuery = true)
    Integer deleteAllByName(@Param("name") String name);
}
