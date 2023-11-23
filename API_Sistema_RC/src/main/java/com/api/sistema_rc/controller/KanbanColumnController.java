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
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanCheckListRepository kanbanCheckListRepository;
    @Autowired
    private KanbanCheckListItemRepository kanbanCheckListItemRepository;
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

        if(kanbanUser.getPermissionLevel().charAt(0) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
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
                List<KanbanCard> kanbanCardsList = kanbanCardRepository.findAllByColumnId(column.getId());
                JsonArray cardsArr = new JsonArray();
                for(KanbanCard card : kanbanCardsList){
                    JsonObject cardObj = new JsonObject();
                    cardObj.addProperty("id",card.getId());
                    cardObj.addProperty("title",card.getTitle());
                    cardObj.addProperty("tags",card.getTags());
                    cardObj.addProperty("members",card.getMembers());
                    cardObj.addProperty("description",card.getDescription());
                    cardObj.addProperty("index",card.getIndex());

                    List<KanbanCheckList> kanbanCheckList = kanbanCheckListRepository.findAllByCardId(card.getId());
                    JsonArray checkListArr = new JsonArray();
                    for (KanbanCheckList checkList : kanbanCheckList) {
                        JsonObject checkListObj = new JsonObject();
                        checkListObj.addProperty("id",checkList.getId());
                        checkListObj.addProperty("name",checkList.getName());

                        List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkList.getId());
                        JsonArray checkListItemArr = new JsonArray();
                        for (KanbanCheckListItem kanbanCheckListItem : kanbanCheckListItems) {
                            JsonObject checkListItemObj = new JsonObject();
                            checkListItemObj.addProperty("id",kanbanCheckListItem.getId());
                            checkListItemObj.addProperty("name",kanbanCheckListItem.getName());
                            checkListItemObj.addProperty("completed",kanbanCheckListItem.isCompleted());
                            checkListItemArr.add(checkListItemObj);
                        }

                        checkListObj.add("items",checkListItemArr);
                        checkListArr.add(checkListObj);
                    }

                    cardObj.add("checkList",checkListArr);
                    cardsArr.add(cardObj);
                }
                columnObj.add("cards",cardsArr);
            }

            columnsArr.add(columnObj);
        });
        return ResponseEntity.status(HttpStatus.OK).body(columnsArr.toString());
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

        if(kanbanUser.getPermissionLevel().charAt(1) == '0'){
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

        if(kanbanUser.getPermissionLevel().charAt(4) == '0'){
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
            List<KanbanCheckList> kanbanCheckList = kanbanCheckListRepository.findAllByCardId(cardId.getId());
            kanbanCheckList.forEach(checkList->{
                List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkList.getId());
                kanbanCheckListItemRepository.deleteAll(kanbanCheckListItems);
            });
            kanbanCheckListRepository.deleteAll(kanbanCheckList);
        });
        kanbanCardRepository.deleteAll(kanbanCardList);

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

        if(kanbanUser.getPermissionLevel().charAt(2) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement columnTitle = jsonObj.get("title");
        if(columnTitle != null){
            selectedColumn.setTitle(columnTitle.getAsString());
        }

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

        if(kanbanUser.getPermissionLevel().charAt(3) == '0'){
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

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
