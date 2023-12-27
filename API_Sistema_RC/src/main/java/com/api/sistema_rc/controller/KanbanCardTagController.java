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
public class KanbanCardTagController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardTagRepository kanbanCardTagRepository;
    @Autowired
    private KanbanCardCommentRepository kanbanCardCommentRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardCheckListRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private UserRepository userRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/tags")
    public ResponseEntity<String> getTags(@PathVariable Integer cardId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (cardId == null) {
            errorMessage.addProperty("mensagem", "O campo cardId é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if (!isCard) {
            errorMessage.addProperty("mensagem", "Card não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardTag> kanbanCardTag = kanbanCardTagRepository.findAllByCardId(cardId);

        JsonArray tagArr = new JsonArray();

        kanbanCardTag.forEach(tag -> {
            JsonObject tagObj = new JsonObject();
            tagObj.addProperty("id", tag.getId());
            tagObj.addProperty("name", tag.getName());
            tagObj.addProperty("color", tag.getColor());
            tagArr.add(tagObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(tagArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/tag")
    public ResponseEntity<String> postTag(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(26) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar tag!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement tagName = jsonObj.get("name");
        if(tagName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement tagColor = jsonObj.get("color");
        if(tagColor == null){
            errorMessage.addProperty("mensagem","O campo color é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardTag kanbanCardTag = new KanbanCardTag();

        kanbanCardTag.setName(tagName.getAsString());
        kanbanCardTag.setColor(tagColor.getAsString());
        kanbanCardTag.setKanbanCard(kanbanCard);

        KanbanCardTag dbKanbanCardTag = kanbanCardTagRepository.saveAndFlush(kanbanCardTag);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou a tag " + dbKanbanCardTag.getName() + " no card "+kanbanCard.getTitle()+
                        " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                        " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(15);
        kanbanCategory.setName(CategoryName.CARDTAG_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardTag(dbKanbanCardTag);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou a tag  " +
                                dbKanbanCardTag.getName() + " no card "+kanbanCard.getTitle()+
                                " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                                " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
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
                            kanbanUser.getUser().getName() + " criou a tag  " +
                                    dbKanbanCardTag.getName() + " no card "+kanbanCard.getTitle()+
                                    " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                                    " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardTag.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/tag/{tagId}")
    public ResponseEntity<String> patchTag(@RequestBody String body,@RequestHeader("Authorization") String token,
                                                 @PathVariable Integer tagId){
        JsonObject errorMessage = new JsonObject();

        boolean isCheckList = kanbanCardTagRepository.findById(tagId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","Tag não foi encontrado!");
            errorMessage.addProperty("status",474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardTag selectedTag = kanbanCardTagRepository.findById(tagId).get();

        Kanban kanban = selectedTag.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(27) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement tagName = jsonObj.get("name");
        String oldTagName = selectedTag.getName();
        if(tagName != null){
            selectedTag.setName(tagName.getAsString());
            modifiedArr.add("nome");
        }

        JsonElement tagColor = jsonObj.get("color");
        if(tagColor != null){
            selectedTag.setName(tagColor.getAsString());
            modifiedArr.add("cor");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou (" +String.join(",",modifiedArr)+ ") na tag " +
                selectedTag.getName() + " do card "+selectedTag.getKanbanCard().getTitle()+
                " da coluna "+selectedTag.getKanbanCard().getKanbanColumn().getTitle()+
                " do kanban "+selectedTag.getKanbanCard().getKanbanColumn().getKanban().getTitle()+".";

        if(tagName != null){
            message = " atualizou (" +String.join(",",modifiedArr)+ ") na tag " +
                    oldTagName + " (nome antigo) | "+selectedTag.getName()+" (novo nome) do card "+
                    selectedTag.getKanbanCard().getTitle()+ " da coluna "+
                    selectedTag.getKanbanCard().getKanbanColumn().getTitle()+ " do kanban "+
                    selectedTag.getKanbanCard().getKanbanColumn().getKanban().getTitle()+".";
        }

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(16);
        kanbanCategory.setName(CategoryName.CARDTAG_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardTag(selectedTag);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName()+ finalMessage);
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
                    kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName()+ finalMessage);
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/tag/{tagId}")
    public ResponseEntity<String> deleteTag(@PathVariable Integer tagId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isTag = kanbanCardTagRepository.findById(tagId).isPresent();
        if(!isTag){
            errorMessage.addProperty("mensagem","Tag não foi encontrado!");
            errorMessage.addProperty("status",474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardTag selectedTag = kanbanCardTagRepository.findById(tagId).get();

        Kanban kanban = selectedTag.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(28) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou a tag " +
                        selectedTag.getName() + " no card "+selectedTag.getKanbanCard().getTitle()+
                        " da coluna "+selectedTag.getKanbanCard().getKanbanColumn().getTitle()+
                        " do kanban "+selectedTag.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(17);
        kanbanCategory.setName(CategoryName.CARDTAG_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        for (KanbanNotification dbNotificationTag : kanbanNotificationRepository.findAllByCardTagId(tagId)) {
            dbNotificationTag.setKanbanCardTag(null);
        }

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " deletou a tag " +
                                selectedTag.getName() + " no card "+selectedTag.getKanbanCard().getTitle()+
                                " da coluna "+selectedTag.getKanbanCard().getKanbanColumn().getTitle()+
                                " do kanban "+selectedTag.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
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
                            kanbanUser.getUser().getName() + " deletou a tag " +
                                    selectedTag.getName() + " no card "+selectedTag.getKanbanCard().getTitle()+
                                    " da coluna "+selectedTag.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+selectedTag.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanCardTagRepository.deleteById(tagId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
