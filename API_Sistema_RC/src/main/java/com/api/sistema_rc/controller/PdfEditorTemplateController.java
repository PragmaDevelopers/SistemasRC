package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.ClientTemplate;
import com.api.sistema_rc.model.PdfEditorTemplate;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.PdfEditorTemplateRepository;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class PdfEditorTemplateController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PdfEditorTemplateRepository pdfEditorTemplateRepository;
    @Autowired
    private TokenService tokenService;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/signup/pdfEditor/templates")
    public ResponseEntity<String> getPdfEditorTemplates(@RequestHeader("Authorization") String token,
                                                        @RequestParam(required = false,defaultValue = "") String search){
        List<PdfEditorTemplate> pdfEditorTemplateList;
        if(Objects.equals(search, "")){
            pdfEditorTemplateList = pdfEditorTemplateRepository.findAll();
        }else{
            pdfEditorTemplateList = pdfEditorTemplateRepository.findAllByName(search);
        }

        JsonArray pdfEditorTemplateArr = new JsonArray();
        pdfEditorTemplateList.forEach(pdfEditorTemplate -> {
            JsonObject pdfEditorTemplateObj = new JsonObject();
            pdfEditorTemplateObj.addProperty("id",pdfEditorTemplate.getId());
            pdfEditorTemplateObj.addProperty("name",pdfEditorTemplate.getName());
            JsonElement template = JsonParser.parseString(pdfEditorTemplate.getTemplate());
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

        PdfEditorTemplate pdfEditorTemplate = new PdfEditorTemplate();
        pdfEditorTemplate.setName(name.getAsString());
        pdfEditorTemplate.setTemplate(template.getAsJsonArray().toString());

        PdfEditorTemplate dbClientTemplate = pdfEditorTemplateRepository.saveAndFlush(pdfEditorTemplate);

        return ResponseEntity.status(HttpStatus.OK).body(dbClientTemplate.getId().toString());
    }

    @DeleteMapping(path = "/private/user/signup/pdfEditor/template/{pdfEditorTemplateId}")
    public ResponseEntity<String> deletePdfEditorTemplate(@PathVariable Integer pdfEditorTemplateId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        boolean isClientTemplate = pdfEditorTemplateRepository.findById(pdfEditorTemplateId).isPresent();
        if(!isClientTemplate){
            errorMessage.addProperty("mensagem","PdfEditorTemplate não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();
        if(user.getPermissionLevel().charAt(38) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        pdfEditorTemplateRepository.deleteById(pdfEditorTemplateId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
