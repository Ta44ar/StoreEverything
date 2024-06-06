package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Repositories.RoleRepository;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin/users")
    public String adminPage(Model model) {
        List<UserEntity> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin";
    }
}
