package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KanbanRoleRepository extends JpaRepository<KanbanRole,Integer> {
}
