package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.Kanban;
import com.api.sistema_rc.model.KanbanColumns;
import com.api.sistema_rc.model.KanbanUser;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class KanbanController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanRepository kanbanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    private final Gson gson = new Gson();

    @GetMapping(path = "/private/user/kanban")
    public ResponseEntity<List<Kanban>> getKanban(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);
        List<KanbanUser> kanbanUsers = kanbanUserRepository.findAllByUserId(user_id);

        List<Kanban> kanbanList;

        kanbanList = kanbanUsers.stream()
                .map(KanbanUser::getKanban)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(kanbanList);
    }

    @PostMapping(path = "/private/user/kanban")
    public ResponseEntity<String> postKanban(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject kanbanJson = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        String kanbanTitle = kanbanJson.get("title").getAsString();
        if(kanbanTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Kanban kanban = new Kanban();
        kanban.setTitle(kanbanTitle);
        Kanban dbKanban = kanbanRepository.saveAndFlush(kanban);

        Integer user_id = tokenService.validateToken(token);

        boolean isUser = userRepository.findById(user_id).isPresent();
        if(!isUser){
            errorMessage.addProperty("mensagem","Usuário não encontrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        User user = userRepository.findById(user_id).get();

        KanbanUser kanbanUser = new KanbanUser();
        kanbanUser.setKanban(dbKanban);
        kanbanUser.setUser(user);
        kanbanUser.setPermissionLevel("0000000000000000000000000");

        kanbanUserRepository.save(kanbanUser);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanban.toJson(new JsonObject()));
    }
}
