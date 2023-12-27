package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class KanbanChecklistItemController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardChecklistRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardChecklistItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}/checklistItems")
    public ResponseEntity<String> getChecklistItem(@PathVariable Integer checklistId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (checklistId == null) {
            errorMessage.addProperty("mensagem", "O campo checklistId é necessário!");
            errorMessage.addProperty("status", 450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if (!isCheckList) {
            errorMessage.addProperty("mensagem", "CheckList não foi encontrado!");
            errorMessage.addProperty("status", 454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardCheckList = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = kanbanCardCheckList.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardChecklistItemRepository.findAllByChecklistId(checklistId);

        JsonArray checklistItemArr = new JsonArray();

        kanbanCardChecklistItems.forEach(checkListItem -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checkListItem.getId());
            cardObj.addProperty("name", checkListItem.getName());
            cardObj.addProperty("completed", checkListItem.isCompleted());
            checklistItemArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checklistItemArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checklist/checklistItem")
    public ResponseEntity<String> postChecklistItem(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement checklistId = jsonObj.get("checklistId");
        if(checklistId == null){
            errorMessage.addProperty("mensagem","O campo checklistId é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).isPresent();
        if(!isChecklist){
            errorMessage.addProperty("mensagem","Checklist não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).get();

        Kanban kanban = kanbanCardChecklist.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(32) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardChecklistItem kanbanCardChecklistItem = new KanbanCardChecklistItem();

        kanbanCardChecklistItem.setName(checklistName.getAsString());
        kanbanCardChecklistItem.setCompleted(false);
        kanbanCardChecklistItem.setKanbanChecklist(kanbanCardChecklist);

        KanbanCardChecklistItem dbKanbanCardChecklistItem = kanbanCardChecklistItemRepository.saveAndFlush(kanbanCardChecklistItem);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o item " +
                        dbKanbanCardChecklistItem.getName() + " na checklist "+ kanbanCardChecklist.getName() +
                        " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
                        " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                        " do kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(25);
        kanbanCategory.setName(CategoryName.CARDCHECKLISTITEM_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardChecklistItem(dbKanbanCardChecklistItem);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou o item " +
                                dbKanbanCardChecklistItem.getName() + " na checklist "+ kanbanCardChecklist.getName() +
                                " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
                                " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                                " do kanban "+kanban.getTitle()+"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " criou o item " +
                                    dbKanbanCardChecklistItem.getName() + " na checklist "+ kanbanCardChecklist.getName() +
                                    " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
                                    " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardChecklistItem.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checklist/checklistItem/{checklistItemId}")
    public ResponseEntity<String> patchChecklistItem(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checklistItemId){
        JsonObject errorMessage = new JsonObject();

        boolean isChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).isPresent();
        if(!isChecklistItem){
            errorMessage.addProperty("mensagem","ChecklistItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardChecklistItem selectedChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).get();

        Kanban kanban = selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(34) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement checklistName = jsonObj.get("name");
        String oldChecklistName = selectedChecklistItem.getName();
        if(checklistName != null){
            selectedChecklistItem.setName(checklistName.getAsString());
            modifiedArr.add("nome");
        }

        JsonElement checklistCompleted = jsonObj.get("completed");
        if(checklistCompleted != null){
            selectedChecklistItem.setCompleted(checklistCompleted.getAsBoolean());
            modifiedArr.add("completado");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou ("+String.join(",",modifiedArr)+") no item " +
                selectedChecklistItem.getName() + " da checklist "+ selectedChecklistItem.getKanbanChecklist().getName() +
                " do card "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getTitle()+
                " da coluna "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getTitle()+
                " do kanban "+kanban.getTitle()+".";

        if(oldChecklistName != null){
            message = " atualizou ("+String.join(",",modifiedArr)+") no item " +
                    oldChecklistName + " (nome antigo) | "+
                    selectedChecklistItem.getName() + " (novo nome) da checklist "+ selectedChecklistItem.getKanbanChecklist().getName() +
                    " do card "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getTitle()+
                    " da coluna "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getTitle()+
                    " do kanban "+kanban.getTitle()+".";
        }

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(26);
        kanbanCategory.setName(CategoryName.CARDCHECKLISTITEM_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardChecklistItem(selectedChecklistItem);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName()+ finalMessage);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName()+ finalMessage);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checklist/checklistItem/{checklistItemId}")
    public ResponseEntity<String> deleteChecklistItem(@PathVariable Integer checklistItemId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).isPresent();
        if(!isChecklistItem){
            errorMessage.addProperty("mensagem","ChecklistItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklistItem selectedChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).get();

        Kanban kanban = selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(33) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou o item " +
                        selectedChecklistItem.getName() + " na checklist "+ selectedChecklistItem.getKanbanChecklist().getName() +
                        " do card "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getTitle()+
                        " da coluna "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getTitle()+
                        " do kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(27);
        kanbanCategory.setName(CategoryName.CARDCHECKLISTITEM_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        for (KanbanNotification dbNotificationChecklistItem : kanbanNotificationRepository.findAllByCardChecklistItemId(checklistItemId)) {
            dbNotificationChecklistItem.setKanbanCardChecklistItem(null);
        }

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() +  " deletou o item " +
                                selectedChecklistItem.getName() + " na checklist "+ selectedChecklistItem.getKanbanChecklist().getName() +
                                " do card "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getTitle()+
                                " da coluna "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getTitle()+
                                " do kanban "+kanban.getTitle()+"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() +  " deletou o item " +
                                    selectedChecklistItem.getName() + " na checklist "+ selectedChecklistItem.getKanbanChecklist().getName() +
                                    " do card "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getTitle()+
                                    " da coluna "+selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanCardChecklistItemRepository.deleteById(checklistItemId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
