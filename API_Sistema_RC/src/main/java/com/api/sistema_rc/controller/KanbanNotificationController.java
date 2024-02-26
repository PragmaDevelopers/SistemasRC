package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class KanbanNotificationController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @GetMapping(path = "/private/user/notifications/count")
    public ResponseEntity<String> getCountNotification(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);
        int countNotification = kanbanNotificationRepository.findAllCountByUserId(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(countNotification));
    }
    @GetMapping(path = "/private/user/notifications")
    public ResponseEntity<String> getNotifications(@RequestHeader("Authorization") String token,
                                                   @RequestParam(required = false,defaultValue = "1") int page){
        Integer user_id = tokenService.validateToken(token);

        List<KanbanNotification> kanbanNotificationList = kanbanNotificationRepository.findAllByUserId(user_id,10 * (page - 1));

        JsonArray notificationArr = new JsonArray();
        kanbanNotificationList.forEach(notification->{
            JsonObject notificationObj = new JsonObject();
            notificationObj.addProperty("id",notification.getId());
            notificationObj.addProperty("registrationDate",notification.getRegistrationDate().toString());
            notificationObj.addProperty("message",notification.getMessage());
            notificationObj.addProperty("viewed",notification.isViewed());
            notificationObj.addProperty("category",notification.getKanbanCategory().getName().name());
            notificationObj.addProperty("sender_user_id",notification.getSenderUser().getId());
            notificationObj.addProperty("sender_user_name",notification.getSenderUser().getName());
            if(notification.getSenderUser().getProfilePicture() == null){
                notificationObj.addProperty("sender_user_profilePicture",(String) null);
            }else{
                try {
                    byte[] bytes = notification.getSenderUser().getProfilePicture().getBytes(1,(int) notification.getSenderUser().getProfilePicture().length());
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    notificationObj.addProperty("sender_user_profilePicture","data:image/"+notification.getSenderUser().getPictureFormat()+";base64,"+encoded);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            CategoryName category = notification.getKanbanCategory().getName();
            if(category.equals(CategoryName.KANBAN_INVITE) ||
                    category.equals(CategoryName.KANBAN_UNINVITE)
            ){
                if(notification.getRecipientUser().getProfilePicture() == null){
                    notificationObj.addProperty("recipient_user_profilePicture",(String) null);
                }else{
                    try {
                        byte[] bytes = notification.getRecipientUser().getProfilePicture().getBytes(1,(int) notification.getRecipientUser().getProfilePicture().length());
                        String encoded = Base64.getEncoder().encodeToString(bytes);
                        notificationObj.addProperty("recipient_user_profilePicture","data:image/"+notification.getRecipientUser().getPictureFormat()+";base64,"+encoded);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                notificationObj.addProperty("recipient_user_id",notification.getRecipientUser().getId());
                notificationObj.addProperty("recipient_user_name",notification.getRecipientUser().getName());
                notificationObj.addProperty("changed_id",notification.getKanban().getId());
            }

            if(category.equals(CategoryName.KANBAN_DELETE) ||
                category.equals(CategoryName.COLUMN_DELETE) ||
                category.equals(CategoryName.CARD_DELETE) ||
                category.equals(CategoryName.CARDTAG_DELETE) ||
                category.equals(CategoryName.CARDCOMMENT_DELETE) ||
                category.equals(CategoryName.CARDCHECKLIST_DELETE) ||
                category.equals(CategoryName.CARDCHECKLISTITEM_DELETE) ||
                category.equals(CategoryName.CARDCUSTOMFIELD_DELETE) ||
                category.equals(CategoryName.CARDDEADLINE_DELETE) ||
                category.equals(CategoryName.CARDCHECKLISTDEADLINE_DELETE) ||
                category.equals(CategoryName.CLIENTTEMPLATE_DELETE) ||
                category.equals(CategoryName.PDFTEMPLATE_DELETE)
            ){
                notificationObj.addProperty("changed_id",(String) null);
            }else{
                if(category.equals(CategoryName.KANBAN_CREATE) || category.equals(CategoryName.KANBAN_UPDATE)){
                    if(notification.getKanban() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else{
                        notificationObj.addProperty("changed_id",notification.getKanban().getId());
                    }
                }else if(
                    category.equals(CategoryName.COLUMN_CREATE) ||
                    category.equals(CategoryName.COLUMN_UPDATE) ||
                    category.equals(CategoryName.COLUMN_MOVE)
                ){
                    if(notification.getKanbanColumn() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else{
                        notificationObj.addProperty("changed_id",notification.getKanbanColumn().getId());
                    }
                }else if(category.equals(CategoryName.CARD_CREATE) ||
                        category.equals(CategoryName.CARD_UPDATE) ||
                        category.equals(CategoryName.CARD_MOVE)
                ){
                    if(notification.getKanbanCard() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCard().getId());
                    }
                }else if(
                    category.equals(CategoryName.CARDTAG_CREATE) ||
                    category.equals(CategoryName.CARDTAG_UPDATE)
                ){
                    if(notification.getKanbanCardTag() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCardTag().getId());
                    }
                }else if(
                    category.equals(CategoryName.CARDCOMMENT_CREATE) ||
                    category.equals(CategoryName.CARDCOMMENTANSWERED_CREATE) ||
                    category.equals(CategoryName.CARDCOMMENT_UPDATE)
                ){
                    if(notification.getKanbanCardComment() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCardComment().getId());
                    }
                }else if(category.equals(CategoryName.CARDCHECKLIST_CREATE) ||
                        category.equals(CategoryName.CARDCHECKLIST_UPDATE)
                ){
                    if(notification.getKanbanCardChecklist() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCardChecklist().getId());
                    }
                }else if(category.equals(CategoryName.CARDCHECKLISTITEM_CREATE) ||
                        category.equals(CategoryName.CARDCHECKLISTITEM_UPDATE)
                ){
                    if(notification.getKanbanCardChecklistItem() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCardChecklistItem().getId());
                    }
                }else if(
                    category.equals(CategoryName.CARDCUSTOMFIELD_CREATE) ||
                    category.equals(CategoryName.CARDCUSTOMFIELD_UPDATE)
                ){
                    if(notification.getKanbanCardCustomField() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanCardCustomField().getId());
                    }
                }else if(
                        category.equals(CategoryName.CARDDEADLINE_CREATE) ||
                        category.equals(CategoryName.CARDDEADLINE_UPDATE) ||
                        category.equals(CategoryName.CARDCHECKLISTDEADLINE_CREATE) ||
                        category.equals(CategoryName.CARDCHECKLISTDEADLINE_UPDATE)
                ){
                    if(notification.getKanbanDeadline() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getKanbanDeadline().getId());
                    }
                }else if(
                        category.equals(CategoryName.CLIENTTEMPLATE_CREATE)
                ){
                    if(notification.getClientTemplate() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getClientTemplate().getId());
                    }
                }else if(
                        category.equals(CategoryName.PDFTEMPLATE_CREATE)
                ){
                    if(notification.getPdfTemplate() == null){
                        notificationObj.addProperty("changed_id",(String) null);
                    }else {
                        notificationObj.addProperty("changed_id",notification.getPdfTemplate().getId());
                    }
                }
            }
            notificationArr.add(notificationObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(notificationArr.toString());
    }

    @Transactional
    @PatchMapping(path = "/private/user/notification/{notificationId}")
    public ResponseEntity<String> patchNotification(@PathVariable Integer notificationId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(notificationId == null){
            errorMessage.addProperty("mensagem","O campo notificationId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isNotification = kanbanNotificationRepository.findById(notificationId).isPresent();
        if(!isNotification){
            errorMessage.addProperty("mensagem","Notificação não foi encontrada!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        Integer user_id = tokenService.validateToken(token);
        KanbanNotification kanbanNotification = kanbanNotificationRepository.findById(notificationId).get();
        if(!Objects.equals(kanbanNotification.getUser().getId(), user_id)){
            errorMessage.addProperty("mensagem","Essa notificação não pertence ao usuário!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        kanbanNotification.setViewed(true);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Transactional
    @PatchMapping(path = "/private/user/notification/all")
    public ResponseEntity<String> patchNotificationAll(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);
        List<KanbanNotification> kanbanNotificationList = kanbanNotificationRepository.findAllByUserIdAndNoViewed(user_id);
        kanbanNotificationList.forEach(kanbanNotification -> {
            kanbanNotification.setViewed(true);
        });
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/notification/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Integer notificationId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(notificationId == null){
            errorMessage.addProperty("mensagem","O campo notificationId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isNotification = kanbanNotificationRepository.findById(notificationId).isPresent();
        if(!isNotification){
            errorMessage.addProperty("mensagem","Notificação não foi encontrada!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        Integer user_id = tokenService.validateToken(token);
        KanbanNotification kanbanNotification = kanbanNotificationRepository.findById(notificationId).get();
        if(!Objects.equals(kanbanNotification.getUser().getId(), user_id)){
            errorMessage.addProperty("mensagem","Essa notificação não pertence ao usuário!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        kanbanNotificationRepository.deleteById(notificationId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
