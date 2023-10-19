package com.advogaciaapi.controller;

import br.com.alquimiadaspalavras.enums.RoleName;
import br.com.alquimiadaspalavras.model.Cargo;
import br.com.alquimiadaspalavras.model.Usuario;
import br.com.alquimiadaspalavras.repository.UsuarioRepository;
import br.com.alquimiadaspalavras.utils.PasswordEncoderUtils;
import com.advogaciaapi.model.User;
import com.advogaciaapi.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping(path = "/admin/usuario")
public class UserController {

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(path = "/select")
    public String select(ModelMap modelMap){
        modelMap.addAttribute("user",new User());
        List<User> users = userRepository.findAll();
        modelMap.addAttribute("users",users);
        return "admin/interface/user";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Integer id){
        Optional<Usuario> usuario = userRepository.findById(id);
        usuario.ifPresent(value -> model.addAttribute("usuario", value));
        return "admin/interface/editar-usuario";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        userRepository.deleteById(id);
        return "redirect:/admin/usuario/select";
    }

    @PostMapping("/insert")
    public String insert(@ModelAttribute Usuario usuario,@RequestParam("cargo") Integer id_cargo,@RequestParam("genero") String genero){
        usuario.setData_cadastro(LocalDate.now());
        if(Objects.equals(usuario.getApelido(), "")){
            usuario.setApelido(null);
        }
        String senhaEncriptada = PasswordEncoderUtils.encode(usuario.getSenha());
        usuario.setSenha(senhaEncriptada);

        Cargo cargo = new Cargo();
        cargo.setId(id_cargo);
        if(id_cargo == 1){
            cargo.setNome(RoleName.ROLE_ADMIN);
        }else{
            cargo.setNome(RoleName.ROLE_USER);
        }
        usuario.setCargo(cargo);
        usuario.setGenero(genero);
        userRepository.save(usuario);
        return "redirect:/admin/usuario/select";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Usuario usuario,@RequestParam("cargo") Integer id_cargo){
        if(Objects.equals(usuario.getApelido(), "")){
            usuario.setApelido(null);
        }
        String senhaEncriptada = PasswordEncoderUtils.encode(usuario.getSenha());
        usuario.setSenha(senhaEncriptada);

        Cargo cargo = new Cargo();
        cargo.setId(id_cargo);
        if(id_cargo == 1){
            cargo.setNome(RoleName.ROLE_ADMIN);
        }else{
            cargo.setNome(RoleName.ROLE_USER);
        }
        usuario.setCargo(cargo);
        userRepository.save(usuario);
        return "redirect:/admin/usuario/select";
    }
}
