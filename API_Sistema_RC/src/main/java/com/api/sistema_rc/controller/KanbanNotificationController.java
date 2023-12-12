package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class KanbanNotificationController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardChecklistRepository kanbanCardCheckListRepository;
    @Autowired
    private KanbanCardChecklistItemRepository kanbanCardCheckListItemRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/notifications")
    public ResponseEntity<String> getNotifications(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);

        List<KanbanNotification> kanbanNotificationList = kanbanNotificationRepository.findAllByUserId(user_id);

        JsonArray notificationArr = new JsonArray();
        kanbanNotificationList.forEach(notification->{
            JsonObject notificationObj = new JsonObject();
            notificationObj.addProperty("id",notification.getId());
            notificationObj.addProperty("type",notification.getType());
            notificationObj.add("aux", JsonParser.parseString(notification.getAux()));
            notificationObj.addProperty("message",notification.getMessage());
            notificationObj.addProperty("viewed",notification.isViewed());

            notificationArr.add(notificationObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(notificationArr.toString());
    }
}
