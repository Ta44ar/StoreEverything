package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    UserRepository userRepository;

    @Autowired
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String showHomePage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            UserEntity user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                model.addAttribute("first_name", user.getFirstName());
            }
        }
        return "home";
    }
}
