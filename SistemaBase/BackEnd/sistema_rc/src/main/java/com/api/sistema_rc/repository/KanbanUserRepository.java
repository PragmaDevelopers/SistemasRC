package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.Kanban;
import com.api.sistema_rc.model.KanbanUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanUserRepository extends JpaRepository<KanbanUser,Integer> {
    @Query(value = "SELECT * FROM kanban_user WHERE user_id = :id",nativeQuery = true)
    List<KanbanUser> findAllByUserId(@Param("id") Integer userid);
}
