package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Services.CategoryService;
import com.niedzwiecki_syperek.StoreEverything.Services.CustomUserDetailsService;
import com.niedzwiecki_syperek.StoreEverything.Services.InformationService;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import jakarta.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class InformationController {

    @Autowired
    private InformationService informationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private SmartValidator validator;

    @GetMapping("/information/add")
    public String showAddInfoForm(Model model) {
        model.addAttribute("information", new Information());
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "addInfoForm";
    }

    @PostMapping("/information/add")
    public String addInformation(@Valid @ModelAttribute("information") Information info, BindingResult result, Model model) {
        info.setUserEntity(customUserDetailsService.getCurrentUser());
        info.setDateAdded(LocalDate.now());

        BindingResult reValidationResult = new BeanPropertyBindingResult(info, "information");
        validator.validate(info, reValidationResult);

        if (reValidationResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "addInfoForm";
        }
        informationService.save(info);
        return "redirect:/";
    }

    @GetMapping("/information/edit/{id}")
    public String showEditInfoForm(@PathVariable("id") Long id, Model model) {
        Information info = informationService.findById(id);
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        if (!info.getUserEntity().getId().equals(currentUserId)) {
            return "error/403";
        }
        model.addAttribute("information", info);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "editInfoForm";
    }

    @PostMapping("/information/update")
    public String updateInformation(@Valid @ModelAttribute("information") Information info, BindingResult result, Model model) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        if (!info.getUserEntity().getId().equals(currentUserId)) {
            return "error/403";
        }
        Information existingInfo = informationService.findById(info.getId());
        info.setUserEntity(existingInfo.getUserEntity());
        info.setDateAdded(existingInfo.getDateAdded());

        BindingResult reValidationResult = new BeanPropertyBindingResult(info, "information");
        validator.validate(info, reValidationResult);

        if (reValidationResult.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "editInfoForm";
        }
        informationService.update(info);
        return "redirect:/";
    }

    @GetMapping("/information/delete/{id}")
    public String deleteInformation(@PathVariable("id") Long id) {
        Information info = informationService.findById(id);
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        if (!info.getUserEntity().getId().equals(currentUserId)) {
            return "error/403";
        }
        informationService.delete(id);
        return "redirect:/";
    }
}
