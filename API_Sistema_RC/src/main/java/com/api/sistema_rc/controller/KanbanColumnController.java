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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping(path = "/api")
public class KanbanColumnController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardCommentRepository kanbanCardCommentRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private KanbanCardTagRepository kanbanCardTagRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardCheckListRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardCheckListItemRepository;
    @Autowired
    private KanbanDeadlineRepository kanbanDeadlineRepository;
    @Autowired
    private KanbanCardCustomFieldRepository kanbanCardCustomFieldRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private Gson gson;
    ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping(path = "/private/user/kanban/{kanbanId}/columns")
    public ResponseEntity<String> getColumns(@PathVariable Integer kanbanId,
                                             @RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isKanban = kanbanRepository.findById(kanbanId).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanColumn> kanbanColumns = kanbanColumnRepository.findAllByKanbanId(kanbanId);

        JsonArray columnsArr = new JsonArray();

        kanbanColumns.forEach(column->{
            JsonObject columnObj = new JsonObject();
            columnObj.addProperty("id",column.getId());
            columnObj.addProperty("title",column.getTitle());
            columnObj.addProperty("index",column.getIndex());
            columnsArr.add(columnObj);
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);

        JsonArray userArray = new JsonArray();

        kanbanUserList.forEach(userInKanban->{
            JsonObject formattedUser = new JsonObject();
            formattedUser.addProperty("id",userInKanban.getUser().getId());
            formattedUser.addProperty("name",userInKanban.getUser().getName());
            formattedUser.addProperty("email",userInKanban.getUser().getEmail());
            formattedUser.addProperty("pushEmail",userInKanban.getUser().getPushEmail());
            formattedUser.addProperty("registration_date",userInKanban.getUser().getRegistration_date().toString());
            formattedUser.addProperty("nationality",userInKanban.getUser().getNationality());
            formattedUser.addProperty("gender",userInKanban.getUser().getGender());
            formattedUser.addProperty("role",userInKanban.getUser().getRole().getName().name());
            formattedUser.addProperty("permissionLevel",userInKanban.getUser().getPermissionLevel());
            if(userInKanban.getUser().getProfilePicture() == null){
                formattedUser.addProperty("profilePicture",(String) null);
            }else{
                try {
                    byte[] bytes = userInKanban.getUser().getProfilePicture().getBytes(1,(int) userInKanban.getUser().getProfilePicture().length());
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    formattedUser.addProperty("profilePicture",encoded);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            userArray.add(formattedUser);
        });

        JsonObject selectedKanban = new JsonObject();
        selectedKanban.add("columns",columnsArr);
        selectedKanban.add("members",userArray);

        return ResponseEntity.status(HttpStatus.OK).body(selectedKanban.toString());
    }

    @PostMapping(path = "/private/user/kanban/column")
    public ResponseEntity<String> postColumn(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement kanbanId = kanbanJson.get("kanbanId");
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",420);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isKanban = kanbanRepository.findById(kanbanId.getAsInt()).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",424);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement columnTitle = kanbanJson.get("title");
        if(columnTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",420);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId.getAsInt()).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",421);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(4) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",425);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanColumn> kanbanColumnList = kanbanColumnRepository.findAllByKanbanId(kanbanId.getAsInt());

        KanbanColumn kanbanColumn = new KanbanColumn();
        kanbanColumn.setTitle(columnTitle.getAsString());
        kanbanColumn.setKanban(kanban);
        kanbanColumn.setIndex(kanbanColumnList.size());

        KanbanColumn dbKanbanColumn = kanbanColumnRepository.saveAndFlush(kanbanColumn);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você criou a coluna "+dbKanbanColumn.getTitle()+" no kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Criação da coluna "+dbKanbanColumn.getTitle(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(6);
            kanbanCategory.setName(CategoryName.COLUMN_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanColumn(dbKanbanColumn);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName()+" criou a coluna "+dbKanbanColumn.getTitle()+" no kanban "+kanban.getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Criação da coluna "+dbKanbanColumn.getTitle(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId.getAsInt());
            kanbanUserList.forEach(userInKanban->{
                if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                    String role = userInKanban.getUser().getRole().getName().name();
                    if (role.equals("ROLE_SUPERVISOR")) {
                        KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                        kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                        kanbanNotificationSupervisor.setMessage(
                                kanbanUser.getUser().getName()+" criou a coluna "+dbKanbanColumn.getTitle()+" no kanban "+kanban.getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Criação da coluna "+dbKanbanColumn.getTitle(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanColumn.getId().toString());
    }

    @DeleteMapping(path = "/private/user/kanban/column/{columnId}")
    public  ResponseEntity<String> deleteColumn(@PathVariable Integer columnId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isColumn = kanbanColumnRepository.findById(columnId).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",424);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn selectedColumn = kanbanColumnRepository.findById(columnId).get();

        Kanban kanban = selectedColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",421);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(6) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",425);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanColumn> columnList = kanbanColumnRepository.findAllByKanbanId(kanban.getId());
        boolean isSearch = false;
        for(int i = 0;i < columnList.size();i++){
            if(Objects.equals(columnList.get(i).getId(), columnId)){
                isSearch = true;
            }else{
                if(isSearch){
                    columnList.get(i).setIndex(i - 1);
                }else{
                    columnList.get(i).setIndex(i);
                }
            }
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Deletando coluna "+selectedColumn.getTitle(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(8);
            kanbanCategory.setName(CategoryName.COLUMN_DELETE);
            kanbanNotification.setKanbanCategory(kanbanCategory);
            kanbanNotification.setKanbanColumn(null);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName()+" deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Deletando coluna "+selectedColumn.getTitle(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName()+" deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Deletando coluna "+selectedColumn.getTitle(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);

            kanbanColumnRepository.deleteById(columnId);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/{columnId}")
    public ResponseEntity<String> patchColumn(@RequestBody String body,@RequestHeader("Authorization") String token,
                                              @PathVariable Integer columnId){
        JsonObject errorMessage = new JsonObject();

        boolean isColumn = kanbanColumnRepository.findById(columnId).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",424);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanColumn selectedColumn = kanbanColumnRepository.findById(columnId).get();

        Kanban kanban = selectedColumn.getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(7) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement columnTitle = jsonObj.get("title");
        String oldColumnTitle = selectedColumn.getTitle();
        if(columnTitle != null){
            selectedColumn.setTitle(columnTitle.getAsString());
            modifiedArr.add("título");
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            String message = " atualizou ("+String.join(",",modifiedArr)+") na coluna " +
                    kanban.getTitle() + " do kanban " + kanban.getTitle() + ".";

            if(columnTitle != null){
                message = " atualizou ("+String.join(",",modifiedArr)+") na coluna " +
                        oldColumnTitle + " (título antigo) | "+ selectedColumn.getTitle() +
                        " (novo título) do kanban " + kanban.getTitle() + ".";
            }

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage("Você"+message);
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Atualização da coluna "+selectedColumn.getTitle(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(7);
            kanbanCategory.setName(CategoryName.COLUMN_UPDATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanColumn(selectedColumn);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            String finalMessage = message;
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName() + finalMessage);
                    mailService.sendMail(userAdmin.getEmail(),"Atualização da coluna "+selectedColumn.getTitle(),kanbanNotificationAdmin.getMessage());
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
                        mailService.sendMail(kanbanUser.getUser().getEmail(),"Atualização da coluna "+selectedColumn.getTitle(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/move")
    public ResponseEntity<String> moveColumn(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement columnId = jsonObj.get("columnId");
        if(columnId == null){
            errorMessage.addProperty("mensagem","O campo columnId é necessário!");
            errorMessage.addProperty("status",420);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isColumn = kanbanColumnRepository.findById(columnId.getAsInt()).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",424);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn selectedColumn = kanbanColumnRepository.findById(columnId.getAsInt()).get();

        Kanban kanban = selectedColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",421);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(5) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",425);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement toIndex = jsonObj.get("toIndex");
        if(toIndex == null){
            errorMessage.addProperty("mensagem","O campo toIndex é necessário!");
            errorMessage.addProperty("status",420);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanColumn> toColumnList = kanbanColumnRepository.findAllByKanbanId(kanban.getId());

        if(toIndex.getAsInt() >= toColumnList.size()){
            selectedColumn.setIndex(toColumnList.size() - 1);
        }else{
            selectedColumn.setIndex(toIndex.getAsInt());
        }

        toColumnList.sort(Comparator.comparing(KanbanColumn::getIndex));
        for (int i = 0; i < toColumnList.size() - 1; i++) {
            if (Objects.equals(toColumnList.get(i).getId(), selectedColumn.getId()) && Objects.equals(toColumnList.get(i + 1).getIndex(), selectedColumn.getIndex())) {
                toColumnList.get(i).setIndex(i + 1);
                toColumnList.get(i + 1).setIndex(i);
                i += 1;  // ajuste aqui
            } else if (Objects.equals(toColumnList.get(i).getId(), selectedColumn.getId()) && i > 0 && toColumnList.get(i - 1).getIndex() == toIndex.getAsInt()) {
                toColumnList.get(i).setIndex(i - 1);
                toColumnList.get(i - 1).setIndex(i);
            } else {
                toColumnList.get(i).setIndex(i);
            }
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você moveu a coluna " +
                            selectedColumn.getTitle() + " do kanban " + kanban.getTitle() + "."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Movendo coluna "+selectedColumn.getTitle(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(9);
            kanbanCategory.setName(CategoryName.COLUMN_MOVE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanColumn(selectedColumn);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " moveu a coluna " +
                                    selectedColumn.getTitle() + " do kanban " + kanban.getTitle() + "."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Movendo coluna "+selectedColumn.getTitle(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName() + " moveu a coluna " +
                                        selectedColumn.getTitle() + " do kanban " + kanban.getTitle() + "."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Movendo coluna "+selectedColumn.getTitle(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
