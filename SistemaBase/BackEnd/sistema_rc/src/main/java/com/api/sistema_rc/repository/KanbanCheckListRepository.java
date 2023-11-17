package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanCheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KanbanCheckListRepository extends JpaRepository<KanbanCheckList,Integer> {
}
