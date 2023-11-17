package com.api.sistema_rc.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import ch.qos.logback.core.model.Model;
import com.api.sistema_rc.model.Kanban;
import com.api.sistema_rc.model.KanbanCards;
import com.api.sistema_rc.model.KanbanColumns;
import com.api.sistema_rc.model.KanbanUser;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class KanbanColumnsController {
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private KanbanColumnsRepository kanbanColumnsRepository;
    @Autowired
    private KanbanCardsRepository kanbanCardsRepository;
    private final Gson gson = new Gson();

    @GetMapping(path = "/private/user/kanban/{kanban_id}/columns")
    public ResponseEntity<String> getColumns(@PathVariable Integer kanban_id,@RequestParam(name = "cards",required = false) boolean isCards){
        List<KanbanColumns> kanbanColumns = kanbanColumnsRepository.findAllByKanbanId(kanban_id);

        JsonArray columnsArr = new JsonArray();

        kanbanColumns.forEach(column->{
            JsonObject columnObj = new JsonObject();
            columnObj.addProperty("id",column.getId());
            columnObj.addProperty("title",column.getTitle());

            if(isCards){
                List<KanbanCards> kanbanCardsList = kanbanCardsRepository.findAllByColumnId(column.getId());
                JsonArray cardsArr = new JsonArray();
                for(KanbanCards card : kanbanCardsList){
                    JsonObject cardObj = new JsonObject();
                    cardObj.addProperty("id",card.getId());
                    cardObj.addProperty("title",card.getTitle());
                    cardObj.addProperty("tags",card.getTags());
                    cardObj.addProperty("members",card.getMembers());
                    cardObj.addProperty("description",card.getDescription());
                    cardsArr.add(cardObj);
                }
                columnObj.add("cards",cardsArr);
            }

            columnsArr.add(columnObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(columnsArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column")
    public ResponseEntity<String> postColumn(@RequestBody String body){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement columnTitle = kanbanJson.get("title");
        if(columnTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement kanbanId = kanbanJson.get("kanbanId");
        if(kanbanId == null){
            errorMessage.addProperty("mensagem","O campo kanbanId é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isKanban = kanbanRepository.findById(kanbanId.getAsInt()).isPresent();
        if(!isKanban){
            errorMessage.addProperty("mensagem","Kanban não foi encontrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = kanbanRepository.findById(kanbanId.getAsInt()).get();

        KanbanColumns kanbanColumn = new KanbanColumns();
        kanbanColumn.setTitle(columnTitle.getAsString());
        kanbanColumn.setKanban(kanban);

        KanbanColumns dbKanbanColumn = kanbanColumnsRepository.saveAndFlush(kanbanColumn);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanColumn.toJson(new JsonObject()));
    }
}
