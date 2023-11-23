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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(path = "/api")
public class KanbanCheckListController {
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
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/checkList")
    public ResponseEntity<String> getCheckList(@PathVariable Integer cardId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (cardId == null) {
            errorMessage.addProperty("mensagem", "O campo cardId é necessário!");
            errorMessage.addProperty("status", 440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if (!isCard) {
            errorMessage.addProperty("mensagem", "Card não foi encontrado!");
            errorMessage.addProperty("status", 444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if (kanbanUser.getPermissionLevel().charAt(0) == '0') {
            errorMessage.addProperty("mensagem", "Você não tem autorização para essa ação!");
            errorMessage.addProperty("status", 445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCheckList> kanbanCheckList = kanbanCheckListRepository.findAllByCardId(cardId);

        JsonArray checkListArr = new JsonArray();

        kanbanCheckList.forEach(checkList -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checkList.getId());
            cardObj.addProperty("name", checkList.getName());
            checkListArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checkListArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checkList")
    public ResponseEntity<String> postCheckList(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(1) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checkListName = jsonObj.get("name");
        if(checkListName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCheckList kanbanCheckList = new KanbanCheckList();

        kanbanCheckList.setName(checkListName.getAsString());
        kanbanCheckList.setKanbanCard(kanbanCard);

        KanbanCheckList dbKanbanCheckList = kanbanCheckListRepository.saveAndFlush(kanbanCheckList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCheckList.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checkList/{checkListId}")
    public ResponseEntity<String> patchCheckList(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checkListId){
        JsonObject errorMessage = new JsonObject();

        boolean isCheckList = kanbanCheckListRepository.findById(checkListId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","CheckList não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCheckList selectedCheckList = kanbanCheckListRepository.findById(checkListId).get();

        Kanban kanban = selectedCheckList.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(2) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checkListName = jsonObj.get("name");
        if(checkListName != null){
            selectedCheckList.setName(checkListName.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checkList/{checkListId}")
    public ResponseEntity<String> deleteCheckList(@PathVariable Integer checkListId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCheckList = kanbanCheckListRepository.findById(checkListId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","CheckList não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCheckList selectedCheckList = kanbanCheckListRepository.findById(checkListId).get();

        Kanban kanban = selectedCheckList.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getPermissionLevel().charAt(4) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCheckListItem> kanbanCheckListItems = kanbanCheckListItemRepository.findAllByCheckListId(checkListId);
        kanbanCheckListItemRepository.deleteAll(kanbanCheckListItems);

        kanbanCheckListRepository.deleteById(checkListId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
