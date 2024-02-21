package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.KanbanNotificationRepository;
import com.api.sistema_rc.repository.PdfTemplateRepository;
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
public class PdfEditorTemplateController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PdfTemplateRepository pdfTemplateRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailService mailService;
    @Autowired
    private Gson gson;
    ExecutorService executorService = Executors.newCachedThreadPool();
    @GetMapping(path = "/private/user/signup/pdfEditor/templates")
    public ResponseEntity<String> getPdfEditorTemplates(@RequestHeader("Authorization") String token,
                                                        @RequestParam(required = false,defaultValue = "") String search){
        List<PdfTemplate> pdfTemplateList;
        if(Objects.equals(search, "")){
            pdfTemplateList = pdfTemplateRepository.findAll();
        }else{
            pdfTemplateList = pdfTemplateRepository.findAllByName(search);
        }

        JsonArray pdfEditorTemplateArr = new JsonArray();
        pdfTemplateList.forEach(pdfTemplate -> {
            JsonObject pdfEditorTemplateObj = new JsonObject();
            pdfEditorTemplateObj.addProperty("id", pdfTemplate.getId());
            pdfEditorTemplateObj.addProperty("name", pdfTemplate.getName());
            JsonElement template = JsonParser.parseString(pdfTemplate.getTemplate());
            pdfEditorTemplateObj.add("template",template);
            pdfEditorTemplateArr.add(pdfEditorTemplateObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(pdfEditorTemplateArr.toString());
    }

    @PostMapping(path = "/private/user/signup/pdfEditor/template")
    public ResponseEntity<String> postPdfEditorTemplate(@RequestBody String body,@RequestHeader("Authorization") String token){
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
        if(user.getPermissionLevel().charAt(37) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        PdfTemplate pdfTemplate = new PdfTemplate();
        pdfTemplate.setName(name.getAsString());
        pdfTemplate.setTemplate(template.getAsJsonArray().toString());

        PdfTemplate dbPdfTemplate = pdfTemplateRepository.saveAndFlush(pdfTemplate);

        executorService.submit(() -> {
            List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

            KanbanNotification kanbanNotification = new KanbanNotification();

            kanbanNotification.setUser(user);
            kanbanNotification.setSenderUser(user);

            kanbanNotification.setRegistrationDate(LocalDateTime.now());
            kanbanNotification.setViewed(false);

            kanbanNotification.setMessage(
                    "Você criou o template PDF "+ dbPdfTemplate.getName()+"."
            );
            mailService.sendMail(user.getEmail(),"Criação do template PDF "+ dbPdfTemplate.getName(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(39);
            kanbanCategory.setName(CategoryName.PDFTEMPLATE_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotification.setPdfTemplate(dbPdfTemplate);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user.getId())){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            user.getName()+" criou o template PDF "+ dbPdfTemplate.getName()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Criação do template PDF "+ dbPdfTemplate.getName(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);
        });

        return ResponseEntity.status(HttpStatus.OK).body(dbPdfTemplate.getId().toString());
    }

    @DeleteMapping(path = "/private/user/signup/pdfEditor/template/{pdfEditorTemplateId}")
    public ResponseEntity<String> deletePdfEditorTemplate(@PathVariable Integer pdfEditorTemplateId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        boolean isPdfEditorTemplate = pdfTemplateRepository.findById(pdfEditorTemplateId).isPresent();
        if(!isPdfEditorTemplate){
            errorMessage.addProperty("mensagem","PdfEditorTemplate não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        PdfTemplate selectedPdfTemplate = pdfTemplateRepository.findById(pdfEditorTemplateId).get();

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();
        if(user.getPermissionLevel().charAt(38) == '0'){
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

            kanbanNotification.setMessage(
                    "Você deletou o template PDF "+ selectedPdfTemplate.getName()+"."
            );
            mailService.sendMail(user.getEmail(),"Deletando template PDF "+ selectedPdfTemplate.getName(),kanbanNotification.getMessage());

            KanbanCategory kanbanCategory = new KanbanCategory();
            kanbanCategory.setId(40);
            kanbanCategory.setName(CategoryName.PDFTEMPLATE_CREATE);
            kanbanNotification.setKanbanCategory(kanbanCategory);

            kanbanNotificationList.add(kanbanNotification);

            List<User> userList = userRepository.findAllByAdmin();
            userList.forEach(userAdmin->{
                if(!Objects.equals(userAdmin.getId(), user.getId())){
                    KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                    kanbanNotificationAdmin.setUser(userAdmin);
                    kanbanNotificationAdmin.setMessage(
                            user.getName()+" deletou o template PDF "+ selectedPdfTemplate.getName()+"."
                    );
                    mailService.sendMail(userAdmin.getEmail(),"Deletando do template PDF "+ selectedPdfTemplate.getName(),kanbanNotificationAdmin.getMessage());
                    kanbanNotificationList.add(kanbanNotificationAdmin);
                }
            });

            kanbanNotificationRepository.saveAll(kanbanNotificationList);

            pdfTemplateRepository.deleteById(pdfEditorTemplateId);
        });

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
