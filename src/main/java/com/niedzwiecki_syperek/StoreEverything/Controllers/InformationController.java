package com.niedzwiecki_syperek.StoreEverything.Controllers;

import com.niedzwiecki_syperek.StoreEverything.Services.CategoryService;
import com.niedzwiecki_syperek.StoreEverything.Services.CustomUserDetailsService;
import com.niedzwiecki_syperek.StoreEverything.Services.InformationService;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Base64;
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
    public String viewMyInformations(
            @RequestParam(required = false, name = "categoryId") Long categoryId,
            @RequestParam(required = false, name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CookieValue(value = "savedOrder", defaultValue = "") String encodedOrder,
            Model model) {
        if (!encodedOrder.isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedOrder);
            String decodedOrder = new String(decodedBytes);
            model.addAttribute("savedOrder", decodedOrder);
        } else {
            model.addAttribute("savedOrder", "");
        }
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        List<Information> myInformations = informationService.findByUserIdWithFilters(currentUserId, categoryId, startDate, endDate);
        model.addAttribute("myInformations", myInformations);
        model.addAttribute("categories", categoryService.findAllSortedByPopularity());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "myInformations";
    }

    @GetMapping("/my-informations/save-order")
    public String saveOrder(@RequestParam("order") String order, HttpServletResponse response) {
        String encodedOrder = java.util.Base64.getEncoder().encodeToString(order.getBytes());
        Cookie cookie = new Cookie("savedOrder", encodedOrder);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        return "redirect:/my-informations";
    }

    @GetMapping("/my-informations/clear-order")
    public String clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("savedOrder", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/my-informations";
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            currentUserId = customUserDetailsService.getCurrentUser().getId();
        }
        Information information = informationService.findByShareableLink(shareableLink);
        model.addAttribute("information", information);
        model.addAttribute("currentUserId", currentUserId);
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
    public String viewSharedWithMeInformations(Model model,
                                               @RequestParam(required = false, name = "categoryId") Long categoryId,
                                               @RequestParam(required = false, name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam(required = false, name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long currentUserId = customUserDetailsService.getCurrentUser().getId();
        List<Information> sharedWithMeInformations = informationService.findSharedWithMeInfos(currentUserId, categoryId, startDate, endDate);
        model.addAttribute("sharedWithMeInformations", sharedWithMeInformations);
        model.addAttribute("categories", categoryService.findAllSortedByPopularity());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "sharedInformations";
    }

}