package com.api.sistema_rc.service;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.Role;
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

    @EventListener(ContextRefreshedEvent.class)
    public void saveRoles(){
        if(roleRepository.findAll().isEmpty()){
            Role roleAdmin = new Role();
            roleAdmin.setName(RoleName.ROLE_ADMIN);

            Role roleSupervisor = new Role();
            roleSupervisor.setName(RoleName.ROLE_SUPERVISOR);

            Role roleMember = new Role();
            roleMember.setName(RoleName.ROLE_MEMBER);

            List<Role> defaultRoles = new ArrayList<>();
            defaultRoles.add(roleAdmin);
            defaultRoles.add(roleSupervisor);
            defaultRoles.add(roleMember);

            roleRepository.saveAll(defaultRoles);
        }
    }

}
