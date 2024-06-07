package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Services.CategoryService;
import com.niedzwiecki_syperek.StoreEverything.Services.CustomUserDetailsService;
import com.niedzwiecki_syperek.StoreEverything.Services.InformationService;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import jakarta.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @GetMapping("/my-informations")
    public String viewMyInformations(Model model) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        List<Information> myInformations = informationService.findByUserId(currentUserId);
        model.addAttribute("myInformations", myInformations);
        return "myInformations";
    }

    @GetMapping("/information/add")
    public String showAddInfoForm(Model model) {
        model.addAttribute("information", new Information());
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "addInfoForm";
    }

    @PostMapping("/information/add")
    public String addInformation(@Valid @ModelAttribute("information") Information info, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addFlashAttribute("successMessage", "Information added successfully!");
        return "redirect:/my-informations";
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
    public String updateInformation(@Valid @ModelAttribute("information") Information info, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        Information existingInfo = informationService.findById(info.getId());
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        if (!existingInfo.getUserEntity().getId().equals(currentUserId)) {
            return "error/403";
        }

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

        redirectAttributes.addFlashAttribute("successMessage", "Information updated successfully!");
        return "redirect:/my-informations";
    }

    @GetMapping("/information/delete/{id}")
    public String deleteInformation(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Information info = informationService.findById(id);
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        if (!info.getUserEntity().getId().equals(currentUserId)) {
            return "error/403";
        }
        informationService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Information deleted successfully!");
        return "redirect:/my-informations";
    }

    @GetMapping("/information/share/{id}")
    public String shareInformation(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        String shareableLink = informationService.generateShareableLink(id);
        model.addAttribute("shareableLink", shareableLink);
        redirectAttributes.addFlashAttribute("successMessage", "Information shared successfully!");
        return "redirect:/my-informations";
    }

    @GetMapping("/information/shared/{shareableLink}")
    public String viewSharedInformation(@PathVariable("shareableLink") String shareableLink, Model model) {
        Information information = informationService.findByShareableLink(shareableLink);
        model.addAttribute("information", information);
        return "viewSharedInfo";
    }

    @GetMapping("/information/share-with-user/{id}")
    public String shareInformationWithUserForm(@PathVariable("id") Long id, Model model) {
        Information information = informationService.findById(id);

        model.addAttribute("information", information);
        model.addAttribute("users", customUserDetailsService.findAllUsersToShareInformation(information.getId(), customUserDetailsService.getCurrentUser().getId()));
        return "shareInfoWithUser";
    }

    @PostMapping("/information/share-with-user")
    public String shareInformationWithUser(@RequestParam("informationId") Long informationId, @RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        try {
            informationService.shareInformationWithUser(informationId, userId, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Information shared for user successfully!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-informations";
    }

    @GetMapping("/information/view/{id}")
    public String viewInformation(@PathVariable("id") Long id, Model model) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        Information information = informationService.findById(id);

        if (!information.getUserEntity().getId().equals(currentUserId) &&
                information.getSharedWithUsers().stream().noneMatch(user -> user.getId().equals(currentUserId))) {
            return "error/403";
        }

        model.addAttribute("information", information);
        model.addAttribute("currentUserId", currentUserId);
        return "viewInfo";
    }

    @GetMapping("/information/shared-with-me")
    public String viewSharedWithMeInformations(Model model) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        List<Information> sharedWithMeInformations = informationService.findSharedWithMeInfos(currentUserId);
        model.addAttribute("sharedWithMeInformations", sharedWithMeInformations);
        return "sharedInformations";
    }

}