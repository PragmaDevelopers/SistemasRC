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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    private KanbanCheckListRepository kanbanCheckListRepository;
    @Autowired
    private KanbanCheckListItemRepository kanbanCheckListItemRepository;
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

        KanbanColumn kanbanColumns = kanbanColumnRepository.findById(columnId).get();

        Kanban kanban = kanbanColumns.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(0) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCards = kanbanCardRepository.findAllByColumnId(columnId);

        JsonArray cardsArr = new JsonArray();

        kanbanCards.forEach(card->{
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id",card.getId());
            cardObj.addProperty("title",card.getTitle());
            cardObj.addProperty("description",card.getDescription());
            cardObj.addProperty("tags",card.getTags());
            cardObj.addProperty("members",card.getMembers());
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

        if(kanbanUser.getPermissionLevel().charAt(1) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription == null){
            errorMessage.addProperty("mensagem","O campo description é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardTags = jsonObj.get("tags");
        if(cardTags == null){
            errorMessage.addProperty("mensagem","O campo tags é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers == null){
            errorMessage.addProperty("mensagem","O campo members é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId.getAsInt());

        KanbanCard kanbanCard = new KanbanCard();

        kanbanCard.setIndex(kanbanCardList.size());

        kanbanCard.setTitle(cardTitle.getAsString());
        kanbanCard.setKanbanColumn(kanbanColumn);
        kanbanCard.setDescription(cardDescription.getAsString());
        kanbanCard.setTags(cardTags.getAsString());

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

        kanbanCard.setMembers(cardMembers.getAsString());

        KanbanCard dbKanbanCard = kanbanCardRepository.saveAndFlush(kanbanCard);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCard.getId().toString());
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

        if(kanbanUser.getPermissionLevel().charAt(4) == '0'){
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

        List<KanbanCheckList> kanbanCheckList = kanbanCheckListRepository.findAllByCardId(cardId);
        kanbanCheckList.forEach(checkList->{
            List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkList.getId());
            kanbanCheckListItemRepository.deleteAll(kanbanCheckListItems);
        });
        kanbanCheckListRepository.deleteAll(kanbanCheckList);

        kanbanCardRepository.deleteById(cardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/{cardId}")
    public ResponseEntity<String> patchCard(@RequestBody String body,@RequestHeader("Authorization") String token,
                                          @PathVariable Integer cardId){
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

        if(kanbanUser.getPermissionLevel().charAt(2) == '0'){
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

        JsonElement cardTags = jsonObj.get("tags");
        if(cardTags != null){
            selectedCard.setTags(cardTags.getAsString());
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

        if(kanbanUser.getPermissionLevel().charAt(3) == '0'){
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
