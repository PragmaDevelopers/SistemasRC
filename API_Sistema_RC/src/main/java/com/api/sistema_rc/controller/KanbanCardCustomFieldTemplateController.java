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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class KanbanCardCustomFieldTemplateController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KanbanColumnRepository kanbanColumnRepository;
    @Autowired
    private KanbanCardRepository kanbanCardRepository;
    @Autowired
    private KanbanCardTagRepository kanbanCardTagRepository;
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
    private KanbanDeadlineRepository kanbanDeadlineRepository;
    @Autowired
    private KanbanCardCustomFieldTemplateRepository kanbanCardCustomFieldTemplateRepository;
    @Autowired
    private UserRepository userRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/card/customField/templates")
    public ResponseEntity<String> getCustomFieldTemplates(@RequestParam(required = false,name = "name") String templateName) {

        List<KanbanCardCustomFieldTemplate> kanbanCardCustomFieldTemplate;

        if(templateName == null){
            kanbanCardCustomFieldTemplate = kanbanCardCustomFieldTemplateRepository.findAllOrderByName();
        }else{
            kanbanCardCustomFieldTemplate = kanbanCardCustomFieldTemplateRepository.findAllByName(templateName);
        }

        // Agrupar os objetos pelo campo "name"
        var groupedByTemplateName = kanbanCardCustomFieldTemplate.stream()
                .collect(Collectors.groupingBy(KanbanCardCustomFieldTemplate::getName));

        JsonArray customFieldTemplateArr = new JsonArray();

        // Para cada grupo, criar o objeto JSON desejado
        groupedByTemplateName.forEach((name, templates) -> {
            JsonObject customFieldTemplateObj = new JsonObject();
            customFieldTemplateObj.addProperty("name", name);

            JsonArray customFieldInputTemplateArr = new JsonArray();

            // Adicionar cada template ao array de inputs
            templates.forEach(template -> {
                JsonObject customFieldObj = new JsonObject();
                customFieldObj.addProperty("id", template.getId());
                customFieldObj.addProperty("inputName", template.getInputName());
                customFieldObj.addProperty("inputType", template.getInputType());
                customFieldObj.addProperty("index", template.getIndex());
                customFieldInputTemplateArr.add(customFieldObj);
            });

            // Adicionar o array de inputs ao objeto principal
            customFieldTemplateObj.add("inputs", customFieldInputTemplateArr);

            // Adicionar o objeto principal ao array resultante
            customFieldTemplateArr.add(customFieldTemplateObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(customFieldTemplateArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/customField/template")
    public ResponseEntity<String> postCustomFieldTemplate(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        Integer user_id = tokenService.validateToken(token);

        User user = userRepository.findById(user_id).get();

        if(user.getPermissionLevel().charAt(29) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar/salvar customField template!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement customFieldTemplateName = jsonObj.get("name");
        if(customFieldTemplateName == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement customFieldInputName = jsonObj.get("inputName");
        if(customFieldInputName == null){
            errorMessage.addProperty("mensagem","O campo inputName é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement customFieldInputType = jsonObj.get("inputType");
        if(customFieldInputType == null){
            errorMessage.addProperty("mensagem","O campo inputType é necessário!");
            errorMessage.addProperty("status",470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCardCustomFieldTemplate> customFieldTemplateList = kanbanCardCustomFieldTemplateRepository.findAllByName(customFieldTemplateName.getAsString());

        KanbanCardCustomFieldTemplate customFieldTemplate = new KanbanCardCustomFieldTemplate();

        customFieldTemplate.setIndex(customFieldTemplateList.size());
        customFieldTemplate.setName(customFieldTemplateName.getAsString());
        customFieldTemplate.setInputName(customFieldInputName.getAsString());
        customFieldTemplate.setInputType(customFieldInputType.getAsString());

        KanbanCardCustomFieldTemplate dbKanbanCardCustomField = kanbanCardCustomFieldTemplateRepository.saveAndFlush(customFieldTemplate);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardCustomField.getId().toString());
    }

    @Transactional
    @DeleteMapping(path = "/private/user/kanban/column/card/customField/template/{customFieldTemplateName}")
    public ResponseEntity<String> deleteCustomFieldTemplate(@PathVariable String customFieldTemplateName,
                                                    @RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        Integer user_id = tokenService.validateToken(token);

        User user = userRepository.findById(user_id).get();

        if(user.getPermissionLevel().charAt(31) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar customFieldTemplate)!");
            errorMessage.addProperty("status",475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardCustomFieldTemplate> kanbanCardCustomFieldTemplateList = kanbanCardCustomFieldTemplateRepository.findAllByName(customFieldTemplateName);
        if(kanbanCardCustomFieldTemplateList.isEmpty()){
            errorMessage.addProperty("mensagem", "CustomFieldTemplate não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }
        kanbanCardCustomFieldTemplateRepository.deleteAllByName(customFieldTemplateName);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}