package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.ClientTemplateRepository;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ClientTemplateController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientTemplateRepository clientTemplateRepository;
    @Autowired
    private TokenService tokenService;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/signup/client/templates")
    public ResponseEntity<String> getClientTemplates(@RequestHeader("Authorization") String token,
                                                    @RequestParam(required = false,defaultValue = "false") boolean value){
        List<ClientTemplate> clientTemplateList = clientTemplateRepository.findAllByValue(value);

        JsonArray clientTemplateArr = new JsonArray();
        clientTemplateList.forEach(clientTemplate -> {
            JsonObject clientTemplateObj = new JsonObject();
            clientTemplateObj.addProperty("id",clientTemplate.getId());
            clientTemplateObj.addProperty("name",clientTemplate.getName());
            JsonElement template = JsonParser.parseString(clientTemplate.getTemplate());
            clientTemplateObj.add("template",template);
            clientTemplateArr.add(clientTemplateObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(clientTemplateArr.toString());
    }

    @PostMapping(path = "/private/user/signup/client/template")
    public ResponseEntity<String> postClientTemplate(@RequestBody String body,
                                                    @RequestParam(required = false,defaultValue = "false") boolean value){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement name = jsonObj.get("name");
        if(name == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement template = jsonObj.get("template");
        if(template == null){
            errorMessage.addProperty("mensagem","O campo template é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        ClientTemplate clientTemplate = new ClientTemplate();
        clientTemplate.setName(name.getAsString());
        clientTemplate.setTemplate(template.getAsJsonObject().toString());
        clientTemplate.setValue(value);

        ClientTemplate dbClientTemplate = clientTemplateRepository.saveAndFlush(clientTemplate);

        return ResponseEntity.status(HttpStatus.OK).body(dbClientTemplate.getId().toString());
    }

    @DeleteMapping(path = "/private/user/signup/client/template/{clientTemplateId}")
    public ResponseEntity<String> deleteClientTemplate(@PathVariable Integer clientTemplateId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        if(clientTemplateId == 1){
            errorMessage.addProperty("mensagem","Esse clientTemplate é fixo, não pode ser deletado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        boolean isClientTemplate = clientTemplateRepository.findById(clientTemplateId).isPresent();
        if(!isClientTemplate){
            errorMessage.addProperty("mensagem","ClientTemplate não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        Integer user_id = tokenService.validateToken(token);

        User user = userRepository.findById(user_id).get();

        if(user.getRole().getName() != RoleName.ROLE_ADMIN && user.getRole().getName() != RoleName.ROLE_SUPERVISOR){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar template de cliente)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        clientTemplateRepository.deleteById(clientTemplateId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
