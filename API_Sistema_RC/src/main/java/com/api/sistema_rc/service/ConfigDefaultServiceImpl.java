package com.api.sistema_rc.service;

import com.api.sistema_rc.enums.KanbanRoleName;
import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.KanbanRole;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.repository.KanbanRoleRepository;
import com.api.sistema_rc.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigDefaultServiceImpl {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private KanbanRoleRepository kanbanRoleRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void saveRoles(){
        if(roleRepository.findAll().isEmpty()){
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleName.ROLE_ADMIN);

            Role roleProfessional = new Role();
            roleProfessional.setName(RoleName.ROLE_PROFESSIONAL);

            Role roleClient = new Role();
            roleClient.setName(RoleName.ROLE_CLIENT);

            List<Role> defaultRoles = new ArrayList<>();
            defaultRoles.add(roleAdmin);
            defaultRoles.add(roleProfessional);
            defaultRoles.add(roleClient);

            roleRepository.saveAll(defaultRoles);
        }
        if(kanbanRoleRepository.findAll().isEmpty()){
            KanbanRole kanbanRoleAdmin = new KanbanRole();
            kanbanRoleAdmin.setName(KanbanRoleName.ADMIN);

            KanbanRole kanbanRoleSupervisor = new KanbanRole();
            kanbanRoleSupervisor.setName(KanbanRoleName.SUPERVISOR);

            KanbanRole kanbanRoleMember = new KanbanRole();
            kanbanRoleMember.setName(KanbanRoleName.MEMBER);

            List<KanbanRole> defaultKanbanRoles = new ArrayList<>();
            defaultKanbanRoles.add(kanbanRoleAdmin);
            defaultKanbanRoles.add(kanbanRoleSupervisor);
            defaultKanbanRoles.add(kanbanRoleMember);

            kanbanRoleRepository.saveAll(defaultKanbanRoles);
        }
    }

}
