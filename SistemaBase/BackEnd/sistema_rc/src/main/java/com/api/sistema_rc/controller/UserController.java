package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.exception.EmailAlreadyExistsException;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.model.UserDetailsImpl;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.PasswordEncoderUtils;
import com.api.sistema_rc.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping(path = "/public/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody User user){
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser.isPresent()) {
            throw new EmailAlreadyExistsException("O email já existe!");
        }
        // Se o email não existe, continue com o registro do usuário.
        user.setRegistration_date(LocalDate.now());
        String encryptedPassword = PasswordEncoderUtils.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setId(2);
        role.setName(RoleName.ROLE_PROFESSIONAL);
        user.setRole(role);

        userRepository.save(user);
    }

    @PostMapping(path = "/public/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody User user){
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
        try {
            var auth = authenticationManager.authenticate(usernamePassword);
            String token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());
            return token;
        } catch (AuthenticationException e) {
            return "Falha na autenticação: " + e.getMessage();
        }
    }

    @GetMapping(path = "/private/clients")
    public List<User> getClients(){
        return userRepository.findAll();
    }
}
