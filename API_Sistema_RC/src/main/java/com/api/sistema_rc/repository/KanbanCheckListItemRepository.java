package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCheckList;
import com.api.sistema_rc.model.KanbanCheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanCheckListItemRepository extends JpaRepository<KanbanCheckListItem,Integer> {
    @Query(value = "SELECT * FROM kanban_check_list_item WHERE kanban_check_list_id = :id",nativeQuery = true)
    List<KanbanCheckListItem> findAllByCheckListId(@Param("id") Integer checkListId);
}
