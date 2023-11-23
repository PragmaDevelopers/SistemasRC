package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.exception.EmailAlreadyExistsException;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.model.UserDetailsImpl;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.PasswordEncoderUtils;
import com.api.sistema_rc.util.TokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sql.rowset.serial.SerialBlob;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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
    private final Gson gson = new Gson();

    @PostMapping(path = "/public/signup")
    public ResponseEntity<String> signup(@RequestBody String body){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement name = jsonObj.get("name");
        if(name == null){
            errorMessage.addProperty("mensagem","O campo name é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement email = jsonObj.get("email");
        if(email == null){
            errorMessage.addProperty("mensagem","O campo email é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Optional<User> dbUser = userRepository.findByEmail(email.getAsString());
        if (dbUser.isPresent()) {
            throw new EmailAlreadyExistsException("O email já está cadastrado!");
        }

        JsonElement password = jsonObj.get("password");
        if(password == null){
            errorMessage.addProperty("mensagem","O campo password é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement nationality = jsonObj.get("nationality");
        if(nationality == null){
            errorMessage.addProperty("mensagem","O campo nationality é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement gender = jsonObj.get("gender");
        if(gender == null){
            errorMessage.addProperty("mensagem","O campo gender é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        User user = new User();

        user.setName(name.getAsString());
        user.setEmail(email.getAsString());
        user.setGender(gender.getAsString());

        String encryptedPassword = PasswordEncoderUtils.encode(password.getAsString());
        user.setPassword(encryptedPassword);

        user.setNationality(nationality.getAsString());
        user.setRegistration_date(LocalDate.now());

        Role role = new Role();
        role.setId(2);
        role.setName(RoleName.ROLE_PROFESSIONAL);

        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/public/login")
    public ResponseEntity<String> login(@RequestBody String body){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement email = jsonObj.get("email");
        if(email == null){
            errorMessage.addProperty("mensagem","O campo email é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement password = jsonObj.get("password");
        if(password == null){
            errorMessage.addProperty("mensagem","O campo password é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(email.getAsString(),password.getAsString());
        try {
            var auth = authenticationManager.authenticate(usernamePassword);
            String token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (AuthenticationException e) {
            errorMessage.addProperty("mensagem","Falha na autenticação: " + e.getMessage());
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
    }

    @Transactional
    @PatchMapping(path = "/private/user/profile")
    public ResponseEntity<String> patchUser(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();

        JsonElement name = jsonObj.get("name");
        if(name != null){
            user.setName(name.getAsString());
        }

        JsonElement email = jsonObj.get("email");
        if(email != null){
            Optional<User> dbUser = userRepository.findByEmail(email.getAsString());
            if (dbUser.isPresent()) {
                throw new EmailAlreadyExistsException("O email já está cadastrado!");
            }
            user.setEmail(email.getAsString());
        }

        JsonElement password = jsonObj.get("password");
        if(password != null){
            String encryptedPassword = PasswordEncoderUtils.encode(password.getAsString());
            user.setPassword(encryptedPassword);
        }

        JsonElement nationality = jsonObj.get("nationality");
        if(nationality != null){
            user.setNationality(nationality.getAsString());
        }

        JsonElement gender = jsonObj.get("gender");
        if(gender != null){
            user.setGender(gender.getAsString());
        }

        JsonElement profilePicture = jsonObj.get("profilePicture");
        if(profilePicture != null){
            byte[] decodedBlobData = Base64.getDecoder().decode(profilePicture.getAsString());
            Blob blob;
            try {
                blob = new SerialBlob(decodedBlobData);
                user.setProfilePicture(blob);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        JsonElement pushEmail = jsonObj.get("pushEmail");
        if(pushEmail != null){
            user.setPushEmail(pushEmail.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @GetMapping(path = "/private/user/profile")
    public ResponseEntity<String> getUser(@RequestHeader("Authorization") String token){
        Integer user_id = tokenService.validateToken(token);
        JsonObject errorMessage = new JsonObject();
        boolean isUser = userRepository.findById(user_id).isPresent();
        if(!isUser){
            errorMessage.addProperty("mensagem","Usuário não encontrado!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
        User user = userRepository.findById(user_id).get();
        JsonObject formattedUser = new JsonObject();
        formattedUser.addProperty("id",user.getId());
        formattedUser.addProperty("name",user.getName());
        formattedUser.addProperty("email",user.getEmail());
        formattedUser.addProperty("pushEmail",user.getPushEmail());
        formattedUser.addProperty("registration_date",user.getRegistration_date().toString());
        formattedUser.addProperty("nationality",user.getNationality());
        formattedUser.addProperty("gender",user.getGender());
        formattedUser.addProperty("role",user.getRole().getName().name());

        if(user.getProfilePicture() == null){
            formattedUser.addProperty("profilePicture",(String) null);
        }else{
            try {
                byte[] bytes = user.getProfilePicture().getBytes(1,(int) user.getProfilePicture().length());
                String encoded = Base64.getEncoder().encodeToString(bytes);
                formattedUser.addProperty("profilePicture",encoded);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(formattedUser.toString());
    }

    @GetMapping(path = "/private/users/search")
    public ResponseEntity<String> getUsersByName(@RequestParam(name = "name",required = false) String name,
                                                 @RequestParam(name = "email",required = false) String email,
                                                 @RequestParam(name = "page",required = false,defaultValue = "1") Integer page){
        List<User> userList;
        if(name != null){
            userList = userRepository.findAllByName(name,10 * (page - 1));
        }else if(email != null){
            userList = userRepository.findAllByEmail(email,10 * (page - 1));
        }else{
            userList = userRepository.findAll(10 * (page - 1));
        }
        JsonArray users = new JsonArray();
        userList.forEach(user->{
            JsonObject formattedUser = new JsonObject();
            formattedUser.addProperty("id",user.getId());
            formattedUser.addProperty("name",user.getName());
            formattedUser.addProperty("email",user.getEmail());
            formattedUser.addProperty("pushEmail",user.getPushEmail());
            formattedUser.addProperty("registrationDate",user.getRegistration_date().toString());
            formattedUser.addProperty("nationality",user.getNationality());
            formattedUser.addProperty("gender",user.getGender());
            formattedUser.addProperty("role",user.getRole().getName().name());

            if(user.getProfilePicture() == null){
                formattedUser.addProperty("profilePicture",(String) null);
            }else{
                try {
                    byte[] bytes = user.getProfilePicture().getBytes(1,(int) user.getProfilePicture().length());
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    formattedUser.addProperty("profilePicture",encoded);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            users.add(formattedUser);
        });
        return ResponseEntity.status(HttpStatus.OK).body(users.toString());
    }
}
