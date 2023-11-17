package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.Kanban;
import com.api.sistema_rc.model.KanbanCards;
import com.api.sistema_rc.model.KanbanColumns;
import com.api.sistema_rc.repository.KanbanCardsRepository;
import com.api.sistema_rc.repository.KanbanColumnsRepository;
import com.api.sistema_rc.repository.KanbanRepository;
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
public class KanbanCardsController {
    @Autowired
    private KanbanColumnsRepository kanbanColumnsRepository;
    @Autowired
    private KanbanCardsRepository kanbanCardsRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/{column_id}/cards")
    public ResponseEntity<String> getCards(@PathVariable Integer column_id){
        List<KanbanCards> kanbanCards = kanbanCardsRepository.findAllByColumnId(column_id);

        JsonArray cardsArr = new JsonArray();

        kanbanCards.forEach(card->{
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id",card.getId());
            cardObj.addProperty("title",card.getTitle());
            cardObj.addProperty("description",card.getDescription());
            cardObj.addProperty("tags",card.getTags());
            cardObj.addProperty("members",card.getMembers());
            cardsArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(cardsArr.toString());
    }
    @PostMapping(path = "/private/user/kanban/column/card")
    public ResponseEntity<String> postCard(@RequestBody String body){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription == null){
            errorMessage.addProperty("mensagem","O campo description é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardTags = jsonObj.get("tags");
        if(cardTags == null){
            errorMessage.addProperty("mensagem","O campo tags é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers == null){
            errorMessage.addProperty("mensagem","O campo members é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement columnId = jsonObj.get("columnId");
        if(columnId == null){
            errorMessage.addProperty("mensagem","O columnId members é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanColumnsRepository.findById(columnId.getAsInt()).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        KanbanColumns kanbanColumns = kanbanColumnsRepository.findById(columnId.getAsInt()).get();

        KanbanCards kanbanCard = new KanbanCards();
        kanbanCard.setTitle(cardTitle.getAsString());
        kanbanCard.setKanbanColumns(kanbanColumns);
        kanbanCard.setDescription(cardDescription.getAsString());
        kanbanCard.setTags(cardTags.getAsString());
        kanbanCard.setMembers(cardMembers.getAsString());

        KanbanCards dbKanbanCard = kanbanCardsRepository.saveAndFlush(kanbanCard);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCard.toJson(new JsonObject()));
    }

    @Transactional
    @PutMapping(path = "/private/user/kanban/column/move/card")
    public ResponseEntity<String> moveCard(@RequestBody String body){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement oldColumnId = jsonObj.get("oldColumnId");
        if(oldColumnId == null){
            errorMessage.addProperty("mensagem","O campo oldColumnId é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isOldColumn = kanbanCardsRepository.findByIdAndColumnId(cardId.getAsInt(),oldColumnId.getAsInt()).isEmpty();
        if(isOldColumn){
            errorMessage.addProperty("mensagem","Antiga coluna não tem o card selecionado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement newColumnId = jsonObj.get("newColumnId");
        if(newColumnId == null){
            errorMessage.addProperty("mensagem","O campo newColumnId é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isNewColumn = kanbanColumnsRepository.findById(newColumnId.getAsInt()).isPresent();
        if(!isNewColumn){
            errorMessage.addProperty("mensagem","Nova coluna não foi encontrada!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        kanbanCardsRepository.updateColumn(cardId.getAsInt(),oldColumnId.getAsInt(),newColumnId.getAsInt());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
