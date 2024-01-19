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
public class KanbanChecklistController {
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
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/checklists")
    public ResponseEntity<String> getChecklists(@PathVariable Integer cardId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (cardId == null) {
            errorMessage.addProperty("mensagem", "O campo cardId é necessário!");
            errorMessage.addProperty("status", 440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if (!isCard) {
            errorMessage.addProperty("mensagem", "Card não foi encontrado!");
            errorMessage.addProperty("status", 444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardChecklist> kanbanCardChecklist = kanbanCardChecklistRepository.findAllByCardId(cardId);

        JsonArray checklistArr = new JsonArray();

        kanbanCardChecklist.forEach(checklist -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checklist.getId());
            cardObj.addProperty("name", checklist.getName());
            checklistArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checklistArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checklist")
    public ResponseEntity<String> postChecklist(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(11) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardChecklist = new KanbanCardChecklist();

        kanbanCardChecklist.setName(checklistName.getAsString());
        kanbanCardChecklist.setKanbanCard(kanbanCard);

        KanbanCardChecklist dbKanbanCardChecklist = kanbanCardChecklistRepository.saveAndFlush(kanbanCardChecklist);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o checklist " + dbKanbanCardChecklist.getName() +
                        " no card "+kanbanCard.getTitle()+
                        " da coluna "+kanbanCard.getKanbanColumn()+
                        " do kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(22);
        kanbanCategory.setName(CategoryName.CARDCHECKLIST_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardChecklist(dbKanbanCardChecklist);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou o checklist " + dbKanbanCardChecklist.getName() +
                                " no card "+kanbanCard.getTitle()+
                                " da coluna "+kanbanCard.getKanbanColumn()+
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
                            kanbanUser.getUser().getName() + " criou o checklist " + dbKanbanCardChecklist.getName() +
                                    " no card "+kanbanCard.getTitle()+
                                    " da coluna "+kanbanCard.getKanbanColumn()+
                                    " do kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardChecklist.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}")
    public ResponseEntity<String> patchCheckList(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checklistId){
        JsonObject errorMessage = new JsonObject();

        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","CheckList não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardChecklist selectedCheckList = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = selectedCheckList.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(13) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement checklistName = jsonObj.get("name");
        String oldChecklistName = selectedCheckList.getName();
        if(checklistName != null){
            selectedCheckList.setName(checklistName.getAsString());
            modifiedArr.add("nome");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou ("+String.join(",",modifiedArr)+") no checklist " +
                selectedCheckList.getName() + " do card "+selectedCheckList.getKanbanCard().getTitle()+
                " da coluna "+selectedCheckList.getKanbanCard().getKanbanColumn().getTitle()+
                " do kanban "+kanban.getTitle()+".";

        if(checklistName != null){
            message = " atualizou ("+String.join(",",modifiedArr)+") no checklist " +
                    oldChecklistName+" (nome antigo) | "+
                    selectedCheckList.getName() + " (novo nome) do card "+selectedCheckList.getKanbanCard().getTitle()+
                    " da coluna "+selectedCheckList.getKanbanCard().getKanbanColumn().getTitle()+
                    " do kanban "+kanban.getTitle()+".";
        }

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(23);
        kanbanCategory.setName(CategoryName.CARDCHECKLIST_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardChecklist(selectedCheckList);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName() + finalMessage);
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
                    kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName() + finalMessage);
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}")
    public ResponseEntity<String> deleteCheckList(@PathVariable Integer checklistId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","Checklist não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist selectedChecklist = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = selectedChecklist.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(12) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou o checklist " + selectedChecklist.getName() +
                        " no card "+selectedChecklist.getKanbanCard().getTitle()+
                        " da coluna "+selectedChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                        " do kanban "+selectedChecklist.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(24);
        kanbanCategory.setName(CategoryName.CARDCHECKLIST_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);
        kanbanNotification.setKanbanCardChecklist(null);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " deletou o checklist " +
                                selectedChecklist.getName() + " no card "+selectedChecklist.getKanbanCard().getTitle()+
                                " da coluna "+selectedChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                                " do kanban "+selectedChecklist.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
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
                            kanbanUser.getUser().getName() + " deletou o checklist " +
                                    selectedChecklist.getName() + " no card "+selectedChecklist.getKanbanCard().getTitle()+
                                    " da coluna "+selectedChecklist.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+selectedChecklist.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanCardChecklistRepository.deleteById(checklistId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
