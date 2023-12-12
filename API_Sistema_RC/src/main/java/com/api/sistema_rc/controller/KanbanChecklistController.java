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

@RestController
@RequestMapping(path = "/api")
public class KanbanChecklistController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardChecklistRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardChecklistItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/checklists")
    public ResponseEntity<String> getChecklists(@PathVariable Integer cardId, @RequestHeader("Authorization") String token) {
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

        List<KanbanCardChecklist> kanbanCardChecklist = kanbanCardChecklistRepository.findAllByCardId(cardId);

        JsonArray checklistArr = new JsonArray();

        kanbanCardChecklist.forEach(checklist -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checklist.getId());
            cardObj.addProperty("name", checklist.getName());
            checklistArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checklistArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checklist")
    public ResponseEntity<String> postChecklist(@RequestBody String body,@RequestHeader("Authorization") String token){
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

        if(kanbanUser.getUser().getPermissionLevel().charAt(11) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",440);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardChecklist = new KanbanCardChecklist();

        kanbanCardChecklist.setName(checklistName.getAsString());
        kanbanCardChecklist.setKanbanCard(kanbanCard);

        KanbanCardChecklist dbKanbanCardChecklist = kanbanCardChecklistRepository.saveAndFlush(kanbanCardChecklist);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardChecklist.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}")
    public ResponseEntity<String> patchCheckList(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checklistId){
        JsonObject errorMessage = new JsonObject();

        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","CheckList não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardChecklist selectedCheckList = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = selectedCheckList.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(13) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName != null){
            selectedCheckList.setName(checklistName.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}")
    public ResponseEntity<String> deleteCheckList(@PathVariable Integer checklistId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if(!isCheckList){
            errorMessage.addProperty("mensagem","Checklist não foi encontrado!");
            errorMessage.addProperty("status",444);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist selectedChecklist = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = selectedChecklist.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",441);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(12) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",445);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardChecklistItemRepository.findAllByChecklistId(checklistId);
        kanbanCardChecklistItemRepository.deleteAll(kanbanCardChecklistItems);

        kanbanCardChecklistRepository.deleteById(checklistId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
