package com.api.sistema_rc.service;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.model.*;
import com.api.sistema_rc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ExpirationChecker {

    @Autowired
    private KanbanNotificationRepository kanbanNotificationRepository;
    @Autowired
    private KanbanDeadlineRepository kanbanDeadlineRepository;
    @Autowired
    private KanbanCategoryRepository kanbanCategoryRepository;
    @Autowired
    private KanbanUserRepository kanbanUserRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Scheduled(fixedRate = 15000) // Agendado para ser executado a cada 15 segundos (ajuste conforme necessário)
    public void verificarVencimentos() {
        List<KanbanDeadline> deadlines =  kanbanDeadlineRepository.findAllByOverdue(false);
        deadlines.forEach(deadline->{
            if(deadline.getDate().isBefore(LocalDateTime.now())){
                deadline.setOverdue(true);

                String extraMessage;

                if(deadline.getKanbanCategory().getName() == CategoryName.CARD_MOVE){
                    KanbanColumn toKanbanColumn = deadline.getActionKanbanColumn();
                    deadline.getKanbanCard().setKanbanColumn(toKanbanColumn);

                    extraMessage = ". Card movido para a coluna "+
                    toKanbanColumn.getTitle() + " do  kanban "+toKanbanColumn.getKanban().getTitle()+".";
                } else {
                    extraMessage = ".";
                }

                List<KanbanNotification> kanbanNotificationList = new ArrayList<>();

                KanbanNotification kanbanNotification = new KanbanNotification();

                kanbanNotification.setUser(deadline.getUser());
                kanbanNotification.setSenderUser(deadline.getUser());

                kanbanNotification.setRegistration_date(LocalDateTime.now());

                kanbanNotification.setViewed(false);

                if(deadline.getKanbanCard() != null){

                    kanbanNotification.setMessage("O prazo do card "+
                            deadline.getKanbanCard().getTitle()+" que você criou expirou"+extraMessage);

                    KanbanCategory kanbanCategoryNotification = new KanbanCategory();
                    kanbanCategoryNotification.setId(32);
                    kanbanCategoryNotification.setName(CategoryName.CARDDEADLINE_UPDATE);
                    kanbanNotification.setKanbanCategory(kanbanCategoryNotification);

                    kanbanNotification.setKanbanDeadline(deadline);

                    kanbanNotificationList.add(kanbanNotification);

                    List<User> userList = userRepository.findAllByAdmin();
                    userList.forEach(userAdmin->{
                        if(!Objects.equals(userAdmin.getId(), deadline.getUser().getId())){
                            KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                            kanbanNotificationAdmin.setUser(userAdmin);
                            kanbanNotificationAdmin.setMessage("O prazo do card "+deadline.getKanbanCard().getTitle()
                                    + " que "+ deadline.getUser().getName()+" criou expirou"+extraMessage);
                            kanbanNotificationList.add(kanbanNotificationAdmin);
                        }
                    });

                    Kanban kanban = deadline.getKanbanCard().getKanbanColumn().getKanban();
                    List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
                    kanbanUserList.forEach(userInKanban->{
                        if(!Objects.equals(userInKanban.getUser().getId(), deadline.getUser().getId())) {
                            String role = userInKanban.getUser().getRole().getName().name();
                            if (role.equals("ROLE_SUPERVISOR")) {
                                KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                                kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                                kanbanNotificationSupervisor.setMessage("O prazo do card "+deadline.getKanbanCard().getTitle()
                                        + " que "+ deadline.getUser().getName()+" criou expirou"+extraMessage);
                                kanbanNotificationList.add(kanbanNotificationSupervisor);
                            }
                        }
                    });

                } else if(deadline.getKanbanCardChecklist() != null){

                    kanbanNotification.setMessage("O prazo da checklist "+
                            deadline.getKanbanCardChecklist().getName()+" que você criou expirou"+extraMessage);

                    KanbanCategory kanbanCategoryNotification = new KanbanCategory();
                    kanbanCategoryNotification.setId(35);
                    kanbanCategoryNotification.setName(CategoryName.CARDCHECKLISTDEADLINE_UPDATE);
                    kanbanNotification.setKanbanCategory(kanbanCategoryNotification);

                    kanbanNotification.setKanbanDeadline(deadline);

                    kanbanNotificationList.add(kanbanNotification);

                    List<User> userList = userRepository.findAllByAdmin();
                    userList.forEach(userAdmin->{
                        if(!Objects.equals(userAdmin.getId(), deadline.getUser().getId())){
                            KanbanNotification kanbanNotificationAdmin = new KanbanNotification(kanbanNotification);
                            kanbanNotificationAdmin.setUser(userAdmin);
                            kanbanNotificationAdmin.setMessage("O prazo da checklist " +
                                    deadline.getKanbanCardChecklist().getName()+" que "+
                                    deadline.getUser().getName()+" criou expirou"+extraMessage);
                            kanbanNotificationList.add(kanbanNotificationAdmin);
                        }
                    });

                    Kanban kanban = deadline.getKanbanCardChecklist().getKanbanCard().getKanbanColumn().getKanban();
                    List<KanbanUser> kanbanUserList = kanbanUserRepository.findAllByKanbanId(kanban.getId());
                    kanbanUserList.forEach(userInKanban->{
                        if(!Objects.equals(userInKanban.getUser().getId(), deadline.getUser().getId())) {
                            String role = userInKanban.getUser().getRole().getName().name();
                            if (role.equals("ROLE_SUPERVISOR")) {
                                KanbanNotification kanbanNotificationSupervisor = new KanbanNotification(kanbanNotification);
                                kanbanNotificationSupervisor.setUser(userInKanban.getUser());
                                kanbanNotificationSupervisor.setMessage("O prazo da checklist " +
                                        deadline.getKanbanCardChecklist().getName()+" que "+
                                        deadline.getUser().getName()+" criou expirou"+extraMessage);
                                kanbanNotificationList.add(kanbanNotificationSupervisor);
                            }
                        }
                    });
                }

                kanbanNotificationRepository.saveAll(kanbanNotificationList);
            }
        });
    }
}

