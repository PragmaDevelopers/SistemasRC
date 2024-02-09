package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.ClientTemplate;
import com.api.sistema_rc.model.KanbanCardChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientTemplateRepository extends JpaRepository<ClientTemplate,Integer> {
    @Query(value = "SELECT * FROM client_templates WHERE value = :value",nativeQuery = true)
    List<ClientTemplate> findAllByValue(@Param("value") boolean value);
}
