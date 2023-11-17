package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCardsRepository extends JpaRepository<KanbanCards,Integer> {
    @Query(value = "SELECT * FROM kanban_cards WHERE kanban_column_id = :id",nativeQuery = true)
    List<KanbanCards> findAllByColumnId(@Param("id") Integer columnId);
    @Query(value = "SELECT * FROM kanban_cards WHERE id = :id AND kanban_column_id = :columnId",nativeQuery = true)
    List<KanbanCards> findByIdAndColumnId(@Param("id") Integer id,@Param("columnId") Integer columnId);

    @Modifying
    @Query(value = "UPDATE kanban_cards SET kanban_column_id = :newColumnId WHERE id = :id AND kanban_column_id = :oldColumnId",nativeQuery = true)
    void updateColumn(@Param("id") Integer id,
                      @Param("oldColumnId") Integer oldColumnId,
                      @Param("newColumnId") Integer newColumnId);
}
