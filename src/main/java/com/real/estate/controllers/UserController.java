package com.real.estate.controllers;

import com.real.estate.constants.Role;
import com.real.estate.datas.Notification;
import com.real.estate.models.User;
import com.real.estate.repositories.UserRepository;
import com.university.repositories.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping(value="list")
    public String getAll(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/user/list";
    }

    @GetMapping(value="view/{id}")
    public String viewUser(@PathVariable long id, Model model){
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "admin/user/view";
    }

    @RequestMapping(value="delete/{id}")
    public RedirectView deleteUser(@PathVariable long id, RedirectAttributes attributes){
        Notification notification = new Notification();
        try {
            Optional<User> user = userRepository.findById(id);
            if(user.isPresent()){
                userRepository.deleteById(id);
            }
            notification.setType("success");
            notification.setMessage("1 utilisateur supprimé avec succès");
        }catch (Exception e){
            notification.setType("error");
            notification.setMessage(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
        attributes.addFlashAttribute("notification", notification);
        return new RedirectView("/user/list", true);
    }

    @GetMapping(value = "save")
    private String getUser(@RequestParam(required = false, defaultValue = "-1") long id, Model model){
        boolean creation = true;
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            model.addAttribute("user", user.get());
            creation = false;
        }
        model.addAttribute("creation", creation);
        model.addAttribute("roles", Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList()));
        return "admin/user/save";
    }

    @PostMapping(value = "save")
    public String saveUser(User user, @RequestParam List<String> authorities, @RequestParam(required = false, defaultValue = "false") Boolean multiple, RedirectAttributes attributes, Model model){
        User user$ = user;
        boolean creation = true;
        if(user.getId() != null){
            Optional<User> _user = userRepository.findById(user.getId());
            if(_user.isPresent()){
                user$ = _user.get();
                user$.setFirstName(user.getFirstName());
                user$.setLastName(user.getLastName());
                user$.setEmail(user.getEmail());
                user$.setTelephone(user.getTelephone());
                user$.setEnabled(user.getEnabled());
                creation = false;
            }
        }
        if(creation){
            user$.setUsername(user.getEmail());
            user$.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }
        user$.setRoles(String.join(";", authorities));
        user$.normalize();
        Notification notification = new Notification();
        try {
            userRepository.save(user$);
            notification.setType("success");
            notification.setMessage(creation ? "1 utilisateur ajouté avec succès" : "1 utilisateur modifié avec succès");
            creation = true;
            user$ = new User();
        } catch (Exception e){
            notification.setType("error");
            notification.setMessage(e.getCause().getMessage());
        }

        if(multiple || "error".equalsIgnoreCase(notification.getType())){
            model.addAttribute("notification", notification);
            model.addAttribute("user", user$);
            model.addAttribute("roles", Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList()));
            model.addAttribute("creation", creation);
            return "admin/user/save";
        }else{
            attributes.addFlashAttribute("notification", notification);
        }
        return "redirect:/user/list";
    }
}
