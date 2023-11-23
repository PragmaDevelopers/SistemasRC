package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCard;
import com.api.sistema_rc.model.KanbanCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCheckListRepository extends JpaRepository<KanbanCheckList,Integer> {
    @Query(value = "SELECT * FROM kanban_check_list WHERE kanban_card_id = :id",nativeQuery = true)
    List<KanbanCheckList> findAllByCardId(@Param("id") Integer cardId);
}
