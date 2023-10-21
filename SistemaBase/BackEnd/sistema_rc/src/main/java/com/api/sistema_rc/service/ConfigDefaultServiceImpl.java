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
            Role role_admin = new Role();
            role_admin.setName(RoleName.ADMIN);

            Role role_professional = new Role();
            role_professional.setName(RoleName.PROFESSIONAL);

            Role role_client = new Role();
            role_client.setName(RoleName.CLIENT);

            List<Role> defaultRoles = new ArrayList<>();
            defaultRoles.add(role_admin);
            defaultRoles.add(role_professional);
            defaultRoles.add(role_client);
            if(!roleRepository.findAll().equals(defaultRoles)){
                roleRepository.saveAll(defaultRoles);
            }
        }
    }

}
