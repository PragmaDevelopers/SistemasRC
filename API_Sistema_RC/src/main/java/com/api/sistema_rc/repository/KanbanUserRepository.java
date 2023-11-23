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
    @Query(value = "SELECT * FROM kanban_users WHERE user_id = :id",nativeQuery = true)
    List<KanbanUser> findAllByUserId(@Param("id") Integer userId);
    @Query(value = "SELECT * FROM kanban_users WHERE kanban_id = :id",nativeQuery = true)
    List<KanbanUser> findAllByKanbanId(@Param("id") Integer kanbanId);
    @Query(value = "SELECT * FROM kanban_users WHERE kanban_id = :kanbanId AND user_id = :userId",nativeQuery = true)
    KanbanUser findByKanbanIdAndUserId(@Param("kanbanId") Integer kanbanId,@Param("userId") Integer userId);
    @Query(value = "UPDATE kanban_users set permission_level = :level,kanban_role_id = :roleId WHERE id = :kanbanUserId returning id",nativeQuery = true)
    Integer updateById(@Param("level") String level,@Param("roleId") Integer roleId,@Param("kanbanUserId") Integer kanbanUserId);
}
