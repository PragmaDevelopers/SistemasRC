package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.KanbanCardRepository;
import com.api.sistema_rc.repository.KanbanCardChecklistItemRepository;
import com.api.sistema_rc.repository.KanbanCardChecklistRepository;
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
public class KanbanChecklistItemController {
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
    @GetMapping(path = "/private/user/kanban/column/card/checklist/{checklistId}/checklistItems")
    public ResponseEntity<String> getChecklistItem(@PathVariable Integer checklistId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (checklistId == null) {
            errorMessage.addProperty("mensagem", "O campo checklistId é necessário!");
            errorMessage.addProperty("status", 450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCheckList = kanbanCardChecklistRepository.findById(checklistId).isPresent();
        if (!isCheckList) {
            errorMessage.addProperty("mensagem", "CheckList não foi encontrado!");
            errorMessage.addProperty("status", 454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardCheckList = kanbanCardChecklistRepository.findById(checklistId).get();

        Kanban kanban = kanbanCardCheckList.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardChecklistItemRepository.findAllByChecklistId(checklistId);

        JsonArray checklistItemArr = new JsonArray();

        kanbanCardChecklistItems.forEach(checkListItem -> {
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id", checkListItem.getId());
            cardObj.addProperty("name", checkListItem.getName());
            cardObj.addProperty("completed", checkListItem.isCompleted());
            checklistItemArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(checklistItemArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/checklist/checklistItem")
    public ResponseEntity<String> postChecklistItem(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement checklistId = jsonObj.get("checklistId");
        if(checklistId == null){
            errorMessage.addProperty("mensagem","O campo checklistId é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).isPresent();
        if(!isChecklist){
            errorMessage.addProperty("mensagem","Checklist não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklist kanbanCardChecklist = kanbanCardChecklistRepository.findById(checklistId.getAsInt()).get();

        Kanban kanban = kanbanCardChecklist.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(11) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",450);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanCardChecklistItem kanbanCardChecklistItem = new KanbanCardChecklistItem();

        kanbanCardChecklistItem.setName(checklistName.getAsString());
        kanbanCardChecklistItem.setCompleted(false);
        kanbanCardChecklistItem.setKanbanChecklist(kanbanCardChecklist);

        KanbanCardChecklistItem dbKanbanCardChecklistItem = kanbanCardChecklistItemRepository.saveAndFlush(kanbanCardChecklistItem);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardChecklistItem.getId().toString());
    }
    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/checklist/checklistItem/{checklistItemId}")
    public ResponseEntity<String> patchChecklistItem(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer checklistItemId){
        JsonObject errorMessage = new JsonObject();

        boolean isChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).isPresent();
        if(!isChecklistItem){
            errorMessage.addProperty("mensagem","ChecklistItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardChecklistItem selectedChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).get();

        Kanban kanban = selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(13) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement checklistName = jsonObj.get("name");
        if(checklistName != null){
            selectedChecklistItem.setName(checklistName.getAsString());
        }

        JsonElement checklistCompleted = jsonObj.get("completed");
        if(checklistCompleted != null){
            selectedChecklistItem.setCompleted(checklistCompleted.getAsBoolean());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping(path = "/private/user/kanban/column/card/checklist/checklistItem/{checklistItemId}")
    public ResponseEntity<String> deleteChecklistItem(@PathVariable Integer checklistItemId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).isPresent();
        if(!isChecklistItem){
            errorMessage.addProperty("mensagem","ChecklistItem não foi encontrado!");
            errorMessage.addProperty("status",454);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardChecklistItem selectedChecklistItem = kanbanCardChecklistItemRepository.findById(checklistItemId).get();

        Kanban kanban = selectedChecklistItem.getKanbanChecklist().getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",451);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(12) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",455);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        kanbanCardChecklistItemRepository.deleteById(checklistItemId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
