package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.Kanban;
import com.api.sistema_rc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban,Integer> {
    @Query(value = "SELECT * FROM kanban WHERE version = :version",nativeQuery = true)
    Optional<Kanban> findByVersion(String version);
}
