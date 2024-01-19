package com.api.sistema_rc.service;

import com.api.sistema_rc.enums.CategoryName;
import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.ClientTemplate;
import com.api.sistema_rc.model.KanbanCategory;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.ClientTemplateRepository;
import com.api.sistema_rc.repository.KanbanCategoryRepository;
import com.api.sistema_rc.repository.RoleRepository;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.PasswordEncoderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigDefaultServiceImpl {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private KanbanCategoryRepository kanbanCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientTemplateRepository clientTemplateRepository;

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

    @EventListener(ContextRefreshedEvent.class)
    public void saveAdminUsers(){
        if(userRepository.findAll().isEmpty()){
            User user1 = new User();
            user1.setName("Admin");
            user1.setEmail("admin@gmail.com");

            String encryptedPassword = PasswordEncoderUtils.encode("123");
            user1.setPassword(encryptedPassword);

            user1.setNationality("Brasileiro");
            user1.setRegistration_date(LocalDateTime.now());
            user1.setPermissionLevel("11111111111111111111111111111111111");
            user1.setVerify(true);
            user1.setCodeToVerify("2352064942");

            Role roleAdmin = new Role();
            roleAdmin.setId(1);
            roleAdmin.setName(RoleName.ROLE_ADMIN);

            user1.setRole(roleAdmin);

            userRepository.save(user1);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void saveClientTemplates(){
        if(clientTemplateRepository.findAll().isEmpty()){
            // Instancie um ObjectMapper do Jackson
            ObjectMapper objectMapper = new ObjectMapper();

            try {

                String path1 = "static/client_template_1.json";

                // Carregue o arquivo JSON usando a classe Resource do Spring
                Resource resource = new ClassPathResource(path1);

                ClientTemplate clientTemplate = new ClientTemplate();
                clientTemplate.setName("Template padrão (fixo)");
                clientTemplate.setTemplate(resource.getContentAsString(Charset.defaultCharset()));

                clientTemplateRepository.save(clientTemplate);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
