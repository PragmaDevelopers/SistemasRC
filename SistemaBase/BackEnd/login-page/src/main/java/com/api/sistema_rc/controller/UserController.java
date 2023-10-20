package com.api.sistema_rc.controller;

import com.api.sistema_rc.model.User;
import com.api.sistema_rc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/p/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@ModelAttribute User user){
        System.out.println(user.getEmail());
        return "admin/interface/user";
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
