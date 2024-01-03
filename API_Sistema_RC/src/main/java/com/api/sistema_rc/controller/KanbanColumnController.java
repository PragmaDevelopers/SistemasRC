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
import java.util.*;

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
    private final Gson gson = new Gson();

    @GetMapping(path = "/private/user/kanban/{kanbanId}/columns")
    public ResponseEntity<String> getColumns(@PathVariable Integer kanbanId,
                                             @RequestHeader("Authorization") String token,
                                             @RequestParam(name = "cards",required = false) boolean isCards){
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
            if(isCards){
                List<KanbanCard> kanbanCardsList = kanbanCardRepository.findAllByColumnIdAndNotInnerCard(column.getId());
                JsonArray cardArr = new JsonArray();
                for(KanbanCard card : kanbanCardsList){
                    JsonObject cardObj = new JsonObject();
                    cardObj.addProperty("id",card.getId());
                    cardObj.addProperty("title",card.getTitle());
                    cardObj.addProperty("description",card.getDescription());

                    JsonArray members = new JsonArray();
                    for (String memberId : card.getMembers().split(",")) {
                        members.add(Integer.parseInt(memberId));
                    }
                    cardObj.add("members", members);

                    boolean isDeadline = kanbanDeadlineRepository.findByCardId(card.getId()).isPresent();
                    if(!isDeadline){
                        cardObj.addProperty("deadline", (String) null);
                    }else{
                        KanbanDeadline kanbanDeadline = kanbanDeadlineRepository.findByCardId(card.getId()).get();
                        JsonObject deadlineObj = new JsonObject();
                        deadlineObj.addProperty("id",kanbanDeadline.getId());
                        deadlineObj.addProperty("date",kanbanDeadline.getDate().toString());
                        deadlineObj.addProperty("overdue",kanbanDeadline.isOverdue());
                        deadlineObj.addProperty("category", kanbanDeadline.getKanbanCategory().getName().name());
                        deadlineObj.addProperty("toKanbanId",(String) null);
                        deadlineObj.addProperty("toColumnId",(String) null);
                        if(kanbanDeadline.getActionKanbanColumn() != null){
                            deadlineObj.addProperty("toKanbanId",kanbanDeadline.getActionKanbanColumn().getKanban().getId());
                            deadlineObj.addProperty("toColumnId",kanbanDeadline.getActionKanbanColumn().getId());
                        }

                        cardObj.add("deadline", deadlineObj);
                    }

                    cardObj.addProperty("index",card.getIndex());
                    List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(card.getId());
                    JsonArray checkListArr = new JsonArray();
                    for (KanbanCardChecklist checkList : kanbanCardCheckList) {
                        JsonObject checkListObj = new JsonObject();
                        checkListObj.addProperty("id",checkList.getId());
                        checkListObj.addProperty("name",checkList.getName());

                        List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
                        JsonArray checkListItemArr = new JsonArray();
                        for (KanbanCardChecklistItem checkListItem : kanbanCardChecklistItems) {
                            JsonObject checkListItemObj = new JsonObject();
                            checkListItemObj.addProperty("id",checkListItem.getId());
                            checkListItemObj.addProperty("name",checkListItem.getName());
                            checkListItemObj.addProperty("completed",checkListItem.isCompleted());
                            checkListItemArr.add(checkListItemObj);
                        }
                        checkListObj.add("items",checkListItemArr);
                        checkListArr.add(checkListObj);
                    }
                    cardObj.add("checkList",checkListArr);
                    
                    List<KanbanCardTag> kanbanCardTagList = kanbanCardTagRepository.findAllByCardId(card.getId());
                    JsonArray tagArr = new JsonArray();
                    for (KanbanCardTag kanbanCardTag : kanbanCardTagList) {
                        JsonObject tagObj = new JsonObject();
                        tagObj.addProperty("id",kanbanCardTag.getId());
                        tagObj.addProperty("name",kanbanCardTag.getName());
                        tagObj.addProperty("color",kanbanCardTag.getColor());
                        tagArr.add(tagObj);
                    }
                    cardObj.add("tags",tagArr);

                    List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(card.getId());
                    JsonArray commentArr = processComments(kanbanCardCommentList,new JsonArray());
                    cardObj.add("comments",commentArr);

                    List<KanbanCard> kanbanInnerCardList = kanbanCardRepository.findAllByInnerCardId(card.getId());
                    JsonArray innerCardArr = processInnerCards(kanbanInnerCardList,new JsonArray());
                    cardObj.add("innerCards",innerCardArr);

                    cardArr.add(cardObj);
                }
                columnObj.add("cards",cardArr);
            }

            columnsArr.add(columnObj);
        });
        return ResponseEntity.status(HttpStatus.OK).body(columnsArr.toString());
    }

    public JsonArray processComments(List<KanbanCardComment> comments, JsonArray arr) {
        Map<Integer, JsonObject> commentMap = new HashMap<>();

        comments.forEach(comment -> {
            // Processar o comentário atual
            JsonObject commentObj = new JsonObject();
            commentObj.addProperty("id", comment.getId());
            commentObj.addProperty("content", comment.getContent());
            commentObj.addProperty("edited", comment.isEdited());

            if (comment.getRegistration_date() == null) {
                commentObj.addProperty("registration_date", (String) null);
            } else {
                commentObj.addProperty("registration_date", String.valueOf(comment.getRegistration_date()));
            }

            JsonObject userObj = new JsonObject();
            userObj.addProperty("id", comment.getUser().getId());
            userObj.addProperty("name", comment.getUser().getName());
            userObj.addProperty("email", comment.getUser().getEmail());
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

    public JsonArray processInnerCards(List<KanbanCard> innerCards,JsonArray arr){
        Map<Integer, JsonObject> innerCardMap = new HashMap<>();

        innerCards.forEach(innerCard -> {
            // Processar o comentário atual
            JsonObject innerCardObj = new JsonObject();

            innerCardObj.addProperty("id",innerCard.getId());
            innerCardObj.addProperty("title",innerCard.getTitle());
            innerCardObj.addProperty("description",innerCard.getDescription());

            JsonArray members = new JsonArray();
            for (String memberId : innerCard.getMembers().split(",")) {
                members.add(Integer.parseInt(memberId));
            }
            innerCardObj.add("members", members);

//            if(innerCard.getDeadline() == null){
//                innerCardObj.addProperty("deadline", (String) null);
//            }else{
//                innerCardObj.addProperty("deadline", String.valueOf(innerCard.getDeadline()));
//            }
            innerCardObj.addProperty("index",innerCard.getIndex());

            List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(innerCard.getId());
            JsonArray checkListArr = new JsonArray();
            for (KanbanCardChecklist checkList : kanbanCardCheckList) {
                JsonObject checkListObj = new JsonObject();
                checkListObj.addProperty("id",checkList.getId());
                checkListObj.addProperty("name",checkList.getName());

                List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
                JsonArray checkListItemArr = new JsonArray();
                for (KanbanCardChecklistItem checkListItem : kanbanCardChecklistItems) {
                    JsonObject checkListItemObj = new JsonObject();
                    checkListItemObj.addProperty("id",checkListItem.getId());
                    checkListItemObj.addProperty("name",checkListItem.getName());
                    checkListItemObj.addProperty("completed",checkListItem.isCompleted());
                    checkListItemArr.add(checkListItemObj);
                }
                checkListObj.add("items",checkListItemArr);
                checkListArr.add(checkListObj);
            }
            innerCardObj.add("checkList",checkListArr);

            List<KanbanCardTag> kanbanCardTagList = kanbanCardTagRepository.findAllByCardId(innerCard.getId());
            JsonArray tagArr = new JsonArray();
            for (KanbanCardTag kanbanCardTag : kanbanCardTagList) {
                JsonObject tagObj = new JsonObject();
                tagObj.addProperty("id",kanbanCardTag.getId());
                tagObj.addProperty("name",kanbanCardTag.getName());
                tagObj.addProperty("color",kanbanCardTag.getColor());
                tagArr.add(tagObj);
            }
            innerCardObj.add("tags",tagArr);

            List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(innerCard.getId());
            JsonArray commentArr = processComments(kanbanCardCommentList,new JsonArray());
            innerCardObj.add("comments",commentArr);

            // Verificar se há respostas e chamar recursivamente
            List<KanbanCard> kanbanInnerCardList = kanbanCardRepository.findAllByInnerCardId(innerCard.getId());

            if (!kanbanInnerCardList.isEmpty()) {
                JsonArray innerCardArr = new JsonArray();
                processInnerCards(kanbanInnerCardList, innerCardArr);
                innerCardObj.add("innerCards", innerCardArr);
            }

            innerCardMap.put(innerCard.getId(), innerCardObj);
        });

        innerCards.forEach(innerCard -> {
            arr.add(innerCardMap.get(innerCard.getId()));
        });

        return arr;
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

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou a coluna "+dbKanbanColumn.getTitle()+" no kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

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
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

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

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId);
        kanbanCardList.forEach(cardId->{
            List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(cardId.getId());
            kanbanCardCheckList.forEach(checkList->{
                List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
                kanbanCardCheckListItemRepository.deleteAll(kanbanCardChecklistItems);
            });
            kanbanCardCheckListRepository.deleteAll(kanbanCardCheckList);

            List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(cardId.getId());
            kanbanCardCommentRepository.deleteAll(kanbanCardCommentList);
        });

        kanbanCardRepository.deleteAll(kanbanCardList);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(8);
        kanbanCategory.setName(CategoryName.COLUMN_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        for (KanbanNotification dbNotificationColumn : kanbanNotificationRepository.findAllByColumnId(columnId)) {
            dbNotificationColumn.setKanbanColumn(null);
        }

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName()+" deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
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
                            kanbanUser.getUser().getName()+" deletou a coluna "+selectedColumn.getTitle()+" do kanban "+kanban.getTitle()+"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanColumnRepository.deleteById(columnId);

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

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

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

    @Transactional
    @PatchMapping(path = "/private/user/kanban/move/column")
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
        for (int i = 0;i < toColumnList.size();i++) {
            if(Objects.equals(toColumnList.get(i).getId(), selectedColumn.getId()) && Objects.equals(toColumnList.get(i + 1).getIndex(), selectedColumn.getIndex())){
                toColumnList.get(i).setIndex(i + 1);
                toColumnList.get(i + 1).setIndex(i);
                i+=2;
                if(i >= toColumnList.size()){
                    break;
                }
            }else if(Objects.equals(toColumnList.get(i).getId(), selectedColumn.getId()) && toColumnList.get(i - 1).getIndex() == toIndex.getAsInt()){
                toColumnList.get(i).setIndex(i - 1);
                toColumnList.get(i - 1).setIndex(i);
                i+=1;
            }{
                toColumnList.get(i).setIndex(i);
            }
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você moveu a coluna " +
                        selectedColumn.getTitle() + " do kanban " + kanban.getTitle() + "."
        );
        kanbanNotification.setViewed(false);

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
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
