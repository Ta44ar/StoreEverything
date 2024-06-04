package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Repositories.RoleRepository;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Role;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserEntity());
        model.addAttribute("error", false);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserEntity user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", true);
            return "register";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", true);
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/";
    }
}
