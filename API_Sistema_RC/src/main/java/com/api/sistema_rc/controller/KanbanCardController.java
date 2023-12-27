package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(path = "/api")
public class KanbanCardController {
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
    private UserRepository userRepository;
    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    private final Gson gson = new Gson();
    @GetMapping(path = "/private/user/kanban/column/{columnId}/cards")
    public ResponseEntity<String> getCards(@PathVariable Integer columnId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();

        if(columnId == null){
            errorMessage.addProperty("mensagem","O campo columnId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanColumnRepository.findById(columnId).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn kanbanColumn = kanbanColumnRepository.findById(columnId).get();

        Kanban kanban = kanbanColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId);

        JsonArray cardsArr = new JsonArray();

        kanbanCardList.forEach(card->{
            JsonObject cardObj = new JsonObject();
            cardObj.addProperty("id",card.getId());
            cardObj.addProperty("title",card.getTitle());
            cardObj.addProperty("description",card.getDescription());
            cardObj.addProperty("index",card.getIndex());

            JsonArray members = new JsonArray();
            for (String memberId : card.getMembers().split(",")) {
                members.add(Integer.parseInt(memberId));
            }
            cardObj.add("members", members);

            cardsArr.add(cardObj);
        });

        return ResponseEntity.status(HttpStatus.OK).body(cardsArr.toString());
    }
    @PostMapping(path = "/private/user/kanban/column/card")
    public ResponseEntity<String> postCard(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement columnId = jsonObj.get("columnId");
        if(columnId == null){
            errorMessage.addProperty("mensagem","O campo columnId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanColumnRepository.findById(columnId.getAsInt()).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Column não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn kanbanColumn = kanbanColumnRepository.findById(columnId.getAsInt()).get();

        Kanban kanban = kanbanColumn.getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(0) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar card)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(columnId.getAsInt());

        KanbanCard kanbanCard = new KanbanCard();

        kanbanCard.setIndex(kanbanCardList.size());

        kanbanCard.setTitle(cardTitle.getAsString());
        kanbanCard.setKanbanColumn(kanbanColumn);

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription != null){
            kanbanCard.setDescription(cardDescription.getAsString());
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers != null){
            JsonArray membersId = cardMembers.getAsJsonArray();
            List<String> arrayToStringArr = new ArrayList<>();
            if(!membersId.isEmpty()){
                Integer kanbanId = kanban.getId();
                List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);

                for (JsonElement memberId : membersId) {
                    AtomicBoolean isFound = new AtomicBoolean(false);
                    kanbanUserList.forEach(kanUser ->{
                        if(kanUser.getUser().getId() == memberId.getAsInt()){
                            isFound.set(true);
                        }
                    });
                    if(!isFound.get()){
                        errorMessage.addProperty("mensagem","Usuário não existente nesse kanban detectado!");
                        errorMessage.addProperty("status",433);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                    }
                    arrayToStringArr.add(String.valueOf(memberId.getAsInt()));
                }
            }
            kanbanCard.setMembers(String.join(",",arrayToStringArr));
        }

        KanbanCard dbKanbanCard = kanbanCardRepository.saveAndFlush(kanbanCard);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o card " + dbKanbanCard.getTitle() + " na coluna "+kanbanColumn.getTitle()+
                        " do kanban "+ kanban.getTitle() +"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(10);
        kanbanCategory.setName(CategoryName.CARD_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCard(dbKanbanCard);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou o card " +
                                dbKanbanCard.getTitle() + " na coluna "+kanbanColumn.getTitle()+
                                " do kanban "+ kanban.getTitle() +"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " criou o card " +
                                    dbKanbanCard.getTitle() + " na coluna "+kanbanColumn.getTitle()+
                                    " do kanban "+ kanban.getTitle() +"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCard.getId().toString());
    }

    @PostMapping(path = "/private/user/kanban/column/card/innerCard")
    public ResponseEntity<String> postInnerCard(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        boolean isColumn = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isColumn){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard kanbanCardParent = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = kanbanCardParent.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(0) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar card)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        boolean isCardParent = true;
        for (String memberId : kanbanCardParent.getMembers().split(",")) {
            if(Integer.parseInt(memberId) == user_id){
                isCardParent = false;
            }
        }

        if(isCardParent){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (criar card dentro deste card)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement cardTitle = jsonObj.get("title");
        if(cardTitle == null){
            errorMessage.addProperty("mensagem","O campo title é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        List<KanbanCard> kanbanCardList = kanbanCardRepository.findAllByColumnId(cardId.getAsInt());

        KanbanCard kanbanCard = new KanbanCard();

        kanbanCard.setIndex(kanbanCardList.size());

        kanbanCard.setTitle(cardTitle.getAsString());
        kanbanCard.setKanbanColumn(kanbanCardParent.getKanbanColumn());
        kanbanCard.setKanbanInnerCard(kanbanCardParent);

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription != null){
            kanbanCard.setDescription(cardDescription.getAsString());
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers != null){
            JsonArray membersId = cardMembers.getAsJsonArray();
            List<String> arrayToStringArr = new ArrayList<>();
            if(!membersId.isEmpty()){
                Integer kanbanId = kanban.getId();
                List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);

                for (JsonElement memberId : membersId) {
                    AtomicBoolean isFound = new AtomicBoolean(false);
                    kanbanUserList.forEach(kanUser ->{
                        if(kanUser.getUser().getId() == memberId.getAsInt()){
                            isFound.set(true);
                        }
                    });
                    if(!isFound.get()){
                        errorMessage.addProperty("mensagem","Usuário não existente nesse kanban detectado!");
                        errorMessage.addProperty("status",433);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                    }
                    arrayToStringArr.add(String.valueOf(memberId.getAsInt()));
                }
            }
            kanbanCard.setMembers(String.join(",",arrayToStringArr));
        }

        KanbanCard dbKanbanCard = kanbanCardRepository.saveAndFlush(kanbanCard);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você criou o card " + dbKanbanCard.getTitle() + " dentro do card "+kanbanCardParent.getTitle()+
                        " na coluna "+kanbanCardParent.getKanbanColumn().getTitle()+ " do kanban "+ kanban.getTitle() +"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(11);
        kanbanCategory.setName(CategoryName.INNERCARD_CREATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCard(dbKanbanCard);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " criou o card "+dbKanbanCard.getTitle() +
                                " dentro do card "+kanbanCardParent.getTitle()+" na coluna "+kanbanCardParent.getKanbanColumn().getTitle()+
                                " do kanban "+ kanban.getTitle() +"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " criou o card "+dbKanbanCard.getTitle() +
                                    " dentro do card "+kanbanCardParent.getTitle()+" na coluna "+kanbanCardParent.getKanbanColumn().getTitle()+
                                    " do kanban "+ kanban.getTitle() +"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).body(dbKanbanCard.getId().toString());
    }

    @DeleteMapping(path = "/private/user/kanban/column/card/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Integer cardId,@RequestHeader("Authorization") String token){
        JsonObject errorMessage = new JsonObject();
        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard selectedCard = kanbanCardRepository.findById(cardId).get();

        Kanban kanban = selectedCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(2) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar card)!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(selectedCard.getKanbanInnerCard() != null){
            boolean isCardParent = true;
            for (String memberId : selectedCard.getKanbanInnerCard().getMembers().split(",")) {
                if(Integer.parseInt(memberId) == user_id){
                    isCardParent = false;
                }
            }

            if(isCardParent){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (deletar card dentro deste card)!");
                errorMessage.addProperty("status",435);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }

        Integer columnId = selectedCard.getKanbanColumn().getId();
        List<KanbanCard> cardsList = kanbanCardRepository.findAllByColumnId(columnId);
        boolean isSearch = false;
        for(int i = 0;i < cardsList.size();i++){
            if(Objects.equals(cardsList.get(i).getId(), cardId)){
                isSearch = true;
            }else{
                if(isSearch){
                    cardsList.get(i).setIndex(i - 1);
                }else{
                    cardsList.get(i).setIndex(i);
                }
            }
        }

        List<KanbanCardChecklist> kanbanCardCheckList = kanbanCardCheckListRepository.findAllByCardId(cardId);
        kanbanCardCheckList.forEach(checkList->{
            List<KanbanCardChecklistItem> kanbanCardChecklistItems = kanbanCardCheckListItemRepository.findAllByChecklistId(checkList.getId());
            kanbanCardCheckListItemRepository.deleteAll(kanbanCardChecklistItems);
        });
        kanbanCardCheckListRepository.deleteAll(kanbanCardCheckList);

        List<KanbanCardTag> kanbanCardTagList = kanbanCardTagRepository.findAllByCardId(cardId);
        kanbanCardTagRepository.deleteAll(kanbanCardTagList);

        List<KanbanCardComment> kanbanCardCommentList = kanbanCardCommentRepository.findAllByCardId(cardId);
        kanbanCardCommentRepository.deleteAll(kanbanCardCommentList);

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você deletou o card " + selectedCard.getTitle() + " na coluna "
                        +selectedCard.getKanbanColumn().getTitle()+ " do kanban "+ kanban.getTitle() +"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(13);
        kanbanCategory.setName(CategoryName.CARD_DELETE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        for (KanbanNotification dbNotificationCard : kanbanNotificationRepository.findAllByCardId(cardId)) {
            dbNotificationCard.setKanbanCard(null);
        }

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " deletou o card " + selectedCard.getTitle() + " na coluna "+
                                selectedCard.getKanbanColumn().getTitle()+ " do kanban "+ kanban.getTitle() +"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " deletou o card " + selectedCard.getTitle() + " na coluna "+
                                    selectedCard.getKanbanColumn().getTitle()+ " do kanban "+ kanban.getTitle() +"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        kanbanCardRepository.deleteById(cardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/card/{cardId}")
    public ResponseEntity<String> patchCard(@RequestBody String body,@RequestHeader("Authorization") String token,
                                            @PathVariable Integer cardId){
        JsonObject errorMessage = new JsonObject();

        boolean isCard = kanbanCardRepository.findById(cardId).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        KanbanCard selectedCard = kanbanCardRepository.findById(cardId).get();

        Integer selectedKanbanId;
        Kanban kanban = selectedCard.getKanbanColumn().getKanban();

        JsonElement toColumnId = jsonObj.get("toColumnId");
        if(toColumnId == null){
            selectedKanbanId = kanban.getId();
        }else{
            boolean isColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
            if(!isColumn){
                errorMessage.addProperty("mensagem","Column não foi encontrado!");
                errorMessage.addProperty("status",434);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
            }
            KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();
            selectedKanbanId = toColumn.getKanban().getId();
        }

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(selectedKanbanId,user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(3) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(selectedCard.getKanbanInnerCard() != null){
            boolean isCardParent = true;
            for (String memberId : selectedCard.getKanbanInnerCard().getMembers().split(",")) {
                if(Integer.parseInt(memberId) == user_id){
                    isCardParent = false;
                }
            }
            if(isCardParent){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (atualizar card dentro deste card)!");
                errorMessage.addProperty("status",435);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
            }
        }

        if(!Objects.equals(selectedKanbanId, kanban.getId())){
            List<KanbanCard> toCardsList = kanbanCardRepository.findAllByColumnId(toColumnId.getAsInt());
            KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();

            selectedCard.setKanbanColumn(toColumn);
            selectedCard.setIndex(toCardsList.size());
        }

        List<String> modifiedArr = new ArrayList<>();

        JsonElement cardTitle = jsonObj.get("title");
        String oldCardTitle = selectedCard.getTitle();
        if(cardTitle != null){
            selectedCard.setTitle(cardTitle.getAsString());
            modifiedArr.add("título");
        }

        JsonElement cardDescription = jsonObj.get("description");
        if(cardDescription != null){
            selectedCard.setDescription(cardDescription.getAsString());
            modifiedArr.add("descrição");
        }

        JsonElement cardMembers = jsonObj.get("members");
        if(cardMembers != null){
            String[] membersId = cardMembers.getAsString().split(",");
            if(!Objects.equals(cardMembers.getAsString(), "") && cardMembers.getAsString() != null){
                Integer kanbanId = kanban.getId();
                List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanbanId);
                for (String memberId : membersId) {
                    AtomicBoolean isFound = new AtomicBoolean(false);
                    kanbanUserList.forEach(kanUser ->{
                        if(kanUser.getUser().getId() == Integer.parseInt(memberId)){
                            isFound.set(true);
                        }
                    });
                    if(!isFound.get()){
                        errorMessage.addProperty("mensagem","Usuário não existente nesse kanban detectado!");
                        errorMessage.addProperty("status",433);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
                    }
                }
            }
            selectedCard.setMembers(cardMembers.getAsString());
            modifiedArr.add("membros");
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        String message = " atualizou (" +String.join(",",modifiedArr)+ ") no card " + selectedCard.getTitle() +
                " da coluna "+selectedCard.getKanbanColumn().getTitle()+" do kanban "+kanban.getTitle()+".";

        if(cardTitle != null){
            message = " atualizou (" +String.join(",",modifiedArr)+ ") no card " + oldCardTitle +
                    " (título antigo) | "+selectedCard.getTitle()+ " (título novo) da coluna "+
                    selectedCard.getKanbanColumn().getTitle()+" do kanban "+kanban.getTitle()+".";
        }

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage("Você"+message);
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(12);
        kanbanCategory.setName(CategoryName.CARD_UPDATE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCard(selectedCard);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        String finalMessage = message;
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(kanbanUser.getUser().getName()+finalMessage);
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(kanbanUser.getUser().getName()+finalMessage);
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/kanban/column/move/card")
    public ResponseEntity<String> moveCard(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement cardId = jsonObj.get("cardId");
        if(cardId == null){
            errorMessage.addProperty("mensagem","O campo cardId é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isCard = kanbanCardRepository.findById(cardId.getAsInt()).isPresent();
        if(!isCard){
            errorMessage.addProperty("mensagem","Card não foi encontrado!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanCard selectedCard = kanbanCardRepository.findById(cardId.getAsInt()).get();

        Kanban kanban = selectedCard.getKanbanColumn().getKanban();
        Integer user_id = tokenService.validateToken(token);

        KanbanUser kanbanUser = kanbanUserRepository.findByKanbanIdAndUserId(kanban.getId(),user_id);

        if(kanbanUser == null){
            errorMessage.addProperty("mensagem","Você não está cadastrado nesse kanban!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(kanbanUser.getUser().getPermissionLevel().charAt(1) == '0'){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        if(selectedCard.getKanbanInnerCard() != null){
            errorMessage.addProperty("mensagem","Não é possivel mover um innerCard!");
            errorMessage.addProperty("status",435);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.toString());
        }

        JsonElement toColumnId = jsonObj.get("toColumnId");
        if(toColumnId == null){
            errorMessage.addProperty("mensagem","O campo toColumnId é necessário!");
            errorMessage.addProperty("status",431);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isToColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).isPresent();
        if(!isToColumn){
            errorMessage.addProperty("mensagem","Coluna de destino não foi encontrada!");
            errorMessage.addProperty("status",434);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.toString());
        }

        KanbanColumn currentColumn = selectedCard.getKanbanColumn();
        KanbanColumn toColumn = kanbanColumnRepository.findById(toColumnId.getAsInt()).get();

//        if(!Objects.equals(currentColumn.getKanban().getId(), toColumn.getKanban().getId())){
//            errorMessage.addProperty("mensagem","As colunas não são do mesmo kanban!");
//            errorMessage.addProperty("status",432);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
//        }

        JsonElement toIndex = jsonObj.get("toIndex");
        if(toIndex == null){
            errorMessage.addProperty("mensagem","O campo toIndex é necessário!");
            errorMessage.addProperty("status",430);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Integer currentColumnId = selectedCard.getKanbanColumn().getId();
        List<KanbanCard> toCardsList = kanbanCardRepository.findAllByColumnId(toColumnId.getAsInt());

        if(currentColumnId != toColumnId.getAsInt()){
            selectedCard.setKanbanColumn(toColumn);
            if(toIndex.getAsInt() >= toCardsList.size()){
                selectedCard.setIndex(toCardsList.size());
                toCardsList.add(selectedCard);
            }else{
                selectedCard.setIndex(toIndex.getAsInt());
                toCardsList.add(toIndex.getAsInt(),selectedCard);
                for(int i = 0;i < toCardsList.size();i++){
                    toCardsList.get(i).setIndex(i);
                }
            }
            List<KanbanCard> currentCardsList = kanbanCardRepository.findAllByColumnId(currentColumnId);
            if(!currentCardsList.isEmpty()){
                for(int i = 0;i < currentCardsList.size();i++){
                    currentCardsList.get(i).setIndex(i);
                }
            }
        }else{
            if(toIndex.getAsInt() >= toCardsList.size()){
                selectedCard.setIndex(toCardsList.size() - 1);
            }else{
                selectedCard.setIndex(toIndex.getAsInt());
            }
            toCardsList.sort(Comparator.comparing(KanbanCard::getIndex));
            for (int i = 0;i < toCardsList.size();i++) {
                if(Objects.equals(toCardsList.get(i).getId(), selectedCard.getId()) && Objects.equals(toCardsList.get(i + 1).getIndex(), selectedCard.getIndex())){
                    toCardsList.get(i).setIndex(i + 1);
                    toCardsList.get(i + 1).setIndex(i);
                    i+=2;
                    if(i >= toCardsList.size()){
                        break;
                    }
                }else if(Objects.equals(toCardsList.get(i).getId(), selectedCard.getId()) && toCardsList.get(i - 1).getIndex() == toIndex.getAsInt()){
                    toCardsList.get(i).setIndex(i - 1);
                    toCardsList.get(i - 1).setIndex(i);
                    i+=1;
                }{
                    toCardsList.get(i).setIndex(i);
                }
            }
        }

        List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

        KanbanNotification kanbanNotification = new KanbanNotification();

        kanbanNotification.setUser(kanbanUser.getUser());
        kanbanNotification.setSenderUser(kanbanUser.getUser());

        kanbanNotification.setRegistration_date(LocalDateTime.now());
        kanbanNotification.setMessage(
                "Você moveu o card " + selectedCard.getTitle() + " da coluna "+
                        selectedCard.getKanbanColumn().getTitle()+ " para a coluna "+toColumn.getTitle()+
                        " do kanban "+ selectedCard.getKanbanColumn().getKanban().getTitle() +"."
        );
        kanbanNotification.setViewed(false);

        KanbanCategory kanbanCategory = new KanbanCategory();
        kanbanCategory.setId(14);
        kanbanCategory.setName(CategoryName.CARD_MOVE);
        kanbanNotification.setKanbanCategory(kanbanCategory);

        kanbanNotification.setKanbanCard(selectedCard);

        kanbanNotificationList.add(kanbanNotification);

        List<User> userList = userRepository.findAllByAdmin();
        userList.forEach(userAdmin->{
            if(!Objects.equals(userAdmin.getId(), user_id)){
                KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                kanbanNotificationAdmin.setUser(userAdmin);
                kanbanNotificationAdmin.setMessage(
                        kanbanUser.getUser().getName() + " moveu o card " +
                                selectedCard.getTitle() + " da coluna "+selectedCard.getKanbanColumn().getTitle()+
                                " para a coluna "+toColumn.getTitle()+
                                " do kanban "+ selectedCard.getKanbanColumn().getKanban().getTitle() +"."
                );
                kanbanNotificationList.add(kanbanNotificationAdmin);
            }
        });

        List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
        kanbanUserList.forEach(userInKanban->{
            if(!Objects.equals(userInKanban.getUser().getId(), user_id)) {
                String role = userInKanban.getUser().getRole().getName().name();
                if (role.equals("ROLE_SUPERVISOR")) {
                    KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                    kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                    kanbanNotificationSupervisor.setMessage(
                            kanbanUser.getUser().getName() + " moveu o card " +
                                    selectedCard.getTitle() + " da coluna "+selectedCard.getKanbanColumn().getTitle()+
                                    " para a coluna "+toColumn.getTitle()+
                                    " do kanban "+ selectedCard.getKanbanColumn().getKanban().getTitle() +"."
                    );
                    kanbanNotificationList.add(kanbanNotificationSupervisor);
                }
            }
        });

        kanbanNotificationRepository.saveAll(kanbanNotificationList);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
