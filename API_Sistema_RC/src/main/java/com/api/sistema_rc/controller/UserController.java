package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.exception.EmailAlreadyExistsException;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.model.UserDetailsImpl;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.service.MailService;
import com.api.sistema_rc.service.UserDetailsServiceImpl;
import com.api.sistema_rc.util.CodeService;
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
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(path = "/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CodeService codeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailService mailService;
    @Autowired
    private Gson gson;

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
            errorMessage.addProperty("mensagem","O email já está cadastrado!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
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
        user.setRegistration_date(LocalDateTime.now());
        user.setPermissionLevel("000000000000000000000000000000000000000");

        Role role = new Role();
        role.setId(3);
        role.setName(RoleName.ROLE_MEMBER);

        user.setRole(role);

        String code = codeService.generateUserCodeVerification(10);

        user.setCodeToVerify(code);
        user.setVerify(false);
        user.setReceiveNotification(false);

        userRepository.save(user);

        String codeToken = tokenService.generateCodeToken(code);

        mailService.sendMailWithoutVerification(user.getEmail(),"Verificação de cadastro em Rafael do Canto Advocacia e Socidade",
            "Para verificar sua conta, clique no link: https://sistemasdocanto.vercel.app/account/verify?code="+codeToken
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
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
        var auth = (UserDetailsImpl) authenticationManager.authenticate(usernamePassword).getPrincipal();

        if(!auth.isVerify()){
            String code = codeService.generateUserCodeVerification(10);
            userRepository.findByEmail(email.getAsString()).get().setCodeToVerify(code);
            String codeToken = tokenService.generateCodeToken(code);
            mailService.sendMailWithoutVerification(email.getAsString(),"Verificação de cadastro em Rafael do Canto Advocacia e Socidade",
                    "Para verificar sua conta, clique no link: https://sistemasdocanto.vercel.app/account/verify?code="+codeToken
            );
            errorMessage.addProperty("mensagem","Email não verificado!");
            errorMessage.addProperty("status",420);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        String token = tokenService.generateUserToken(auth);
        JsonObject tokenObject = new JsonObject();
        tokenObject.addProperty("token",token);

        return ResponseEntity.status(HttpStatus.OK).body(tokenObject.toString());
    }

    @Transactional
    @PatchMapping(path = "/public/user/verify/{code}")
    public ResponseEntity<String> userVerify(@PathVariable String code) {
        String formattedCode = tokenService.validateCodeToken(code);
        Optional<User> user = userRepository.findByCodeToVerify(formattedCode);
        if(user.isPresent()){
            user.get().setVerify(true);
            user.get().setCodeToVerify(null);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping(path = "/public/user/new/password")
    public ResponseEntity<String> newPassword(@RequestBody String body) {
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement email = jsonObj.get("email");
        if(email == null){
            errorMessage.addProperty("mensagem","O campo email é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        Optional<User> user = userRepository.findByEmail(email.getAsString());
        if(user.isEmpty()){
            errorMessage.addProperty("mensagem","Email não encontrado!");
            errorMessage.addProperty("status",404);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user.get());
        String token = tokenService.generateUserToken(userDetails);

        mailService.sendMail(email.getAsString(),"Redefinição de senha em Rafael do Canto Advocacia e Socidade",
                "Para redefinir sua senha, clique no link: https://sistemasdocanto.vercel.app/account/redefine?token="+token
        );

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/public/user/new/email")
    public ResponseEntity<String> newEmail(@RequestBody String body) {
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        JsonObject errorMessage = new JsonObject();

        JsonElement code = jsonObj.get("code");
        if(code == null){
            errorMessage.addProperty("mensagem","O campo code é necessário!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        String formattedCode = tokenService.validateCodeToken(code.getAsString());
        Optional<User> user = userRepository.findByCodeToSwitch(formattedCode);
        if(user.isEmpty()){
            errorMessage.addProperty("mensagem","O code é inválida!");
            errorMessage.addProperty("status",400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        user.get().setEmail(user.get().getEmailToSwitch());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/{userId}/config")
    public ResponseEntity<String> configUserPermission(@PathVariable Integer userId,@RequestBody String body,@RequestHeader("Authorization") String token) {
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer yourUserId = tokenService.validateToken(token);
        User yourUser = userRepository.findById(yourUserId).get();

        JsonObject errorMessage = new JsonObject();

        if(yourUserId.equals(userId)){
            errorMessage.addProperty("mensagem","Você não pode modificar o seu próprio perfil!");
            errorMessage.addProperty("status",405);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        boolean isTargetUser = userRepository.findById(userId).isPresent();
        if(!isTargetUser){
            errorMessage.addProperty("mensagem","Usuário não encontrado!");
            errorMessage.addProperty("status",414);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        User targetUser = userRepository.findById(userId).get();

        String yourRole = yourUser.getRole().getName().name();
        String targetRole = targetUser.getRole().getName().name();

        if(yourRole.equals("ROLE_MEMBER")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (definir as permissões)!");
            errorMessage.addProperty("status",405);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        if(yourRole.equals("ROLE_SUPERVISOR")){
            if(targetRole.equals("ROLE_SUPERVISOR")){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar as permissões de outro supervisor)!");
                errorMessage.addProperty("status",405);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            if(targetRole.equals("ROLE_ADMIN")){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar as permissões de um admin)!");
                errorMessage.addProperty("status",405);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
        }

        if(yourRole.equals("ROLE_ADMIN") && targetRole.equals("ROLE_ADMIN")){
            errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (Mudar as permissões de outro admin)!");
            errorMessage.addProperty("status",405);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        JsonElement isSupervisor = jsonObj.get("isSupervisor");
        if(isSupervisor != null){
            if(!yourUser.getRole().getName().name().equals("ROLE_ADMIN")){
                errorMessage.addProperty("mensagem","Você não tem autorização para essa ação (mudar o cargo de alguém)!");
                errorMessage.addProperty("status",405);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            if(isSupervisor.getAsBoolean()){
                Role role = new Role();
                role.setId(2);
                role.setName(RoleName.ROLE_SUPERVISOR);
                targetUser.setRole(role);
            }else{
                Role role = new Role();
                role.setId(3);
                role.setName(RoleName.ROLE_MEMBER);
                targetUser.setRole(role);
            }
        }

        JsonElement permissionLevel = jsonObj.get("permissionLevel");
        if(permissionLevel != null){
            if(permissionLevel.getAsString().split("").length != 39){
                errorMessage.addProperty("mensagem","O permissionLevel precisa ter 39 caracteres!");
                errorMessage.addProperty("status",400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            targetUser.setPermissionLevel(permissionLevel.getAsString());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @PatchMapping(path = "/private/user/profile")
    public ResponseEntity<String> patchUser(@RequestBody String body,@RequestHeader("Authorization") String token){
        JsonObject jsonObj = gson.fromJson(body, JsonObject.class);

        Integer user_id = tokenService.validateToken(token);
        User user = userRepository.findById(user_id).get();

        JsonObject errorMessage = new JsonObject();

        JsonElement name = jsonObj.get("name");
        if(name != null){
            user.setName(name.getAsString());
        }

        JsonElement email = jsonObj.get("email");
        if(email != null && !Objects.equals(user.getEmail(),email.getAsString())){
            Optional<User> dbUser = userRepository.findByEmail(email.getAsString());
            if (dbUser.isPresent()) {
                errorMessage.addProperty("mensagem","O email já está cadastrado!");
                errorMessage.addProperty("status",420);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            String code = codeService.generateUserCodeSwitch(10);
            user.setCodeToSwitch(code);
            user.setEmailToSwitch(email.getAsString());
            String codeToken = tokenService.generateCodeToken(code);
            mailService.sendMailWithoutVerification(email.getAsString(),"Verificação de troca de email em Rafael do Canto Advocacia e Socidade",
                    "Para verificar troca de email da sua conta, clique no link: https://sistemasdocanto.vercel.app/account/switch?code="+codeToken
            );
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
            String imageFormat = extractImageFormat(profilePicture.getAsString());
            if(imageFormat == null){
                errorMessage.addProperty("mensagem","Não foi possivel encontrar o formato da imagem entre data:image/???;base64,");
                errorMessage.addProperty("status",400);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
            }
            user.setPictureFormat(imageFormat);

            String base64Data = profilePicture.getAsString().replaceAll("data:image\\/.*;base64,", "");
            byte[] decodedBlobData = Base64.getDecoder().decode(base64Data);
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

        JsonElement receiveNotification = jsonObj.get("isReceiveNotification");
        if(receiveNotification != null){
            user.setReceiveNotification(receiveNotification.getAsBoolean());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public static String extractImageFormat(String base64ImageData) {
        // Encontre a parte da string que indica o formato da imagem
        int startIndex = base64ImageData.indexOf("data:image/") + "data:image/".length();
        int endIndex = base64ImageData.indexOf(";base64,");

        if (startIndex != -1 && endIndex != -1) {
            return base64ImageData.substring(startIndex, endIndex);
        }

        // Se não for possível extrair o formato, retorne null ou uma string indicativa de erro
        return null;
    }

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
        formattedUser.addProperty("permissionLevel",user.getPermissionLevel());
        formattedUser.addProperty("isReceiveNotification",user.isReceiveNotification());
        if(user.getProfilePicture() == null){
            formattedUser.addProperty("profilePicture",(String) null);
        }else{
            try {
                byte[] bytes = user.getProfilePicture().getBytes(1,(int) user.getProfilePicture().length());
                String encoded = Base64.getEncoder().encodeToString(bytes);
                formattedUser.addProperty("profilePicture","data:image/"+user.getPictureFormat()+";base64,"+encoded);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(formattedUser.toString());
    }

    @GetMapping(path = "/private/user/search")
    public ResponseEntity<String> getUsersByName(@RequestParam(name = "name",required = false) String name,
                                                 @RequestParam(name = "email",required = false) String email,
                                                 @RequestParam(name = "id",required = false) Integer id,
                                                 @RequestParam(name = "page",required = false,defaultValue = "1") Integer page){
        List<User> userList;
        if(name != null){
            userList = userRepository.findAllByName(name,10 * (page - 1));
        }else if(email != null){
            userList = userRepository.findAllByEmail(email,10 * (page - 1));
        }else if(id != null){
            userList = new ArrayList<>();
            if(userRepository.findById(id).isPresent()){
                userList.add(userRepository.findById(id).get());
            }
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
            formattedUser.addProperty("permissionLevel",user.getPermissionLevel());
            formattedUser.addProperty("isReceiveNotification",user.isReceiveNotification());
            if(user.getProfilePicture() == null){
                formattedUser.addProperty("profilePicture",(String) null);
            }else{
                try {
                    byte[] bytes = user.getProfilePicture().getBytes(1,(int) user.getProfilePicture().length());
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    formattedUser.addProperty("profilePicture","data:image/"+user.getPictureFormat()+";base64,"+encoded);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            users.add(formattedUser);
        });
        return ResponseEntity.status(HttpStatus.OK).body(users.toString());
    }
}
