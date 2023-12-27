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

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class KanbanController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardCheckListRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    private final Gson gson = new Gson();

    @GetMapping(path = "/private/user/kanban")
    public ResponseEntity<List<Kanban>> getKanban(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);
        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByUserId(user_id);

        List<Kanban> kanbanList = kanbanUserList.stream()
                .map(KanbanUser::getKanban)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(kanbanList);
    }
    @GetMapping(path = "/private/user/kanban/{kanbanId}")
    public ResponseEntity<String> getUsersInKanban(@PathVariable Integer kanbanId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isKanban = kanbanRepository.findById(kanbanId).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

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
            formattedUser.addProperty("permission_level",userInKanban.getUser().getPermissionLevel());
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

        return ResponseEntity.status(HttpStatus.OK).body(userArray.toString());
    }
    @PostMapping(path = "/private/user/kanban")
    public ResponseEntity<String> postKanban(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement kanbanTitle = kanbanJson.get("title");
        if(kanbanTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Integer user_id = tokenService.validateToken(token);

        User user = userRepository.findById(user_id).get();
        if(user.getPermissionLevel().charAt(8) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        Kanban kanban = new Kanban();
        kanban.setTitle(kanbanTitle.getAsString());
        Kanban dbKanban = kanbanRepository.saveAndFlush(kanban);

        KanbanUser kanbanUser = new KanbanUser();
        kanbanUser.setKanban(dbKanban);
        kanbanUser.setUser(user);

        kanbanUserRepository.save(kanbanUser);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(user);
        kanbanNotification.setSenderUser(user);

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o kanban "+dbKanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(1);
        kanbanCategory.setName(CategoryName.KANBAN_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanban(dbKanban);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user.getId())){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        user.getName()+" criou o kanban "+dbKanban.getTitle()+"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanban.getId().toString());
    }
    @PostMapping(path = "/private/user/invite/kanban")
    public ResponseEntity<String> inviteKanban(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);
        JsonObject errorMessage = new JsonObject();

        JsonElement kanbanId = kanbanJson.get("kanbanId");
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isKanban = kanbanRepository.findById(kanbanId.getAsInt()).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId.getAsInt()).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(24) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement inviteUserId = kanbanJson.get("userId");
        if(inviteUserId == null){
            errorMessage.addProperty("mensagem","O campo userId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        if(inviteUserId.getAsInt() == user_id){
            errorMessage.addProperty("mensagem","Você não pode se auto-convidar!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isUser = userRepository.findById(inviteUserId.getAsInt()).isPresent();
        if(!isUser){
            errorMessage.addProperty("mensagem","Usuário não encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanUser isInviteKanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),inviteUserId.getAsInt());
        if(isInviteKanbanUser != null){
            errorMessage.addProperty("mensagem","Usuário já está cadastrado nesse kanban!");
            errorMessage.addProperty("status",416);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        User inviteUser = userRepository.findById(inviteUserId.getAsInt()).get();

        KanbanUser inviteKanbanUser = new KanbanUser();
        inviteKanbanUser.setUser(inviteUser);
        inviteKanbanUser.setKanban(kanban);

        kanbanUserRepository.save(inviteKanbanUser);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());
        kanbanNotification.setRecipientUser(inviteUser);

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você convidou "+inviteUser.getName()+" para o kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(4);
        kanbanCategory.setName(CategoryName.KANBAN_INVITE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanban(kanban);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName()+" convidou "+inviteUser.getName()+
                                " para o kanban "+kanban.getTitle()+"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId.getAsInt());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)){
                String role = userInKanban.getUser().getRole().getName().name();
                if(role.equals("ROLE_SUPERVISOR")){
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName()+" convidou "+inviteUser.getName()+
                                    " para o kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/{kanbanId}")
    public ResponseEntity<String> patchKanban(@PathVariable Integer kanbanId,@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isKanban = kanbanRepository.findById(kanbanId).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(10) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);

        JsonElement kanbanTitle = kanbanJson.get("title");
        String oldKanbanTitle = kanban.getTitle();
        if(kanbanTitle != null){
            kanban.setTitle(kanbanTitle.getAsString());
            modifiedArr.add("título");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou ("+String.join(",",modifiedArr)+") do kanban "+
                kanban.getTitle();

        if(kanbanTitle != null){
            message = " atualizou ("+String.join(",",modifiedArr)+") do kanban "+
                    oldKanbanTitle + " (título antigo) | " + kanban.getTitle() + " (novo título).";
        }

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(2);
        kanbanCategory.setName(CategoryName.KANBAN_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanban(kanban);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName()+ finalMessage
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName()+finalMessage
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/{kanbanId}/remove/user/{targetUserId}")
    public ResponseEntity<String> uninviteKanban(@PathVariable Integer kanbanId,@PathVariable Integer targetUserId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isKanban = kanbanRepository.findById(kanbanId).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        if(targetUserId == null){
            errorMessage.addProperty("mensagem","O parametro targetUserId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer yourUserId = tokenService.validateToken(token);

        KanbanUser yourKanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),yourUserId);

        if(yourKanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(yourKanbanUser.getUser().getPermissionLevel().charAt(25) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        KanbanUser targetKanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),targetUserId);

        if(targetKanbanUser == null){
            errorMessage.addProperty("mensagem","O usuário já está fora do kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(yourKanbanUser.getUser());
        kanbanNotification.setSenderUser(yourKanbanUser.getUser());
        kanbanNotification.setRecipientUser(targetKanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você removeu "+targetKanbanUser.getUser().getName()+" do kanban "+yourKanbanUser.getKanban().getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(5);
        kanbanCategory.setName(CategoryName.KANBAN_UNINVITE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanban(yourKanbanUser.getKanban());

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), yourKanbanUser.getUser().getId())){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification();
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        yourKanbanUser.getUser().getName() + " removeu o usuário " +
                                targetKanbanUser.getUser().getName() + " do kanban " + yourKanbanUser.getKanban().getTitle() + "."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), yourKanbanUser.getUser().getId())) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            yourKanbanUser.getUser().getName() + " removeu o usuário " +
                                    targetKanbanUser.getUser().getName() + " do kanban " + yourKanbanUser.getKanban().getTitle() + "."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanUserRepository.delete(targetKanbanUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/{kanbanId}")
    public ResponseEntity<String> deleteKanban(@PathVariable Integer kanbanId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isKanban = kanbanRepository.findById(kanbanId).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(9) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanColumn> kanbanColumnList = kanbanColumnRepository.findAllByKanbanId(kanbanId);
        kanbanColumnList.forEach(column->{
            List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(column.getId());
            kanbanCardList.forEach(card->{
                List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(card.getId());
                kanbanCardCheckList.forEach(checkList->{
                    List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
                    kanbanCardCheckListItemRepository.deleteAll(kanbanCardChecklistItems);
                });
                kanbanCardCheckListRepository.deleteAll(kanbanCardCheckList);
            });
            kanbanCardRepository.deleteAll(kanbanCardList);
        });

        kanbanColumnRepository.deleteAll(kanbanColumnList);

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);

        kanbanUserRepository.deleteAll(kanbanUserList);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou o kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(3);
        kanbanCategory.setName(CategoryName.KANBAN_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        for (KanbanNotification dbNotificationKanban : kanbanNotificationRepository.findAllByKanbanId(kanbanId)) {
            dbNotificationKanban.setKanban(null);
        }

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName()+" deletou o kanban "+kanban.getTitle()+"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)){
                String role = userInKanban.getUser().getRole().getName().name();
                if(role.equals("ROLE_SUPERVISOR")){
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName()+" deletou o kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanRepository.deleteById(kanbanId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
