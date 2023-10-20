package com.api.sistema_rc.controller;

import com.api.sistema_rc.enums.RoleName;
import com.api.sistema_rc.model.Role;
import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.UserRepository;
import com.api.sistema_rc.util.PasswordEncoderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/p/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody User user){
        user.setDate_of_birth(LocalDate.now());
        if(Objects.equals(user.getNickname(), "")){
            user.setNickname(null);
        }
        String encryptedPassword = PasswordEncoderUtils.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setId(2);
        role.setName(RoleName.CLIENT);
        user.setRole(role);
//
//        Usuario usuarioDb = usuarioRepository.saveAndFlush(usuario);
//
//        ProfilePicture picture_default = profilePictureRepository.findByNome("default");
//        ProfilePicture picture_book = profilePictureRepository.findByNome("book");
//
//        UsuarioProfilePicture usuarioProfilePicture_1 = new UsuarioProfilePicture();
//        usuarioProfilePicture_1.setActive(true);
//        usuarioProfilePicture_1.setProfilePicture(picture_default);
//        usuarioProfilePicture_1.setUsuario(usuarioDb);
//
//        UsuarioProfilePicture usuarioProfilePicture_2 = new UsuarioProfilePicture();
//        usuarioProfilePicture_2.setActive(false);
//        usuarioProfilePicture_2.setProfilePicture(picture_book);
//        usuarioProfilePicture_2.setUsuario(usuarioDb);
//
//        List<UsuarioProfilePicture> usuarioProfilePictureList = new ArrayList<>();
//        usuarioProfilePictureList.add(usuarioProfilePicture_1);
//        usuarioProfilePictureList.add(usuarioProfilePicture_2);
//
//        usuarioprofilePictureRepository.saveAll(usuarioProfilePictureList);

    }

//    @GetMapping("/edit/{id}")
//    public String edit(Model model, @PathVariable Integer id){
//        Optional<User> usuario = userRepository.findById(id);
//        usuario.ifPresent(value -> model.addAttribute("usuario", value));
//        return "admin/interface/editar-usuario";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable Integer id){
//        userRepository.deleteById(id);
//        return "redirect:/admin/usuario/select";
//    }
//
//    @PostMapping("/insert")
//    public String insert(@ModelAttribute Usuario usuario,@RequestParam("cargo") Integer id_cargo,@RequestParam("genero") String genero){
//        usuario.setData_cadastro(LocalDate.now());
//        if(Objects.equals(usuario.getApelido(), "")){
//            usuario.setApelido(null);
//        }
//        String senhaEncriptada = PasswordEncoderUtils.encode(usuario.getSenha());
//        usuario.setSenha(senhaEncriptada);
//
//        Cargo cargo = new Cargo();
//        cargo.setId(id_cargo);
//        if(id_cargo == 1){
//            cargo.setNome(RoleName.ROLE_ADMIN);
//        }else{
//            cargo.setNome(RoleName.ROLE_USER);
//        }
//        usuario.setCargo(cargo);
//        usuario.setGenero(genero);
//        userRepository.save(usuario);
//        return "redirect:/admin/usuario/select";
//    }
//
//    @PostMapping("/update")
//    public String update(@ModelAttribute Usuario usuario,@RequestParam("cargo") Integer id_cargo){
//        if(Objects.equals(usuario.getApelido(), "")){
//            usuario.setApelido(null);
//        }
//        String senhaEncriptada = PasswordEncoderUtils.encode(usuario.getSenha());
//        usuario.setSenha(senhaEncriptada);
//
//        Cargo cargo = new Cargo();
//        cargo.setId(id_cargo);
//        if(id_cargo == 1){
//            cargo.setNome(RoleName.ROLE_ADMIN);
//        }else{
//            cargo.setNome(RoleName.ROLE_USER);
//        }
//        usuario.setCargo(cargo);
//        userRepository.save(usuario);
//        return "redirect:/admin/usuario/select";
//    }
}
