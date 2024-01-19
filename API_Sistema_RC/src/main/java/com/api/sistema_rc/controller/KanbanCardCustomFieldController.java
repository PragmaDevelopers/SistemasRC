package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class KanbanCardCustomFieldController {
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
    private KanbanCardCustomFieldRepository kanbanCardCustomFieldRepository;
    @Autowired
    private UserRepository userRepository;
    private final Gson gson = new Gson();

    @GetMapping(path = "/private/user/kanban/column/card/{cardId}/customFields")
    public ResponseEntity<String> getCustomFields(@PathVariable Integer cardId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();

        if (cardId == null) {
            errorMessage.addProperty("mensagem", "O campo cardId é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if (!isCard) {
            errorMessage.addProperty("mensagem", "Card não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCardCustomField> kanbanCardCustomField = kanbanCardCustomFieldRepository.findAllByCardId(cardId);

        JsonArray customFieldArr = new JsonArray();

        kanbanCardCustomField.forEach(customField -> {
            JsonObject customFieldObj = new JsonObject();
            customFieldObj.addProperty("id", customField.getId());
            customFieldObj.addProperty("name", customField.getName());
            customFieldObj.addProperty("value", customField.getValue());
            customFieldObj.addProperty("fieldType", customField.getType());
            customFieldObj.addProperty("index", customField.getIndex());
            customFieldArr.add(customFieldObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(customFieldArr.toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/customField")
    public ResponseEntity<String> postCustomField(@RequestBody String body, @RequestHeader("Authorization") String token) {
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if (cardId == null) {
            errorMessage.addProperty("mensagem", "O campo cardId é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if (!isCard) {
            errorMessage.addProperty("mensagem", "Card não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if (kanbanUser.getUser().getPermissionLevel().charAt(29) == '0') {
            errorMessage.addProperty("mensagem", "Você não tem autorização para essa ação (criar customField!");
            errorMessage.addProperty("status", 475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement customFieldName = jsonObj.get("name");
        if (customFieldName == null) {
            errorMessage.addProperty("mensagem", "O campo name é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement customFieldValue = jsonObj.get("value");
        if (customFieldValue == null) {
            errorMessage.addProperty("mensagem", "O campo value é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement customFieldType = jsonObj.get("fieldType");
        if (customFieldType == null) {
            errorMessage.addProperty("mensagem", "O campo fieldType é necessário!");
            errorMessage.addProperty("status", 470);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCardCustomField> kanbanCardCustomFieldList = kanbanCardCustomFieldRepository.findAllByCardId(cardId.getAsInt());

        KanbanCardCustomField kanbanCardCustomField = new KanbanCardCustomField();

        kanbanCardCustomField.setIndex(kanbanCardCustomFieldList.size());
        kanbanCardCustomField.setName(customFieldName.getAsString());
        kanbanCardCustomField.setValue(customFieldValue.getAsString());
        kanbanCardCustomField.setType(customFieldType.getAsString());
        kanbanCardCustomField.setKanbanCard(kanbanCard);

        KanbanCardCustomField dbKanbanCardCustomField = kanbanCardCustomFieldRepository.saveAndFlush(kanbanCardCustomField);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o customField " + dbKanbanCardCustomField.getName() + " no card " + kanbanCard.getTitle() +
                        " da coluna " + kanbanCard.getKanbanColumn().getTitle() +
                        " do kanban " + kanbanCard.getKanbanColumn().getKanban().getTitle() + "."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(28);
        kanbanCategory.setName(CategoryName.CARDCUSTOMFIELD_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardCustomField(dbKanbanCardCustomField);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin -> {
            if (!Objects.equals(userAdmin.getId(), user_id)) {
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou o customField  " +
                                dbKanbanCardCustomField.getName() + " no card " + kanbanCard.getTitle() +
                                " da coluna " + kanbanCard.getKanbanColumn().getTitle() +
                                " do kanban " + kanbanCard.getKanbanColumn().getKanban().getTitle() + "."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban -> {
            if (!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " criou o customField  " +
                                    dbKanbanCardCustomField.getName() + " no card " + kanbanCard.getTitle() +
                                    " da coluna " + kanbanCard.getKanbanColumn().getTitle() +
                                    " do kanban " + kanbanCard.getKanbanColumn().getKanban().getTitle() + "."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCardCustomField.getId().toString());
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/customField/{customFieldId}")
    public ResponseEntity<String> patchCustomField(@RequestBody String body, @RequestHeader("Authorization") String token,
                                                   @PathVariable Integer customFieldId) {
        JsonObject errorMessage = new JsonObject();

        boolean isCustomField = kanbanCardCustomFieldRepository.findById(customFieldId).isPresent();
        if (!isCustomField) {
            errorMessage.addProperty("mensagem", "CustomField não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCardCustomField selectedCustomField = kanbanCardCustomFieldRepository.findById(customFieldId).get();

        Kanban kanban = selectedCustomField.getKanbanCard().getKanbanColumn().getKanban();

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if (kanbanUser.getUser().getPermissionLevel().charAt(30) == '0') {
            errorMessage.addProperty("mensagem", "Você não tem autorização para essa ação!");
            errorMessage.addProperty("status", 475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement customFieldName = jsonObj.get("name");
        String oldCustomFieldName = selectedCustomField.getName();
        if (customFieldName != null) {
            selectedCustomField.setName(customFieldName.getAsString());
            modifiedArr.add("nome");
        }

        JsonElement customFieldValue = jsonObj.get("value");
        if (customFieldValue != null) {
            selectedCustomField.setValue(customFieldValue.getAsString());
            modifiedArr.add("valor");
        }

        JsonElement customFieldType = jsonObj.get("fieldType");
        if (customFieldType != null) {
            selectedCustomField.setType(customFieldType.getAsString());
            modifiedArr.add("tipo de campo");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou (" + String.join(",", modifiedArr) + ") no customField " +
                selectedCustomField.getName() + " do card " + selectedCustomField.getKanbanCard().getTitle() +
                " da coluna " + selectedCustomField.getKanbanCard().getKanbanColumn().getTitle() +
                " do kanban " + selectedCustomField.getKanbanCard().getKanbanColumn().getKanban().getTitle() + ".";

        if (customFieldName != null) {
            message = " atualizou (" + String.join(",", modifiedArr) + ") no customField " +
                    oldCustomFieldName + " (nome antigo) | " + selectedCustomField.getName() + " (novo nome) do card " +
                    selectedCustomField.getKanbanCard().getTitle() + " da coluna " +
                    selectedCustomField.getKanbanCard().getKanbanColumn().getTitle() + " do kanban " +
                    selectedCustomField.getKanbanCard().getKanbanColumn().getKanban().getTitle() + ".";
        }

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage("Você" + message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(29);
        kanbanCategory.setName(CategoryName.CARDCUSTOMFIELD_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCardCustomField(selectedCustomField);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin -> {
            if (!Objects.equals(userAdmin.getId(), user_id)) {
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName() + finalMessage);
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban -> {
            if (!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName() + finalMessage);
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping(path = "/private/user/kanban/column/card/customField/{customFieldId}")
    public ResponseEntity<String> deleteCustomField(@PathVariable Integer customFieldId, @RequestHeader("Authorization") String token) {
        JsonObject errorMessage = new JsonObject();
        boolean isCustomField = kanbanCardCustomFieldRepository.findById(customFieldId).isPresent();
        if (!isCustomField) {
            errorMessage.addProperty("mensagem", "CustomField não foi encontrado!");
            errorMessage.addProperty("status", 474);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCardCustomField selectedCustomField = kanbanCardCustomFieldRepository.findById(customFieldId).get();

        Kanban kanban = selectedCustomField.getKanbanCard().getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(), user_id);

        if (kanbanUser == null) {
            errorMessage.addProperty("mensagem", "Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status", 471);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if (kanbanUser.getUser().getPermissionLevel().charAt(31) == '0') {
            errorMessage.addProperty("mensagem", "Você não tem autorização para essa ação!");
            errorMessage.addProperty("status", 475);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistrationDate(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou o customField " +
                        selectedCustomField.getName() + " no card " + selectedCustomField.getKanbanCard().getTitle() +
                        " da coluna " + selectedCustomField.getKanbanCard().getKanbanColumn().getTitle() +
                        " do kanban " + selectedCustomField.getKanbanCard().getKanbanColumn().getKanban().getTitle() + "."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(30);
        kanbanCategory.setName(CategoryName.CARDCUSTOMFIELD_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);
        kanbanNotification.setKanbanCardCustomField(null);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin -> {
            if (!Objects.equals(userAdmin.getId(), user_id)) {
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " deletou o customField " +
                                selectedCustomField.getName() + " no card " + selectedCustomField.getKanbanCard().getTitle() +
                                " da coluna " + selectedCustomField.getKanbanCard().getKanbanColumn().getTitle() +
                                " do kanban " + selectedCustomField.getKanbanCard().getKanbanColumn().getKanban().getTitle() + "."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban -> {
            if (!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " deletou o customField " +
                                    selectedCustomField.getName() + " no card " + selectedCustomField.getKanbanCard().getTitle() +
                                    " da coluna " + selectedCustomField.getKanbanCard().getKanbanColumn().getTitle() +
                                    " do kanban " + selectedCustomField.getKanbanCard().getKanbanColumn().getKanban().getTitle() + "."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanCardCustomFieldRepository.deleteById(customFieldId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

