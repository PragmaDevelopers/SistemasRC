package com.api.sistema_rc.service;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.KanbanCategory;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.repository.KanbanCategoryRepository;
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
    private KanbanCategoryRepository kanbanCategoryRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void saveRoles(){
        if(roleRepository.findAll().isEmpty()){
            for (RoleName roleName : RoleName.values()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
    @EventListener(ContextRefreshedEvent.class)
    public void saveCategories(){
        if(kanbanCategoryRepository.findAll().isEmpty()){
            for (CategoryName categoryName : CategoryName.values()) {
                KanbanCategory kanbanCategory = new KanbanCategory();
                kanbanCategory.setName(categoryName);
                kanbanCategoryRepository.save(kanbanCategory);
            }
        }
    }


}
