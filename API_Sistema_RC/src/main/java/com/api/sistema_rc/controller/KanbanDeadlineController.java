package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.service.MailService;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping(path = "/api")
public class KanbanDeadlineController {
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
    private KanbanCardChecklistRepository kanbanCardChecklistRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardChecklistItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private KanbanDeadlineRepository kanbanDeadlineRepository;
    @Autowired
    private KanbanCategoryRepository kanbanCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    private MailService mailService;
    @Autowired
    private Gson gson;
    ExecutorService executorService = Executors.newCachedThreadPool();
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/deadlines")
    public ResponseEntity<String> getCardDeadline(@PathVariable Integer cardId,
                                               @RequestHeader("Authorization") String token) {
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

        boolean isDeadline = kanbanDeadlineRepository.findByCardId(cardId).isPresent();

        JsonObject deadlineObj = new JsonObject();

        if(isDeadline){
            KanbanDeadline deadline = kanbanDeadlineRepository.findByCardId(cardId).get();

            deadlineObj.addProperty("id", deadline.getId());
            deadlineObj.addProperty("date", deadline.getDate().toString());
            deadlineObj.addProperty("overdue", deadline.isOverdue());
            deadlineObj.addProperty("category", deadline.getKanbanCategory().getName().name());
            deadlineObj.addProperty("toKanbanId",(String) null);
            deadlineObj.addProperty("toColumnId",(String) null);
            if(deadline.getActionKanbanColumn() != null){
                deadlineObj.addProperty("toKanbanId",deadline.getActionKanbanColumn().getKanban().getId());
                deadlineObj.addProperty("toColumnId",deadline.getActionKanbanColumn().getId());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(deadlineObj.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/deadline")
    public ResponseEntity<String> postCardDeadline(@RequestBody String body,@RequestHeader("Authorization") String token){
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

        boolean isDeadline = kanbanDeadlineRepository.findByCardId(cardId.getAsInt()).isPresent();
        if(isDeadline){
            errorMessage.addProperty("mensagem","Esse card já possuí um prazo!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
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

        if(kanbanUser.getUser().getPermissionLevel().charAt(14) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar prazo!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement deadlineDate = jsonObj.get("date");
        if(deadlineDate == null){
            errorMessage.addProperty("mensagem","O campo date é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        LocalDateTime formattedDate = LocalDateTime.parse(deadlineDate.getAsString(), formatter);
        if(formattedDate.isBefore(LocalDateTime.now())){
            errorMessage.addProperty("mensagem","A data precisa ser futura!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanDeadline kanbanDeadline = new KanbanDeadline();

        JsonElement category = jsonObj.get("category");
        if(category != null){
            if(!Objects.equals(category.getAsString(), CategoryName.CARD_MOVE.name())){
                errorMessage.addProperty("mensagem","Só são aceitas as categorias CARD_MOVE!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            boolean isCategory = kanbanCategoryRepository.findByName(category.getAsString()).isPresent();
            if(!isCategory){
                errorMessage.addProperty("mensagem","Categoria não foi encontrada!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            KanbanCategory kanbanCategoryDeadline = kanbanCategoryRepository.findByName(category.getAsString()).get();
            kanbanDeadline.setKanbanCategory(kanbanCategoryDeadline);

            if(Objects.equals(category.getAsString(), CategoryName.CARD_MOVE.name())){
                JsonElement toColumnId = jsonObj.get("toColumnId");
                if(toColumnId == null){
                    errorMessage.addProperty("mensagem","O campo toColumnId é necessário!");
                    errorMessage.addProperty("status",470);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                }
                boolean isColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
                if(!isColumn){
                    errorMessage.addProperty("mensagem","Coluna não foi encontrado!");
                    errorMessage.addProperty("status",474);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
                }

                KanbanColumn kanbanColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();
                kanbanDeadline.setActionKanbanColumn(kanbanColumn);
            }
        }

        kanbanDeadline.setOverdue(false);
        kanbanDeadline.setKanbanCard(kanbanCard);
        kanbanDeadline.setUser(kanbanUser.getUser());

        kanbanDeadline.setDate(formattedDate);

        KanbanDeadline dbKanbanDeadline = kanbanDeadlineRepository.saveAndFlush(kanbanDeadline);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você criou um prazo no card "+kanbanCard.getTitle()+
                            " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                            " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Criação do prazo "+dbKanbanDeadline.getDate(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategoryNotification = new KanbanCategory();
            kanbanCategoryNotification.setId(31);
            kanbanCategoryNotification.setName(CategoryName.CARDDEADLINE_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategoryNotification);

            kanbanNotification.setKanbanDeadline(dbKanbanDeadline);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " criou um prazo no card "+kanbanCard.getTitle()+
                                    " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                                    " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Criação do prazo "+dbKanbanDeadline.getDate(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName() + " criou um prazo no card "+kanbanCard.getTitle()+
                                        " da coluna "+kanbanCard.getKanbanColumn().getTitle()+
                                        " do kanban "+kanbanCard.getKanbanColumn().getKanban().getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Criação do prazo "+dbKanbanDeadline.getDate(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanDeadline.getId().toString());
    }

    @DeleteMapping(path = "/private/user/kanban/column/card/deadline/{deadlineId}")
    public ResponseEntity<String> deleteCardDeadline(@PathVariable Integer deadlineId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isDeadline = kanbanDeadlineRepository.findById(deadlineId).isPresent();
        if(!isDeadline){
            errorMessage.addProperty("mensagem","Deadline não foi encontrado!");
            errorMessage.addProperty("status",474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanDeadline selectedDeadline = kanbanDeadlineRepository.findById(deadlineId).get();

        Kanban kanban = selectedDeadline.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(15) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar prazo)!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você deletou o prazo no card "+selectedDeadline.getKanbanCard().getTitle()+
                            " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
                            " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Deletando prazo "+selectedDeadline.getDate(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(33);
            kanbanCategory.setName(CategoryName.CARDDEADLINE_DELETE);
            kanbanNotification.setKanbanCategory(kanbanCategory);
            kanbanNotification.setKanbanDeadline(null);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " deletou o prazo no card "+selectedDeadline.getKanbanCard().getTitle()+
                                    " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Deletando prazo "+selectedDeadline.getDate(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName() + " deletou o prazo no card "+selectedDeadline.getKanbanCard().getTitle()+
                                        " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
                                        " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Deletando prazo "+selectedDeadline.getDate(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);

            kanbanDeadlineRepository.deleteById(deadlineId);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/deadline/{deadlineId}")
    public ResponseEntity<String> patchCardDeadline(@RequestBody String body,@RequestHeader("Authorization") String token,
                                                     @PathVariable Integer deadlineId){
        JsonObject errorMessage = new JsonObject();

        boolean isDeadline = kanbanDeadlineRepository.findById(deadlineId).isPresent();
        if(!isDeadline){
            errorMessage.addProperty("mensagem","Deadline não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanDeadline selectedDeadline = kanbanDeadlineRepository.findById(deadlineId).get();

        Kanban kanban = selectedDeadline.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(16) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (editar prazo)!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(selectedDeadline.isOverdue()){
            errorMessage.addProperty("mensagem","O prazo já expirou!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement deadlineDate = jsonObj.get("date");
        if(deadlineDate != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            LocalDateTime formattedDate = LocalDateTime.parse(deadlineDate.getAsString(), formatter);
            if(formattedDate.isBefore(LocalDateTime.now())){
                errorMessage.addProperty("mensagem","A data precisa ser futura!");
                errorMessage.addProperty("status",470);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            selectedDeadline.setDate(formattedDate);
            modifiedArr.add("data");
        }

        JsonElement category = jsonObj.get("category");
        if(category != null && !Objects.equals(category.getAsString(), "")){
            if(!Objects.equals(category.getAsString(), CategoryName.CARD_MOVE.name())){
                errorMessage.addProperty("mensagem","Só são aceitas as categorias CARD_MOVE!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            boolean isCategory = kanbanCategoryRepository.findByName(category.getAsString()).isPresent();
            if(!isCategory){
                errorMessage.addProperty("mensagem","Categoria não foi encontrada!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }

            KanbanCategory kanbanCategoryDeadline = kanbanCategoryRepository.findByName(category.getAsString()).get();
            selectedDeadline.setKanbanCategory(kanbanCategoryDeadline);
            modifiedArr.add("categoria");
        }

        JsonElement toColumnId = jsonObj.get("toColumnId");
        if(toColumnId != null && !Objects.equals(toColumnId.getAsString(), "")){
            if(!Objects.equals(selectedDeadline.getKanbanCategory().getName().name(), CategoryName.CARD_MOVE.name())){
                errorMessage.addProperty("mensagem","Categoria CARD_MOVE necessária!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }

            boolean isColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
            if(!isColumn){
                errorMessage.addProperty("mensagem","Coluna não foi encontrado!");
                errorMessage.addProperty("status",474);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }

            KanbanColumn kanbanColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();
            selectedDeadline.setActionKanbanColumn(kanbanColumn);
            modifiedArr.add("coluna de destino");
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            String message = " atualizou ("+String.join(",",modifiedArr)+") no prazo do card "+selectedDeadline.getKanbanCard().getTitle()+
                    " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
                    " do kanban "+kanban.getTitle()+".";

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage("Você"+message);
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Atualização do prazo "+selectedDeadline.getDate(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(32);
            kanbanCategory.setName(CategoryName.CARDDEADLINE_UPDATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanDeadline(selectedDeadline);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName()+ message);
                    mailService.sendMail(userAdmin.getEmail(),"Atualização do prazo "+selectedDeadline.getDate(),kanbanNotificationAdmin.getMessage());
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
                        kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName()+ message);
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Atualização do prazo "+selectedDeadline.getDate(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    @GetMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}/deadlines")
//    public ResponseEntity<String> getChecklistDeadlines(@PathVariable Integer checklistId,
//                                                        @RequestHeader("Authorization") String token) {
//        JsonObject errorMessage = new JsonObject();
//
//        if (checklistId == null) {
//            errorMessage.addProperty("mensagem", "O campo checklistId é necessário!");
//            errorMessage.addProperty("status", 470);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }
//        boolean isChecklist = kanbanCardChecklistRepository.findById(checklistId).isPresent();
//        if (!isChecklist) {
//            errorMessage.addProperty("mensagem", "Checklist não foi encontrado!");
//            errorMessage.addProperty("status", 474);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
//        }
//
//        KanbanCardChecklist kanbanCardChecklist = kanbanCardChecklistRepository.findById(checklistId).get();
//
//        Kanban kanban = kanbanCardChecklist.getKanbanCard().getKanbanColumn().getKanban();
//        Integer user_id = tokenService.validateToken(token);
//
//        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);
//
//        if (kanbanUser == null) {
//            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
//            errorMessage.addProperty("status", 471);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
//        }
//
//        List<KanbanDeadline> kanbanDeadlines = kanbanDeadlineRepository.findAllByChecklistId(checklistId);
//
//        JsonArray deadlineArr = new JsonArray();
//
//        kanbanDeadlines.forEach(deadline -> {
//            JsonObject deadlineObj = new JsonObject();
//            deadlineObj.addProperty("id", deadline.getId());
//            deadlineObj.addProperty("date", deadline.getDate().toString());
//            deadlineObj.addProperty("overdue", deadline.isOverdue());
////            deadlineObj.addProperty("category", deadline.getKanbanCategory().getName().name());
////            if(deadline.getActionKanbanCardChecklist() != null){
////                JsonObject cardObj = new JsonObject();
////                cardObj.addProperty("id",deadline.getActionKanbanCardChecklist().getId());
////                cardObj.addProperty("name",deadline.getActionKanbanCardChecklist().getName());
////                deadlineObj.add("actionChecklist",cardObj);
////            }
//            deadlineArr.add(deadlineObj);
//        });
//
//        return ResponseEntity.status(HttpStatus.OK).body(deadlineArr.toString());
//    }

//    @PostMapping(path = "/private/user/kanban/column/card/checklist/deadline")
//    public ResponseEntity<String> postChecklistDeadline(@RequestBody String body,@RequestHeader("Authorization") String token){
//        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);
//
//        JsonObject errorMessage = new JsonObject();
//
//        JsonElement checklistId = jsonObj.get("checklistId");
//        if(checklistId == null){
//            errorMessage.addProperty("mensagem","O campo checklistId é necessário!");
//            errorMessage.addProperty("status",470);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }
//        boolean isChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).isPresent();
//        if(!isChecklist){
//            errorMessage.addProperty("mensagem","Checklist não foi encontrado!");
//            errorMessage.addProperty("status",474);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
//        }
//
//        boolean isDeadline = kanbanDeadlineRepository.findByCardId(checklistId.getAsInt()).isPresent();
//        if(isDeadline){
//            errorMessage.addProperty("mensagem","Essa checklist já possuí um prazo!");
//            errorMessage.addProperty("status",470);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }
//
//        KanbanCardChecklist kanbanCardChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).get();
//
//        Kanban kanban = kanbanCardChecklist.getKanbanCard().getKanbanColumn().getKanban();
//        Integer user_id = tokenService.validateToken(token);
//
//        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);
//
//        if(kanbanUser == null){
//            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
//            errorMessage.addProperty("status",471);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
//        }
//
//        if(kanbanUser.getUser().getPermissionLevel().charAt(14) == '0'){
//            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar prazo!");
//            errorMessage.addProperty("status",475);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
//        }
//
//        JsonElement deadlineDate = jsonObj.get("date");
//        if(deadlineDate == null){
//            errorMessage.addProperty("mensagem","O campo date é necessário!");
//            errorMessage.addProperty("status",470);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//        LocalDateTime formattedDate = LocalDateTime.parse(deadlineDate.getAsString(), formatter);
//        if(formattedDate.isBefore(LocalDateTime.now())){
//            errorMessage.addProperty("mensagem","A data precisa ser futura!");
//            errorMessage.addProperty("status",470);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }
//
//        KanbanDeadline kanbanDeadline = new KanbanDeadline();
//
////        JsonElement categoryId = jsonObj.get("categoryId");
////        if(categoryId != null){
////            if(categoryId.getAsInt() != 26){
////                errorMessage.addProperty("mensagem","Só são aceitas as categorias CHECKLISTITEM_UPDATE!");
////                errorMessage.addProperty("status",474);
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
////            }
////            boolean isCategory = kanbanCategoryRepository.findById(categoryId.getAsInt()).isPresent();
////            if(!isCategory){
////                errorMessage.addProperty("mensagem","Categoria não foi encontrada!");
////                errorMessage.addProperty("status",474);
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
////            }
////            KanbanCategory kanbanCategoryDeadline = kanbanCategoryRepository.findById(categoryId.getAsInt()).get();
////            kanbanDeadline.setKanbanCategory(kanbanCategoryDeadline);
////
////            if(categoryId.getAsInt() == 26){
////                //UPDATE CHECKLISTITEM
////            }
////        }
//
//        kanbanDeadline.setOverdue(false);
//        kanbanDeadline.setKanbanCardChecklist(kanbanCardChecklist);
//        kanbanDeadline.setUser(kanbanUser.getUser());
//
//        kanbanDeadline.setDate(formattedDate);
//
//        KanbanDeadline dbKanbanDeadline = kanbanDeadlineRepository.saveAndFlush(kanbanDeadline);
//
//        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();
//
//        KanbanNotification kanbanNotification = new KanbanNotification();
//
//        kanbanNotification.setUser(kanbanUser.getUser());
//        kanbanNotification.setSenderUser(kanbanUser.getUser());
//
//        kanbanNotification.setRegistration_date(LocalDateTime.now());
//        kanbanNotification.setMessage(
//                "Você criou um prazo na checklist " + kanbanCardChecklist.getName() +
//                        " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
//                        " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
//                        " do kanban "+kanban.getTitle()+"."
//        );
//        kanbanNotification.setViewed(false);
//
//        KanbanCategory kanbanCategoryNotification = new KanbanCategory();
//        kanbanCategoryNotification.setId(34);
//        kanbanCategoryNotification.setName(CategoryName.CARDCHECKLISTDEADLINE_CREATE);
//        kanbanNotification.setKanbanCategory(kanbanCategoryNotification);
//
//        kanbanNotification.setKanbanDeadline(dbKanbanDeadline);
//
//        kanbanNotificationList.add(kanbanNotification);
//
//        List<User> userList = userRepository.findAllByAdmin();
//        userList.forEach(userAdmin->{
//            if(!Objects.equals(userAdmin.getId(), user_id)){
//                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
//                kanbanNotificationAdmin.setUser(userAdmin);
//                kanbanNotificationAdmin.setMessage(
//                        kanbanUser.getUser().getName() + " criou um prazo na checklist" + kanbanCardChecklist.getName() +
//                                " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
//                                " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
//                                " do kanban "+kanban.getTitle()+"."
//                );
//                kanbanNotificationList.add(kanbanNotificationAdmin);
//            }
//        });
//
//        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
//        kanbanUserList.forEach(userInKanban->{
//            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
//                String role = userInKanban.getUser().getRole().getName().name();
//                if (role.equals("ROLE_SUPERVISOR")) {
//                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
//                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
//                    kanbanNotificationSupervisor.setMessage(
//                            kanbanUser.getUser().getName() + " criou um prazo na checklist" + kanbanCardChecklist.getName() +
//                                    " do card "+kanbanCardChecklist.getKanbanCard().getTitle()+
//                                    " da coluna "+kanbanCardChecklist.getKanbanCard().getKanbanColumn().getTitle()+
//                                    " do kanban "+kanban.getTitle()+"."
//                    );
//                    kanbanNotificationList.add(kanbanNotificationSupervisor);
//                }
//            }
//        });
//
//        kanbanNotificationRepository.saveAll(kanbanNotificationList);
//
//        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanDeadline.getId().toString());
//    }

//    @DeleteMapping(path = "/private/user/kanban/column/card/checklist/deadline/{deadlineId}")
//    public ResponseEntity<String> deleteChecklistDeadline(@PathVariable Integer deadlineId,@RequestHeader("Authorization") String token){
//        JsonObject errorMessage = new JsonObject();
//        boolean isDeadline = kanbanDeadlineRepository.findById(deadlineId).isPresent();
//        if(!isDeadline){
//            errorMessage.addProperty("mensagem","Deadline não foi encontrado!");
//            errorMessage.addProperty("status",474);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
//        }
//
//        KanbanDeadline selectedDeadline = kanbanDeadlineRepository.findById(deadlineId).get();
//
//        Kanban kanban = selectedDeadline.getKanbanCard().getKanbanColumn().getKanban();
//        Integer user_id = tokenService.validateToken(token);
//
//        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);
//
//        if(kanbanUser == null){
//            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
//            errorMessage.addProperty("status",471);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
//        }
//
//        if(kanbanUser.getUser().getPermissionLevel().charAt(15) == '0'){
//            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar prazo)!");
//            errorMessage.addProperty("status",475);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
//        }
//
//        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();
//
//        KanbanNotification kanbanNotification = new KanbanNotification();
//
//        kanbanNotification.setUser(kanbanUser.getUser());
//        kanbanNotification.setSenderUser(kanbanUser.getUser());
//
//        kanbanNotification.setRegistration_date(LocalDateTime.now());
//        kanbanNotification.setMessage(
//                "Você deletou o prazo na checklist "+selectedDeadline.getKanbanCardChecklist().getName()+
//                        " do card "+selectedDeadline.getKanbanCard().getTitle()+
//                        " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
//                        " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
//        );
//        kanbanNotification.setViewed(false);
//
//        KanbanCategory kanbanCategory = new KanbanCategory();
//        kanbanCategory.setId(36);
//        kanbanCategory.setName(CategoryName.CARDCHECKLISTDEADLINE_DELETE);
//        kanbanNotification.setKanbanCategory(kanbanCategory);
//
//        for (KanbanNotification dbNotificationDeadline : kanbanNotificationRepository.findAllByKanbanDeadlineId(deadlineId)) {
//            dbNotificationDeadline.setKanbanDeadline(null);
//        }
//
//        kanbanNotificationList.add(kanbanNotification);
//
//        List<User> userList = userRepository.findAllByAdmin();
//        userList.forEach(userAdmin->{
//            if(!Objects.equals(userAdmin.getId(), user_id)){
//                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
//                kanbanNotificationAdmin.setUser(userAdmin);
//                kanbanNotificationAdmin.setMessage(
//                        kanbanUser.getUser().getName() + " deletou o prazo na checklist "+selectedDeadline.getKanbanCardChecklist().getName()+
//                                " do card "+selectedDeadline.getKanbanCard().getTitle()+
//                                " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
//                                " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
//                );
//                kanbanNotificationList.add(kanbanNotificationAdmin);
//            }
//        });
//
//        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
//        kanbanUserList.forEach(userInKanban->{
//            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
//                String role = userInKanban.getUser().getRole().getName().name();
//                if (role.equals("ROLE_SUPERVISOR")) {
//                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
//                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
//                    kanbanNotificationSupervisor.setMessage(
//                            kanbanUser.getUser().getName() + " deletou o prazo na checklist "+selectedDeadline.getKanbanCardChecklist().getName()+
//                                    " do card "+selectedDeadline.getKanbanCard().getTitle()+
//                                    " da coluna "+selectedDeadline.getKanbanCard().getKanbanColumn().getTitle()+
//                                    " do kanban "+selectedDeadline.getKanbanCard().getKanbanColumn().getKanban().getTitle()+"."
//                    );
//                    kanbanNotificationList.add(kanbanNotificationSupervisor);
//                }
//            }
//        });
//
//        kanbanNotificationRepository.saveAll(kanbanNotificationList);
//
//        kanbanCardTagRepository.deleteById(deadlineId);
//
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

}
