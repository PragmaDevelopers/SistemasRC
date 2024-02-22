package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.ClientTemplateRepository;
import com.api.sistema_rc.repository.KanbanNotificationRepository;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.service.MailService;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping(path = "/api")
public class ClientTemplateController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientTemplateRepository clientTemplateRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailService mailService;
    @Autowired
    private Gson gson;
    ExecutorService executorService = Executors.newCachedThreadPool();
    @GetMapping(path = "/private/user/signup/client/templates")
    public ResponseEntity<String> getClientTemplates(@RequestParam(required = false,defaultValue = "") String name,
                                                    @RequestParam(required = false,defaultValue = "false") boolean value){
        List<ClientTemplate> clientTemplateList;
        if(Objects.equals(name, "")){
            clientTemplateList = clientTemplateRepository.findAllByValue(value);
        }else{
            clientTemplateList = clientTemplateRepository.findAllByNameAndValue(name,value);
        }

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
    public ResponseEntity<String> postClientTemplate(@RequestBody String body,@RequestHeader("Authorization") String token,
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

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();
        if(user.getPermissionLevel().charAt(35) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        ClientTemplate clientTemplate = new ClientTemplate();
        clientTemplate.setName(name.getAsString());
        clientTemplate.setTemplate(template.getAsJsonObject().toString());
        clientTemplate.setValue(value);

        ClientTemplate dbClientTemplate = clientTemplateRepository.saveAndFlush(clientTemplate);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(user);
            kanbanNotification.setSenderUser(user);

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);

            String wordTemplate;
            if(value){
                wordTemplate = "template cliente ";
            }else{
                wordTemplate = "template formulário ";
            }
            kanbanNotification.setMessage(
                    "Você criou o "+wordTemplate+dbClientTemplate.getName()+"."
            );
            mailService.sendMail(user.getEmail(),"Criação do "+wordTemplate+dbClientTemplate.getName(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(37);
            kanbanCategory.setName(CategoryName.CLIENTTEMPLATE_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setClientTemplate(dbClientTemplate);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            String finalWordTemplate = wordTemplate;
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user.getId())){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            user.getName()+" criou o "+finalWordTemplate+dbClientTemplate.getName()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Criação do "+finalWordTemplate+dbClientTemplate.getName(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

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

        ClientTemplate selectedClientTemplate = clientTemplateRepository.findById(clientTemplateId).get();

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();
        if(user.getPermissionLevel().charAt(36) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(user);
            kanbanNotification.setSenderUser(user);

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);

            String wordTemplate;
            if(selectedClientTemplate.isValue()){
                wordTemplate = "template cliente ";
            }else{
                wordTemplate = "template formulário ";
            }
            kanbanNotification.setMessage(
                    "Você deletou o "+wordTemplate+selectedClientTemplate.getName()+"."
            );
            mailService.sendMail(user.getEmail(),"Deletando "+wordTemplate+selectedClientTemplate.getName(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(38);
            kanbanCategory.setName(CategoryName.CLIENTTEMPLATE_DELETE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            String finalWordTemplate = wordTemplate;
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user.getId())){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            user.getName()+" deletou o "+finalWordTemplate+selectedClientTemplate.getName()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Deletando "+finalWordTemplate+selectedClientTemplate.getName(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);

            clientTemplateRepository.deleteById(clientTemplateId);
        });
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
