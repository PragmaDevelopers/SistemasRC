package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.KanbanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanbanNotificationRepository extends JpaRepository<KanbanNotification,Integer> {
    @Query(value = "SELECT * FROM kanban_notifications WHERE user_id = :id ORDER BY registration_date",nativeQuery = true)
    List<KanbanNotification> findAllByUserId(@Param("id") Integer userId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByKanbanId(@Param("id") Integer kanbanId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_deadline_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByKanbanDeadlineId(@Param("id") Integer deadlineId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_column_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByColumnId(@Param("id") Integer columnId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardId(@Param("id") Integer cardId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_comment_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardCommentId(@Param("id") Integer commentId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_tag_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardTagId(@Param("id") Integer tagId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_checklist_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardChecklistId(@Param("id") Integer checklistId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_checklist_item_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardChecklistItemId(@Param("id") Integer checklistItemId);
    @Query(value = "SELECT * FROM kanban_notifications WHERE kanban_card_custom_field_id = :id",nativeQuery = true)
    List<KanbanNotification> findAllByCardCustomFieldId(@Param("id") Integer customFieldId);
}
