package com.api.sistema_rc.controller;

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
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(path = "/api")
public class KanbanCardController {
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
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/{columnId}/cards")
    public ResponseEntity<String> getCards(@PathVariable Integer columnId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        if(columnId == null){
            errorMessage.addProperty("mensagem","O campo columnId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanColumnRepository.findById(columnId).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn kanbanColumn = kanbanColumnRepository.findById(columnId).get();

        Kanban kanban = kanbanColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId);

        JsonArray cardsArr = new JsonArray();

        kanbanCardList.forEach(card->{
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id",card.getId());
            cardObj.addProperty("title",card.getTitle());
            cardObj.addProperty("description",card.getDescription());
            cardObj.addProperty("members",card.getMembers());
            if(card.getDeadline() == null){
                cardObj.addProperty("deadline", (String) null);
            }else{
                cardObj.addProperty("deadline", String.valueOf(card.getDeadline()));
            }
            cardObj.addProperty("index",card.getIndex());
            cardsArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(cardsArr.toString());
    }
    @PostMapping(path = "/private/user/kanban/column/card")
    public ResponseEntity<String> postCard(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement columnId = jsonObj.get("columnId");
        if(columnId == null){
            errorMessage.addProperty("mensagem","O campo columnId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanColumnRepository.findById(columnId.getAsInt()).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn kanbanColumn = kanbanColumnRepository.findById(columnId.getAsInt()).get();

        Kanban kanban = kanbanColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(0) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar card)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId.getAsInt());

        KanbanCard kanbanCard = new KanbanCard();

        kanbanCard.setIndex(kanbanCardList.size());

        kanbanCard.setTitle(cardTitle.getAsString());
        kanbanCard.setKanbanColumn(kanbanColumn);

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription != null){
            kanbanCard.setDescription(cardDescription.getAsString());
        }

        JsonElement cardDeadline = jsonObj.get("deadline");
        if(cardDeadline != null){
            if(kanbanUser.getUser().getPermissionLevel().charAt(14) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar prazo)!");
                errorMessage.addProperty("status",435);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
            LocalDateTime deadline = LocalDateTime.parse(cardDeadline.getAsString());
            kanbanCard.setDeadline(deadline);
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers != null){
            JsonArray membersId = cardMembers.getAsJsonArray();
            List<String> arrayToStringArr = new ArrayList<>();
            if(!membersId.isEmpty()){
                Integer kanbanId = kanban.getId();
                List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);

                for (JsonElement memberId : membersId) {
                    AtomicBoolean isFound = new AtomicBoolean(false);
                    kanbanUserList.forEach(kanUser ->{
                        if(kanUser.getUser().getId() == memberId.getAsInt()){
                            isFound.set(true);
                        }
                    });
                    if(!isFound.get()){
                        errorMessage.addProperty("mensagem","Usuário não existente nesse kanban detectado!");
                        errorMessage.addProperty("status",433);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                    }
                    arrayToStringArr.add(String.valueOf(memberId.getAsInt()));
                }
            }
            kanbanCard.setMembers(String.join(",",arrayToStringArr));
        }

        KanbanCard dbKanbanCard = kanbanCardRepository.saveAndFlush(kanbanCard);

        JsonObject returnIds = new JsonObject();
        returnIds.addProperty("id",dbKanbanCard.getId());

        JsonElement cardTags = jsonObj.get("tags");
        if(cardTags != null){
            if(!cardTags.getAsJsonArray().isEmpty()){
                try {
                    List<KanbanCardTag> tagsArr = new ArrayList<>();
                    cardTags.getAsJsonArray().forEach(tag->{
                        KanbanCardTag kanbanCardTag = new KanbanCardTag();
                        kanbanCardTag.setKanbanCard(dbKanbanCard);
                        if(tag.getAsJsonObject().get("name") == null){
                            throw new Error("O campo name é necessário!");
                        }
                        if(tag.getAsJsonObject().get("color") == null){
                            throw new Error("O campo color é necessário!");
                        }

                        kanbanCardTag.setName(tag.getAsJsonObject().get("name").getAsString());
                        kanbanCardTag.setColor(tag.getAsJsonObject().get("color").getAsString());
                        tagsArr.add(kanbanCardTag);

                        List<KanbanCardTag> dbKanbanCardTagList = kanbanCardTagRepository.saveAllAndFlush(tagsArr);
                        dbKanbanCardTagList.forEach(dbTag-> {
                            JsonObject formattedTag = new JsonObject();
                            formattedTag.addProperty("id",dbTag.getId());
                            returnIds.add("tags", formattedTag);
                        });
                    });
                }catch (Error e){
                    errorMessage.addProperty("mensagem",e.getMessage());
                    errorMessage.addProperty("status",430);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                }
            }
        }

        JsonElement cardChecklists = jsonObj.get("checklists");
        if(cardChecklists != null){
            try{
                cardChecklists.getAsJsonArray().forEach(checklist->{
                    KanbanCardChecklist kanbanCardCheckList = new KanbanCardChecklist();
                    if(checklist.getAsJsonObject().get("name") == null){
                        throw new Error("O campo name é necessário no checklist!");
                    }
                    kanbanCardCheckList.setName(checklist.getAsJsonObject().get("name").getAsString());
                    kanbanCardCheckList.setKanbanCard(dbKanbanCard);

                    KanbanCardChecklist dbKanbanCardChecklist = kanbanCardCheckListRepository.saveAndFlush(kanbanCardCheckList);
                    JsonObject checkListObj = new JsonObject();
                    checkListObj.addProperty("id", dbKanbanCardChecklist.getId());
                    checkListObj.add("items",new JsonArray());
                    returnIds.add("checklists", checkListObj);

                    checklist.getAsJsonObject().get("items").getAsJsonArray().forEach(item->{
                        KanbanCardChecklistItem kanbanCardChecklistItem = new KanbanCardChecklistItem();
                        if(item.getAsJsonObject().get("name") == null){
                            throw  new Error("O campo name é necessário no checklist item!");
                        }
                        kanbanCardChecklistItem.setName(item.getAsJsonObject().get("name").getAsString());
                        kanbanCardChecklistItem.setCompleted(false);
                        kanbanCardChecklistItem.setKanbanChecklist(dbKanbanCardChecklist);

                        KanbanCardChecklistItem dbKanbanCheckListItemCard = kanbanCardCheckListItemRepository.saveAndFlush(kanbanCardChecklistItem);
                        JsonObject checkListItemObj = new JsonObject();
                        checkListItemObj.addProperty("id", dbKanbanCheckListItemCard.getId());
                        returnIds.get("checklists").getAsJsonObject().get("items").getAsJsonArray().add(checkListItemObj);
                    });
                });
            }catch (Error e){
                errorMessage.addProperty("mensagem",e.getMessage());
                errorMessage.addProperty("status",430);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }

        }

        JsonElement cardComments = jsonObj.get("comments");
        if(cardComments != null){
            try{
                JsonArray commentsId = processComments(cardComments.getAsJsonArray(),dbKanbanCard,kanbanUser,null,new JsonArray());
                returnIds.add("comments",commentsId);
            }catch (Error e){
                errorMessage.addProperty("mensagem",e.getMessage());
                errorMessage.addProperty("status",430);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
        }

        JsonElement innerCards = jsonObj.get("innerCards");
        if(innerCards != null){
            try {
                JsonArray innerCardsId = processInnerCards(innerCards.getAsJsonArray(),kanbanColumn,dbKanbanCard,new JsonArray());
                returnIds.add("innerCards",innerCardsId);
            }catch (Error e){
                errorMessage.addProperty("mensagem",e.getMessage());
                errorMessage.addProperty("status",430);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnIds.toString());
    }

    public JsonArray processComments(JsonArray comments, KanbanCard dbKanbanCard, KanbanUser kanbanUser,
                                     KanbanCardComment commentAnswers, JsonArray arr) {
        comments.forEach(commentElement->{
            JsonObject comment = commentElement.getAsJsonObject();
            KanbanCardComment kanbanCardComment = new KanbanCardComment();
            if(comment.get("content") == null){
                throw new Error("O campo content é necessário no comment!");
            }
            kanbanCardComment.setContent(comment.get("content").getAsString());
            kanbanCardComment.setEdited(false);
            kanbanCardComment.setKanbanCard(dbKanbanCard);
            kanbanCardComment.setUser(kanbanUser.getUser());
            kanbanCardComment.setRegistration_date(LocalDateTime.now());

            if (commentAnswers != null) {
                kanbanCardComment.setKanbanCommentAnswered(commentAnswers);
            }

            // Salvar o comentário no banco de dados e obter o ID
            KanbanCardComment dbKanbanCardComment = kanbanCardCommentRepository.saveAndFlush(kanbanCardComment);
            int commentId = dbKanbanCardComment.getId();

            // Adicionar o ID ao objeto JSON
            JsonObject obj = new JsonObject();
            obj.addProperty("id", commentId);
            obj.add("answers", new JsonArray());

            // Adicionar o objeto ao array de IDs
            arr.add(obj);

            // Verificar se há respostas e chamar recursivamente
            if (comment.get("answers") != null && !comment.get("answers").getAsJsonArray().isEmpty()) {
                processComments(comment.get("answers").getAsJsonArray(), dbKanbanCard, kanbanUser, dbKanbanCardComment, obj.getAsJsonArray("answers"));
            }
        });

        return arr;
    }

    public JsonArray processInnerCards(JsonArray innerCards, KanbanColumn kanbanColumn,KanbanCard parentCard, JsonArray arr) {
        innerCards.forEach(innerCardElement->{
            JsonObject innerCard = innerCardElement.getAsJsonObject();
            KanbanCard kanbanCard = new KanbanCard();
            kanbanCard.setIndex(arr.size());
            kanbanCard.setKanbanColumn(kanbanColumn);
            kanbanCard.setKanbanInnerCard(parentCard);

            if(innerCard.get("title") == null){
                throw new Error("O campo title é necessário!");
            }
            kanbanCard.setTitle(innerCard.get("title").getAsString());

            if(innerCard.get("description") != null){
                kanbanCard.setDescription(innerCard.get("description").getAsString());
            }
            if(innerCard.get("deadline") != null){
                LocalDateTime deadline = LocalDateTime.parse(innerCard.get("deadline").getAsString());
                kanbanCard.setDeadline(deadline);
            }
            if(innerCard.get("members") != null){
                JsonArray membersId = innerCard.get("members").getAsJsonArray();
                if(!membersId.isEmpty()){
                    List<String> memberCurrencies = new ArrayList<>();

                    membersId.forEach(memberId->memberCurrencies.add(memberId.getAsString()));
                    List<String> memberParents = Arrays.stream(parentCard.getMembers().split(",")).toList();

                    if(memberCurrencies.stream().anyMatch(elemento -> !memberParents.contains(elemento))){
                        throw new Error("Membro não foi encontrado no card pai!");
                    }

                    kanbanCard.setMembers(String.join(",",memberCurrencies));
                }
            }

            // Salvar o comentário no banco de dados e obter o ID
            KanbanCard dbKanbanCard = kanbanCardRepository.saveAndFlush(kanbanCard);

            // Adicionar o ID ao objeto JSON
            JsonObject obj = new JsonObject();
            obj.addProperty("id", dbKanbanCard.getId());
            obj.add("tags", new JsonArray());
            obj.add("checklists", new JsonArray());
            obj.add("innerCards", new JsonArray());

            JsonElement cardtags = innerCard.get("tags");
            if(cardtags != null){
                if(!cardtags.getAsJsonArray().isEmpty()){
                    List<KanbanCardTag> tagsArr = new ArrayList<>();
                    cardtags.getAsJsonArray().forEach(tag->{
                        KanbanCardTag kanbanCardTag = new KanbanCardTag();
                        kanbanCardTag.setKanbanCard(dbKanbanCard);
                        if(tag.getAsJsonObject().get("name").getAsString() == null){
                            throw new Error("O campo name é necessário!");
                        }
                        if(tag.getAsJsonObject().get("color").getAsString() == null){
                            throw new Error("O campo color é necessário!");
                        }
                        kanbanCardTag.setName(tag.getAsJsonObject().get("name").getAsString());
                        kanbanCardTag.setColor(tag.getAsJsonObject().get("color").getAsString());
                        tagsArr.add(kanbanCardTag);
                    });

                    List<KanbanCardTag> dbKanbanCardTagList = kanbanCardTagRepository.saveAllAndFlush(tagsArr);
                    dbKanbanCardTagList.forEach(tag-> {
                        JsonObject formattedTag = new JsonObject();
                        formattedTag.addProperty("id",tag.getId());
                        obj.get("tags").getAsJsonArray().add(formattedTag);
                    });
                }
            }

            JsonElement cardChecklists = innerCard.get("checklists");
            if(cardChecklists != null){
                cardChecklists.getAsJsonArray().forEach(checklist->{
                    KanbanCardChecklist kanbanCardCheckList = new KanbanCardChecklist();
                    if(checklist.getAsJsonObject().get("name") == null){
                        throw new Error("O campo name é necessário no checklist!");
                    }
                    kanbanCardCheckList.setName(checklist.getAsJsonObject().get("name").getAsString());
                    kanbanCardCheckList.setKanbanCard(parentCard);

                    KanbanCardChecklist dbKanbanCardChecklist = kanbanCardCheckListRepository.saveAndFlush(kanbanCardCheckList);
                    JsonObject checkListObj = new JsonObject();
                    checkListObj.addProperty("id", dbKanbanCardChecklist.getId());
                    checkListObj.add("items",new JsonArray());

                    obj.get("checklists").getAsJsonArray().add(checkListObj);

                    if(checklist.getAsJsonObject().get("items") != null && !checklist.getAsJsonObject().get("items").getAsJsonArray().isEmpty()){
                        checklist.getAsJsonObject().get("items").getAsJsonArray().forEach(item->{
                            KanbanCardChecklistItem kanbanCardChecklistItem = new KanbanCardChecklistItem();
                            if(item.getAsJsonObject().get("name") == null){
                                throw  new Error("O campo name é necessário no checklist item!");
                            }
                            kanbanCardChecklistItem.setName(item.getAsJsonObject().get("name").getAsString());
                            kanbanCardChecklistItem.setCompleted(false);
                            kanbanCardChecklistItem.setKanbanChecklist(dbKanbanCardChecklist);

                            KanbanCardChecklistItem dbKanbanCheckListItemCard = kanbanCardCheckListItemRepository.saveAndFlush(kanbanCardChecklistItem);
                            JsonObject checkListItemObj = new JsonObject();
                            checkListItemObj.addProperty("id", dbKanbanCheckListItemCard.getId());
                            checkListObj.get("items").getAsJsonArray().add(checkListItemObj);

                            obj.get("checklists").getAsJsonArray().add(checkListObj);
                        });
                    }
                });
            }

            // Adicionar o objeto ao array de IDs
            arr.add(obj);

            // Verificar se há respostas e chamar recursivamente
            if (innerCard.get("innerCards") != null && !innerCard.get("innerCards").getAsJsonArray().isEmpty()) {
                processInnerCards(innerCard.get("innerCards").getAsJsonArray(), kanbanColumn,dbKanbanCard, obj.getAsJsonArray("innerCards"));
            }
        });

        return arr;
    }

    @DeleteMapping(path = "/private/user/kanban/column/card/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Integer cardId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard selectedCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = selectedCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(2) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        Integer columnId = selectedCard.getKanbanColumn().getId();
        List<KanbanCard> cardsList = kanbanCardRepository.findAllByColumnId(columnId);
        boolean isSearch = false;
        for(int i = 0;i < cardsList.size();i++){
            if(Objects.equals(cardsList.get(i).getId(), cardId)){
                isSearch = true;
            }else{
                if(isSearch){
                    cardsList.get(i).setIndex(i - 1);
                }else{
                    cardsList.get(i).setIndex(i);
                }
            }
        }

        List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(cardId);
        kanbanCardCheckList.forEach(checkList->{
            List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
            kanbanCardCheckListItemRepository.deleteAll(kanbanCardChecklistItems);
        });
        kanbanCardCheckListRepository.deleteAll(kanbanCardCheckList);

        List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(cardId);
        kanbanCardCommentRepository.deleteAll(kanbanCardCommentList);

        kanbanCardRepository.deleteById(cardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/{cardId}")
    public ResponseEntity<String> patchCard(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer cardId,
                                            @RequestParam(required = false,defaultValue = "false") boolean deleteDeadLine){
        JsonObject errorMessage = new JsonObject();

        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCard selectedCard = kanbanCardRepository.findById(cardId).get();

        Integer selectedKanbanId;
        Kanban kanban = selectedCard.getKanbanColumn().getKanban();

        JsonElement toColumnId = jsonObj.get("toColumnId");
        if(toColumnId == null){
            selectedKanbanId = kanban.getId();
        }else{
            boolean isColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
            if(!isColumn){
                errorMessage.addProperty("mensagem","Column não foi encontrado!");
                errorMessage.addProperty("status",434);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();
            selectedKanbanId = toColumn.getKanban().getId();
        }

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(selectedKanbanId,user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(3) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(!Objects.equals(selectedKanbanId, kanban.getId())){
            List<KanbanCard> toCardsList = kanbanCardRepository.findAllByColumnId(toColumnId.getAsInt());
            KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();

            selectedCard.setKanbanColumn(toColumn);
            selectedCard.setIndex(toCardsList.size());
        }

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle != null){
            selectedCard.setTitle(cardTitle.getAsString());
        }

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription != null){
            selectedCard.setDescription(cardDescription.getAsString());
        }

        JsonElement cardDeadline = jsonObj.get("deadline");
        if(cardDeadline != null){
            if(kanbanUser.getUser().getPermissionLevel().charAt(16) == '0'){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar prazo)!");
                errorMessage.addProperty("status",435);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
            LocalDateTime deadline = LocalDateTime.parse(cardDeadline.getAsString());
            selectedCard.setDeadline(deadline);
        }else{
            if(deleteDeadLine){
                if(kanbanUser.getUser().getPermissionLevel().charAt(15) == '0'){
                    errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar prazo)!");
                    errorMessage.addProperty("status",435);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
                }
                selectedCard.setDeadline(null);
            }
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers != null){
            String[] membersId = cardMembers.getAsString().split(",");
            if(!Objects.equals(cardMembers.getAsString(), "") && cardMembers.getAsString() != null){
                Integer kanbanId = kanban.getId();
                List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);
                for (String memberId : membersId) {
                    AtomicBoolean isFound = new AtomicBoolean(false);
                    kanbanUserList.forEach(kanUser ->{
                        if(kanUser.getUser().getId() == Integer.parseInt(memberId)){
                            isFound.set(true);
                        }
                    });
                    if(!isFound.get()){
                        errorMessage.addProperty("mensagem","Usuário não existente nesse kanban detectado!");
                        errorMessage.addProperty("status",433);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                    }
                }
            }
            selectedCard.setMembers(cardMembers.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/move/card")
    public ResponseEntity<String> moveCard(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard selectedCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = selectedCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(1) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement toColumnId = jsonObj.get("toColumnId");
        if(toColumnId == null){
            errorMessage.addProperty("mensagem","O campo toColumnId é necessário!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isToColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
        if(!isToColumn){
            errorMessage.addProperty("mensagem","Coluna de destino não foi encontrada!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn currentColumn = selectedCard.getKanbanColumn();
        KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();

        if(!Objects.equals(currentColumn.getKanban().getId(), toColumn.getKanban().getId())){
            errorMessage.addProperty("mensagem","As colunas não são do mesmo kanban!");
            errorMessage.addProperty("status",432);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement toIndex = jsonObj.get("toIndex");
        if(toIndex == null){
            errorMessage.addProperty("mensagem","O campo toIndex é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Integer currentColumnId = selectedCard.getKanbanColumn().getId();
        List<KanbanCard> toCardsList = kanbanCardRepository.findAllByColumnId(toColumnId.getAsInt());

        if(currentColumnId != toColumnId.getAsInt()){
            selectedCard.setKanbanColumn(toColumn);
            if(toIndex.getAsInt() >= toCardsList.size()){
                selectedCard.setIndex(toCardsList.size());
                toCardsList.add(selectedCard);
            }else{
                selectedCard.setIndex(toIndex.getAsInt());
                toCardsList.add(toIndex.getAsInt(),selectedCard);
                for(int i = 0;i < toCardsList.size();i++){
                    toCardsList.get(i).setIndex(i);
                }
            }
            List<KanbanCard> currentCardsList = kanbanCardRepository.findAllByColumnId(currentColumnId);
            if(!currentCardsList.isEmpty()){
                for(int i = 0;i < currentCardsList.size();i++){
                    currentCardsList.get(i).setIndex(i);
                }
            }
        }else{
            if(toIndex.getAsInt() >= toCardsList.size()){
                selectedCard.setIndex(toCardsList.size() - 1);
            }else{
                selectedCard.setIndex(toIndex.getAsInt());
            }
            toCardsList.sort(Comparator.comparing(KanbanCard::getIndex));
            for (int i = 0;i < toCardsList.size();i++) {
                if(Objects.equals(toCardsList.get(i).getId(), selectedCard.getId()) && Objects.equals(toCardsList.get(i + 1).getIndex(), selectedCard.getIndex())){
                    toCardsList.get(i).setIndex(i + 1);
                    toCardsList.get(i + 1).setIndex(i);
                    i+=2;
                    if(i >= toCardsList.size()){
                        break;
                    }
                }else if(Objects.equals(toCardsList.get(i).getId(), selectedCard.getId()) && toCardsList.get(i - 1).getIndex() == toIndex.getAsInt()){
                    toCardsList.get(i).setIndex(i - 1);
                    toCardsList.get(i - 1).setIndex(i);
                    i+=1;
                }{
                    toCardsList.get(i).setIndex(i);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
