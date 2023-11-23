package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban,Integer> {
}
