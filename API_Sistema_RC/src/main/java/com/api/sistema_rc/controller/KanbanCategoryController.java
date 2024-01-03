package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class KanbanCategoryController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCategoryRepository kanbanCategoryRepository;
    @Autowired
    private KanbanCardCommentRepository kanbanCardCommentRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardCheckListRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private UserRepository userRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/categories")
    public ResponseEntity<String> getCategories() {
        List<KanbanCategory> kanbanCategory = kanbanCategoryRepository.findAll();

        JsonArray categoryArr = new JsonArray();

        kanbanCategory.forEach(category -> {
            if(category.getName().name().equals(CategoryName.CARD_MOVE.name())){
                JsonObject tagObj = new JsonObject();
                tagObj.addProperty("id", category.getId());
                tagObj.addProperty("name", category.getName().name());
                categoryArr.add(tagObj);
            }
        });

        return ResponseEntity.status(HttpStatus.OK).body(categoryArr.toString());
    }
}
