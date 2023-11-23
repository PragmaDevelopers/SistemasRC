package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.KanbanRoleName;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
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
    private KanbanCheckListRepository kanbanCheckListRepository;
    @Autowired
    private KanbanCheckListItemRepository kanbanCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
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
    public ResponseEntity<String> getKanbanUser(@PathVariable Integer kanbanId,@RequestHeader("Authorization") String token){
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
            formattedUser.addProperty("kanban_role",userInKanban.getRole().getName().name());
            formattedUser.addProperty("permission_level",userInKanban.getPermissionLevel());
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

        Kanban kanban = new Kanban();
        kanban.setTitle(kanbanTitle.getAsString());
        Kanban dbKanban = kanbanRepository.saveAndFlush(kanban);

        Integer user_id = tokenService.validateToken(token);

        User user = userRepository.findById(user_id).get();

        KanbanUser kanbanUser = new KanbanUser();
        kanbanUser.setKanban(dbKanban);
        kanbanUser.setUser(user);
        kanbanUser.setPermissionLevel("11111");

        KanbanRole kanbanRole = new KanbanRole();
        kanbanRole.setId(1);
        kanbanRole.setName(KanbanRoleName.ADMIN);

        kanbanUser.setRole(kanbanRole);

        kanbanUserRepository.save(kanbanUser);

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

        if(!kanbanUser.getRole().getName().name().equals("ADMIN") && !kanbanUser.getRole().getName().name().equals("SUPERVISOR")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement isSupervisor = kanbanJson.get("isSupervisor");
        if(isSupervisor == null){
            errorMessage.addProperty("mensagem","O campo isSupervisor é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        if(isSupervisor.getAsBoolean()){
            if(!kanbanUser.getRole().getName().name().equals("ADMIN")){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Definir o convidado como supervisor)!");
                errorMessage.addProperty("status",415);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
        }

        JsonElement inviteUserId = kanbanJson.get("userId");
        if(inviteUserId == null){
            errorMessage.addProperty("mensagem","O campo userId é necessário!");
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

        JsonElement isRead = kanbanJson.get("isRead");
        if(isRead == null){
            errorMessage.addProperty("mensagem","O campo isRead é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isCreate = kanbanJson.get("isCreate");
        if(isCreate == null){
            errorMessage.addProperty("mensagem","O campo isCreate é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isUpdate = kanbanJson.get("isUpdate");
        if(isUpdate == null){
            errorMessage.addProperty("mensagem","O campo isUpdate é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isMove = kanbanJson.get("isMove");
        if(isMove == null){
            errorMessage.addProperty("mensagem","O campo isMove é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isDelete = kanbanJson.get("isDelete");
        if(isDelete == null){
            errorMessage.addProperty("mensagem","O campo isDelete é necessário!");
            errorMessage.addProperty("status",410);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        User user = userRepository.findById(inviteUserId.getAsInt()).get();

        KanbanUser inviteKanbanUser = new KanbanUser();
        inviteKanbanUser.setUser(user);
        inviteKanbanUser.setKanban(kanban);

        char[] chars = "00000".toCharArray();
        if(isRead.getAsBoolean()){
            chars[0] = '1';
        }
        if(isCreate.getAsBoolean()){
            chars[1] = '1';
        }
        if(isUpdate.getAsBoolean()){
            chars[2] = '1';
        }
        if(isMove.getAsBoolean()){
            chars[3] = '1';
        }
        if(isDelete.getAsBoolean()){
            chars[4] = '1';
        }
        inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);

        KanbanRole kanbanRole = new KanbanRole();
        if(isSupervisor.getAsBoolean()){
            kanbanRole.setId(2);
            kanbanRole.setName(KanbanRoleName.SUPERVISOR);
        }else{
            kanbanRole.setId(3);
            kanbanRole.setName(KanbanRoleName.MEMBER);
        }
        inviteKanbanUser.setRole(kanbanRole);

        kanbanUserRepository.save(inviteKanbanUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/{kanbanId}/user")
    public ResponseEntity<String> patchKanbanUser(@PathVariable Integer kanbanId,@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);
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

        if(!kanbanUser.getRole().getName().name().equals("ADMIN") && !kanbanUser.getRole().getName().name().equals("SUPERVISOR")){
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

        boolean isUser = userRepository.findById(inviteUserId.getAsInt()).isPresent();
        if(!isUser){
            errorMessage.addProperty("mensagem","Usuário não encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanUser inviteKanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),inviteUserId.getAsInt());
        if(inviteKanbanUser == null){
            errorMessage.addProperty("mensagem","Usuário não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",416);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isSupervisor = kanbanJson.get("isSupervisor");
        if(isSupervisor != null){
            if(kanbanUser.getRole().getName().name().equals("ADMIN")){
                KanbanRole kanbanRole = new KanbanRole();
                if(isSupervisor.getAsBoolean()){
                    kanbanRole.setId(2);
                    kanbanRole.setName(KanbanRoleName.SUPERVISOR);
                    inviteKanbanUser.setRole(kanbanRole);
                }else{
                    kanbanRole.setId(3);
                    kanbanRole.setName(KanbanRoleName.MEMBER);
                    inviteKanbanUser.setRole(kanbanRole);
                }
            }else{
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar o cargo uma pessoa)!");
                errorMessage.addProperty("status",415);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
        }

        if(kanbanUser.getRole().getName().name().equals("SUPERVISOR") && inviteKanbanUser.getRole().getName().name().equals("SUPERVISOR")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar as permissões de outro supervisor)!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        if(kanbanUser.getRole().getName().name().equals("SUPERVISOR") && inviteKanbanUser.getRole().getName().name().equals("ADMIN")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar as permissões de um admin)!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isRead = kanbanJson.get("isRead");
        if(isRead != null){
            if(isRead.getAsBoolean()){
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[0] = '1';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }else{
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[0] = '0';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }
        }

        JsonElement isCreate = kanbanJson.get("isCreate");
        if(isCreate != null){
            if(isCreate.getAsBoolean()){
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[1] = '1';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }else{
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[1] = '0';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }
        }

        JsonElement isUpdate = kanbanJson.get("isUpdate");
        if(isUpdate != null){
            if(isUpdate.getAsBoolean()){
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[2] = '1';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }else{
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[2] = '0';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }
        }

        JsonElement isMove = kanbanJson.get("isMove");
        if(isMove != null){
            if(isMove.getAsBoolean()){
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[3] = '1';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }else{
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[3] = '0';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }
        }

        JsonElement isDelete = kanbanJson.get("isDelete");
        if(isDelete != null){
            if(isDelete.getAsBoolean()){
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[4] = '1';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }else{
                char[] chars = inviteKanbanUser.getPermissionLevel().toCharArray();
                chars[4] = '0';
                inviteKanbanUser.setPermissionLevel(chars[0]+""+chars[1]+chars[2]+chars[3]+chars[4]);
            }
        }

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

        if(!kanbanUser.getRole().getName().name().equals("ADMIN")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);
        JsonElement kanbanTitle = kanbanJson.get("title");
        if(kanbanTitle != null){
            kanban.setTitle(kanbanTitle.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/{kanbanId}/user")
    public ResponseEntity<String> exitKanban(@PathVariable Integer kanbanId,@RequestParam(required = false) Integer nextAdminId,@RequestHeader("Authorization") String token){
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
        Kanban kanban = kanbanRepository.findById(kanbanId).get();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",411);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getRole().getName().name().equals("ADMIN")){
            if(nextAdminId == null || nextAdminId.equals(user_id)){
                errorMessage.addProperty("mensagem","Por se um admin é necessário passar o id do usuário a herdar o cargo!");
                errorMessage.addProperty("status",415);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
            KanbanUser nextAdmin = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),nextAdminId);
            if(nextAdmin == null){
                errorMessage.addProperty("mensagem","Proximo admin não foi encontrado!");
                errorMessage.addProperty("status",414);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            kanbanUserRepository.updateById("11111",1,nextAdmin.getId());
        }

        kanbanUserRepository.delete(kanbanUser);

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

        if(!kanbanUser.getRole().getName().name().equals("ADMIN")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",415);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanColumn> kanbanColumnList = kanbanColumnRepository.findAllByKanbanId(kanbanId);
        kanbanColumnList.forEach(column->{
            List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(column.getId());
            kanbanCardList.forEach(card->{
                List<KanbanCheckList> kanbanCheckList = kanbanCheckListRepository.findAllByCardId(card.getId());
                kanbanCheckList.forEach(checkList->{
                    List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkList.getId());
                    kanbanCheckListItemRepository.deleteAll(kanbanCheckListItems);
                });
                kanbanCheckListRepository.deleteAll(kanbanCheckList);
            });
            kanbanCardRepository.deleteAll(kanbanCardList);
        });

        kanbanColumnRepository.deleteAll(kanbanColumnList);

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);
        kanbanUserRepository.deleteAll(kanbanUserList);

        kanbanRepository.deleteById(kanbanId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
