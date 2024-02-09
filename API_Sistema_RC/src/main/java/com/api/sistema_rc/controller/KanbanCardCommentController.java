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
public class KanbanCardCommentController {
    @Autowired
    private TokenService tokenService;
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
    private MailService mailService;
    @Autowired
    private Gson gson;
    ExecutorService executorService = Executors.newCachedThreadPool();
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/comments")
    public ResponseEntity<String> getComments(@PathVariable Integer cardId, @RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",460);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",464);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",461);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(cardId);
        JsonArray commentArr = processComments(kanbanCardCommentList,new JsonArray());

        return ResponseEntity.status(HttpStatus.OK).body(commentArr.toString());
    }

    public JsonArray processComments(List<KanbanCardComment> comments, JsonArray arr) {
        Map<Integer, JsonObject> commentMap = new HashMap<>();

        comments.forEach(comment -> {
            // Processar o comentário atual
            JsonObject commentObj = new JsonObject();
            commentObj.addProperty("id", comment.getId());
            commentObj.addProperty("content", comment.getContent());
            commentObj.addProperty("edited", comment.isEdited());

            if (comment.getRegistrationDate() == null) {
                commentObj.addProperty("registrationDate", (String) null);
            } else {
                commentObj.addProperty("registrationDate", String.valueOf(comment.getRegistrationDate()));
            }

            JsonObject userObj = new JsonObject();
            userObj.addProperty("id", comment.getUser().getId());
            userObj.addProperty("name", comment.getUser().getName());
            userObj.addProperty("email", comment.getUser().getEmail());
            if(comment.getUser().getProfilePicture() == null){
                userObj.addProperty("profilePicture",(String) null);
            }else{
                try {
                    byte[] bytes = comment.getUser().getProfilePicture().getBytes(1,(int) comment.getUser().getProfilePicture().length());
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    userObj.addProperty("profilePicture","data:image/"+comment.getUser().getPictureFormat()+";base64,"+encoded);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            commentObj.add("user", userObj);

            // Verificar se há respostas e chamar recursivamente
            List<KanbanCardComment> kanbanCardCommentAnsweredList = kanbanCardCommentRepository.findAllByCommentAnsweredId(comment.getId());

            if (!kanbanCardCommentAnsweredList.isEmpty()) {
                JsonArray answerArr = new JsonArray();
                processComments(kanbanCardCommentAnsweredList, answerArr);
                commentObj.add("answers", answerArr);
            }

            commentMap.put(comment.getId(), commentObj);
        });

        comments.forEach(comment -> {
            // Verificar se o comentário tem um pai (foi respondido)
            if (comment.getKanbanCommentAnswered() != null) {
                int parentId = comment.getKanbanCommentAnswered().getId();
                JsonObject parentComment = commentMap.get(parentId);

                // Adicionar como resposta ao pai
                if (parentComment != null) {
                    JsonArray answers = parentComment.getAsJsonArray("answers");
                    answers.add(commentMap.get(comment.getId()));
                }
            } else {
                // Adicionar o comentário principal ao array
                arr.add(commentMap.get(comment.getId()));
            }
        });

        return arr;
    }

    @PostMapping(path = "/private/user/kanban/column/card/comment")
    public ResponseEntity<String> postComment(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",460);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",464);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",461);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(17) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar comentário)!");
            errorMessage.addProperty("status",465);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement commentContent = jsonObj.get("content");
        if(commentContent == null){
            errorMessage.addProperty("mensagem","O campo content é necessário!");
            errorMessage.addProperty("status",460);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardComment kanbanCardComment = new KanbanCardComment();

        kanbanCardComment.setContent(commentContent.getAsString());
        kanbanCardComment.setRegistrationDate(LocalDateTime.now());
        kanbanCardComment.setEdited(false);

        kanbanCardComment.setKanbanCard(kanbanCard);
        kanbanCardComment.setUser(kanbanUser.getUser());

        KanbanCardComment dbKanbanCardComment = kanbanCardCommentRepository.saveAndFlush(kanbanCardComment);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você criou o comentário " + dbKanbanCardComment.getContent() +
                            " no card "+ kanbanCard.getTitle()+
                            " da coluna "+kanbanCard.getKanbanColumn().getTitle()+" do kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Criação do comentário "+dbKanbanCardComment.getContent(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(18);
            kanbanCategory.setName(CategoryName.CARDCOMMENT_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanCardComment(dbKanbanCardComment);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " criou o comentário  " +
                                    dbKanbanCardComment.getContent() + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                                    " da coluna "+kanbanCard.getKanbanColumn().getTitle()+" do kanban "+kanban.getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Criação do comentário "+dbKanbanCardComment.getContent(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName() + " criou o comentário  " +
                                        dbKanbanCardComment.getContent() + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                                        " da coluna "+kanbanCard.getKanbanColumn().getTitle()+" do kanban "+kanban.getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Criação do comentário "+dbKanbanCardComment.getContent(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardComment.getId().toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/comment/answer")
    public ResponseEntity<String> postCommentAnswer(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement commentId = jsonObj.get("commentId");
        if(commentId == null){
            errorMessage.addProperty("mensagem","O campo commentId é necessário!");
            errorMessage.addProperty("status",460);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isComment = kanbanCardCommentRepository.findById(commentId.getAsInt()).isPresent();
        if(!isComment){
            errorMessage.addProperty("mensagem","Comment não foi encontrado!");
            errorMessage.addProperty("status",464);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardComment kanbanCardComment = kanbanCardCommentRepository.findById(commentId.getAsInt()).get();

        Kanban kanban = kanbanCardComment.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",461);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(17) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar comentário)!");
            errorMessage.addProperty("status",465);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement commentContent = jsonObj.get("content");
        if(commentContent == null){
            errorMessage.addProperty("mensagem","O campo content é necessário!");
            errorMessage.addProperty("status",460);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardComment commentAnswered = new KanbanCardComment();

        commentAnswered.setContent(commentContent.getAsString());
        commentAnswered.setRegistrationDate(LocalDateTime.now());
        commentAnswered.setEdited(false);

        commentAnswered.setKanbanCard(kanbanCardComment.getKanbanCard());
        commentAnswered.setUser(kanbanUser.getUser());
        commentAnswered.setKanbanCommentAnswered(kanbanCardComment);

        KanbanCardComment dbKanbanCardComment = kanbanCardCommentRepository.saveAndFlush(commentAnswered);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você respondeu o comentário " + dbKanbanCardComment.getKanbanCommentAnswered().getContent()
                            + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                            " da coluna "+dbKanbanCardComment.getKanbanCard().getKanbanColumn().getTitle()+
                            " do kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Respondendo comentário "+dbKanbanCardComment.getKanbanCommentAnswered().getContent(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(19);
            kanbanCategory.setName(CategoryName.CARDCOMMENTANSWERED_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanCardComment(dbKanbanCardComment);

            kanbanNotificationList.add(kanbanNotification);

            User userAnswered = dbKanbanCardComment.getKanbanCommentAnswered().getUser();

            KanbanNotification kanbanNotificationUserAnswered = new KanbanNotification(kanbanNotification);
            kanbanNotificationUserAnswered.setUser(userAnswered);
            kanbanNotificationUserAnswered.setMessage(
                    kanbanUser.getUser().getName() + " respondeu o seu comentário " + dbKanbanCardComment.getKanbanCommentAnswered().getContent()
                            + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                            " da coluna "+dbKanbanCardComment.getKanbanCard().getKanbanColumn().getTitle()+
                            " do kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(userAnswered.getEmail(),"Respondendo comentário "+dbKanbanCardComment.getKanbanCommentAnswered().getContent(),kanbanNotificationUserAnswered.getMessage());
            kanbanNotificationList.add(kanbanNotificationUserAnswered);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id) && !Objects.equals(userAdmin.getId(), userAnswered.getId())){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " respondeu o comentário " + dbKanbanCardComment.getKanbanCommentAnswered().getContent()
                                    + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                                    " da coluna "+dbKanbanCardComment.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+kanban.getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Respondendo comentário "+dbKanbanCardComment.getKanbanCommentAnswered().getContent(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
            kanbanUserList.forEach(userInKanban->{
                if(!Objects.equals(userInKanban.getUser().getId(), user_id) && !Objects.equals(userInKanban.getUser().getId(), userAnswered.getId())) {
                    String role = userInKanban.getUser().getRole().getName().name();
                    if (role.equals("ROLE_SUPERVISOR")) {
                        KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                        kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                        kanbanNotificationSupervisor.setMessage(
                                kanbanUser.getUser().getName() + " respondeu o comentário " +
                                        dbKanbanCardComment.getKanbanCommentAnswered().getContent()
                                        + " no card "+dbKanbanCardComment.getKanbanCard().getTitle()+
                                        " da coluna "+dbKanbanCardComment.getKanbanCard().getKanbanColumn().getTitle()+
                                        " do kanban "+kanban.getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Respondendo comentário "+dbKanbanCardComment.getKanbanCommentAnswered().getContent(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardComment.getId().toString());
    }

    @DeleteMapping(path = "/private/user/kanban/column/card/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer commentId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isComment = kanbanCardCommentRepository.findById(commentId).isPresent();
        if(!isComment){
            errorMessage.addProperty("mensagem","Comment não foi encontrado!");
            errorMessage.addProperty("status",464);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardComment selectedComment = kanbanCardCommentRepository.findById(commentId).get();

        Kanban kanban = selectedComment.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",461);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(Objects.equals(selectedComment.getUser().getId(), kanbanUser.getUser().getId())){
            if(kanbanUser.getUser().getPermissionLevel().charAt(20) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar comentário próprio)!");
                errorMessage.addProperty("status",465);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }else{
            if(kanbanUser.getUser().getPermissionLevel().charAt(21) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar comentário externo)!");
                errorMessage.addProperty("status",465);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }

        executorService.submit(() -> {
            List<KanbanCardComment> kanbanCardCommentAnsweredList = kanbanCardCommentRepository.findAllByCommentAnsweredId(selectedComment.getId());
            if(!kanbanCardCommentAnsweredList.isEmpty()){
                kanbanCardCommentRepository.deleteAll(kanbanCardCommentAnsweredList);
            }

            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage(
                    "Você deletou o comentário " + selectedComment.getContent()
                            + " no card "+selectedComment.getKanbanCard().getTitle()+
                            " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                            " do kanban "+kanban.getTitle()+"."
            );
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Deletando comentário "+selectedComment.getContent(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(21);
            kanbanCategory.setName(CategoryName.CARDCOMMENT_DELETE);
            kanbanNotification.setKanbanCategory(kanbanCategory);
            kanbanNotification.setKanbanCardComment(null);

            kanbanNotificationList.add(kanbanNotification);

            if(!Objects.equals(selectedComment.getUser().getId(), kanbanUser.getUser().getId())){
                KanbanNotification kanbanNotificationUserDeleted = new KanbanNotification(kanbanNotification);
                kanbanNotificationUserDeleted.setUser(selectedComment.getUser());
                kanbanNotificationUserDeleted.setMessage(
                        kanbanUser.getUser().getName() + " deletou o seu comentário " + selectedComment.getContent()
                                + " no card "+selectedComment.getKanbanCard().getTitle()+
                                " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                                " do kanban "+kanban.getTitle()+"."
                );
                mailService.sendMail(selectedComment.getUser().getEmail(),"Deletando comentário "+selectedComment.getContent(),kanbanNotificationUserDeleted.getMessage());
                kanbanNotificationList.add(kanbanNotificationUserDeleted);
            }

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            kanbanUser.getUser().getName() + " deletou o comentário " + selectedComment.getContent()
                                    + " no card "+selectedComment.getKanbanCard().getTitle()+
                                    " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                                    " do kanban "+kanban.getTitle()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Deletando comentário "+selectedComment.getContent(),kanbanNotificationAdmin.getMessage());
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
                                kanbanUser.getUser().getName() + " deletou o comentário " + selectedComment.getContent()
                                        + " no card "+selectedComment.getKanbanCard().getTitle()+
                                        " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                                        " do kanban "+kanban.getTitle()+"."
                        );
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Deletando comentário "+selectedComment.getContent(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);

            kanbanCardCommentRepository.deleteById(commentId);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/comment/{commentId}")
    public ResponseEntity<String> patchComment(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer commentId){
        JsonObject errorMessage = new JsonObject();

        boolean isComment = kanbanCardCommentRepository.findById(commentId).isPresent();
        if(!isComment){
            errorMessage.addProperty("mensagem","Comment não foi encontrado!");
            errorMessage.addProperty("status",464);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardComment selectedComment = kanbanCardCommentRepository.findById(commentId).get();

        Kanban kanban = selectedComment.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",461);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(Objects.equals(selectedComment.getUser().getId(), kanbanUser.getUser().getId())){
            if(kanbanUser.getUser().getPermissionLevel().charAt(18) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (editar comentário próprio)!");
                errorMessage.addProperty("status",465);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }else{
            if(kanbanUser.getUser().getPermissionLevel().charAt(19) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (editar comentário externo)!");
                errorMessage.addProperty("status",465);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement commentContent = jsonObj.get("content");
        String oldCommentContent = selectedComment.getContent();
        if(commentContent != null){
            selectedComment.setContent(commentContent.getAsString());
            selectedComment.setEdited(true);
            modifiedArr.add("conteúdo");
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(kanbanUser.getUser());
            kanbanNotification.setSenderUser(kanbanUser.getUser());

            String message = " atualizou ("+String.join(",",modifiedArr)+") no comentário "+oldCommentContent+
                    " do card "+selectedComment.getKanbanCard().getTitle()+
                    " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                    " do kanban "+kanban.getTitle()+".";

            if(commentContent != null){
                message = " atualizou ("+String.join(",",modifiedArr)+") no comentário "+
                        oldCommentContent + " (conteúdo antigo) | "+selectedComment.getContent()+
                        " (novo conteúdo) do card "+selectedComment.getKanbanCard().getTitle()+
                        " da coluna "+selectedComment.getKanbanCard().getKanbanColumn().getTitle()+
                        " do kanban "+kanban.getTitle()+".";
            }

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);
            kanbanNotification.setMessage("Você"+message);
            mailService.sendMail(kanbanUser.getUser().getEmail(),"Atualização do comentário "+selectedComment.getContent(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(20);
            kanbanCategory.setName(CategoryName.CARDCOMMENT_UPDATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setKanbanCardComment(selectedComment);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            String finalMessage = message;
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user_id)){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName()+ finalMessage);
                    mailService.sendMail(userAdmin.getEmail(),"Atualização do comentário "+selectedComment.getContent(),kanbanNotificationAdmin.getMessage());
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
                        mailService.sendMail(userInKanban.getUser().getEmail(),"Atualização do comentário "+selectedComment.getContent(),kanbanNotificationSupervisor.getMessage());
                        kanbanNotificationList.add(kanbanNotificationSupervisor);
                    }
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
