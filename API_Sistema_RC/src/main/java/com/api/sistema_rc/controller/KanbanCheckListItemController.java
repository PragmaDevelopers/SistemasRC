package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.KanbanCardRepository;
import com.api.sistema_rc.repository.KanbanCheckListItemRepository;
import com.api.sistema_rc.repository.KanbanCheckListRepository;
import com.api.sistema_rc.repository.KanbanUserRepository;
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

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class KanbanCheckListItemController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCheckListRepository kanbanCheckListRepository;
    @Autowired
    private KanbanCheckListItemRepository kanbanCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/card/checkList/{checkListId}/checkListItems")
    public ResponseEntity<String> getCheckListItem(@PathVariable Integer checkListId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (checkListId == null) {
            errorMessage.addProperty("mensagem", "O campo checkListId é necessário!");
            errorMessage.addProperty("status", 450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCheckList = kanbanCheckListRepository.findById(checkListId).isPresent();
        if (!isCheckList) {
            errorMessage.addProperty("mensagem", "CheckList não foi encontrado!");
            errorMessage.addProperty("status", 454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCheckList kanbanCheckList = kanbanCheckListRepository.findById(checkListId).get();

        Kanban kanban = kanbanCheckList.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if (kanbanUser.getPermissionLevel().charAt(0) == '0') {
            errorMessage.addProperty("mensagem", "Você não tem autorização para essa ação!");
            errorMessage.addProperty("status", 455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkListId);

        JsonArray checkListItemArr = new JsonArray();

        kanbanCheckListItems.forEach(checkListItem -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checkListItem.getId());
            cardObj.addProperty("name", checkListItem.getName());
            cardObj.addProperty("completed", checkListItem.isCompleted());
            checkListItemArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checkListItemArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checkList/checkListItem")
    public ResponseEntity<String> postCheckListItem(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement checkListId = jsonObj.get("checkListId");
        if(checkListId == null){
            errorMessage.addProperty("mensagem","O campo checkListId é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCheckList = kanbanCheckListRepository.findById(checkListId.getAsInt()).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","CheckList não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCheckList kanbanCheckList = kanbanCheckListRepository.findById(checkListId.getAsInt()).get();

        Kanban kanban = kanbanCheckList.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(1) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checkListName = jsonObj.get("name");
        if(checkListName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCheckListItem kanbanCheckListItem = new KanbanCheckListItem();

        kanbanCheckListItem.setName(checkListName.getAsString());
        kanbanCheckListItem.setCompleted(false);
        kanbanCheckListItem.setKanbanCheckList(kanbanCheckList);

        KanbanCheckListItem dbKanbanCheckListItem = kanbanCheckListItemRepository.saveAndFlush(kanbanCheckListItem);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCheckListItem.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checkList/checkListItem/{checkListItemId}")
    public ResponseEntity<String> patchCheckListItem(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checkListItemId){
        JsonObject errorMessage = new JsonObject();

        boolean isCheckListItem = kanbanCheckListItemRepository.findById(checkListItemId).isPresent();
        if(!isCheckListItem){
            errorMessage.addProperty("mensagem","CheckListItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCheckListItem selectedCheckListItem = kanbanCheckListItemRepository.findById(checkListItemId).get();

        Kanban kanban = selectedCheckListItem.getKanbanCheckList().getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(2) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checkListName = jsonObj.get("name");
        if(checkListName != null){
            selectedCheckListItem.setName(checkListName.getAsString());
        }

        JsonElement checkListCompleted = jsonObj.get("completed");
        if(checkListCompleted != null){
            selectedCheckListItem.setCompleted(checkListCompleted.getAsBoolean());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checkList/checkListItem/{checkListItemId}")
    public ResponseEntity<String> deleteCheckListItem(@PathVariable Integer checkListItemId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCheckListItem = kanbanCheckListItemRepository.findById(checkListItemId).isPresent();
        if(!isCheckListItem){
            errorMessage.addProperty("mensagem","CheckListItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCheckListItem selectedCheckListItem = kanbanCheckListItemRepository.findById(checkListItemId).get();

        Kanban kanban = selectedCheckListItem.getKanbanCheckList().getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(4) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        kanbanCheckListItemRepository.deleteById(checkListItemId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
