package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KanbanCheckListItemRepository extends JpaRepository<KanbanCheckListItem,Integer> {
}
