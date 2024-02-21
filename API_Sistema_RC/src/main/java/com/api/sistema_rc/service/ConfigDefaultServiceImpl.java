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
            user1.setName("Rafael do Canto");
            user1.setEmail("Rafael@rafaeldocanto.com.br");

            String encryptedPassword1 = PasswordEncoderUtils.encode("123");
            user1.setPassword(encryptedPassword1);

            user1.setNationality("Brasileiro");
            user1.setRegistration_date(LocalDateTime.now());
            user1.setPermissionLevel("111111111111111111111111111111111111111");
            user1.setVerify(true);
            user1.setCodeToVerify(null);
            user1.setReceiveNotification(false);

            Role roleAdmin = new Role();
            roleAdmin.setId(1);
            roleAdmin.setName(RoleName.ROLE_ADMIN);

            user1.setRole(roleAdmin);

            User user2 = new User();
            user2.setName("Lucas Emanuel Santana Dos Santos");
            user2.setEmail("lucasemanuel2077@gmail.com");

            String encryptedPassword2 = PasswordEncoderUtils.encode("2077Rr-84");
            user2.setPassword(encryptedPassword2);

            user2.setNationality("Brasileiro");
            user2.setRegistration_date(LocalDateTime.now());
            user2.setPermissionLevel("111111111111111111111111111111111111111");
            user2.setVerify(true);
            user2.setCodeToVerify(null);
            user2.setReceiveNotification(true);

            user2.setRole(roleAdmin);

            List<User> userList = new ArrayList<>();
            userList.add(user1);
            userList.add(user2);

            userRepository.saveAll(userList);
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void saveClientTemplates(){
        if(clientTemplateRepository.findAll().isEmpty()){
            // Instancie um ObjectMapper do Jackson

            String path = "static/client_template_1.json";

            // Carregue o arquivo JSON usando a classe Resource do Spring
            Resource resource = new ClassPathResource(path);

            ClientTemplate clientTemplate = new ClientTemplate();
            clientTemplate.setName("Template padrão (fixo)");
            clientTemplate.setValue(false);

            try {
                clientTemplate.setTemplate(resource.getContentAsString(Charset.defaultCharset()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            clientTemplateRepository.save(clientTemplate);

        }
    }
}
