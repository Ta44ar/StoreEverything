package com.niedzwiecki_syperek.StoreEverything;

import com.niedzwiecki_syperek.StoreEverything.Controllers.InformationController;
import com.niedzwiecki_syperek.StoreEverything.Services.CategoryService;
import com.niedzwiecki_syperek.StoreEverything.Services.CustomUserDetailsService;
import com.niedzwiecki_syperek.StoreEverything.Services.InformationService;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Information;
import com.niedzwiecki_syperek.StoreEverything.db.entities.Category;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InformationController.class)
public class InformationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InformationService informationService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private SmartValidator validator;

    @Test
    @WithMockUser
    void testViewMyInformationsWithLoggedInUser() throws Exception {
        when(customUserDetailsService.getCurrentUser()).thenReturn(new UserEntity());
        when(informationService.findByUserIdWithFilters(anyLong(), any(), any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/my-informations"))
                .andExpect(status().isOk())
                .andExpect(view().name("myInformations"))
                .andExpect(model().attributeExists("myInformations"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void testViewMyInformationsWithoutLoggedInUser() throws Exception {
        when(customUserDetailsService.getCurrentUser()).thenReturn(new UserEntity());
        when(informationService.findByUserIdWithFilters(anyLong(), any(), any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/my-informations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testViewMyInformationsParams() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(customUserDetailsService.getCurrentUser()).thenReturn(user);

        when(informationService.findByUserIdWithFilters(eq(user.getId()), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(categoryService.findAllSortedByPopularity())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/my-informations")
                        .param("categoryId", "1")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31")
                        .cookie(new Cookie("savedOrder", "dGVzdA==")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("myInformations", Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.model().attribute("categories", Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.model().attribute("savedOrder", "test"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedCategoryId", 1L))
                .andExpect(MockMvcResultMatchers.model().attribute("startDate", LocalDate.parse("2023-01-01")))
                .andExpect(MockMvcResultMatchers.model().attribute("endDate", LocalDate.parse("2023-12-31")))
                .andExpect(MockMvcResultMatchers.view().name("myInformations"));

        verify(informationService).findByUserIdWithFilters(user.getId(), 1L, LocalDate.parse("2023-01-01"), LocalDate.parse("2023-12-31"));
    }

    @Test
    @WithMockUser
    void testShowAddInfoForm() throws Exception {
        when(categoryService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/information/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("addInfoForm"))
                .andExpect(model().attributeExists("information"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser
    void testAddInformation() throws Exception {
        Information info = new Information();
        info.setTitle("Test Title");
        info.setContent("Test Content");

        when(customUserDetailsService.getCurrentUser()).thenReturn(new UserEntity());

        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        doNothing().when(validator).validate(any(), any());

        mockMvc.perform(post("/information/add").with(csrf())
                        .flashAttr("information", info))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-informations"))
                .andExpect(flash().attribute("successMessage", "Information added successfully!"));
    }

    @Test
    @WithMockUser
    void testShowEditInfoForm() throws Exception {
        Long infoId = 1L;
        Information info = new Information();
        info.setId(infoId);
        UserEntity user = new UserEntity();
        user.setId(1L);
        info.setUserEntity(user);

        when(informationService.findById(infoId)).thenReturn(info);
        when(customUserDetailsService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/information/edit/{id}", infoId))
                .andExpect(status().isOk())
                .andExpect(view().name("editInfoForm"))
                .andExpect(model().attribute("information", info))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser
    void testUpdateInformation() throws Exception {
        Long infoId = 1L;
        Information info = new Information();
        info.setId(infoId);
        UserEntity user = new UserEntity();
        user.setId(1L);
        info.setUserEntity(user);

        when(informationService.findById(infoId)).thenReturn(info);
        when(customUserDetailsService.getCurrentUser()).thenReturn(user);

        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        doNothing().when(validator).validate(any(), any());

        mockMvc.perform(post("/information/update").with(csrf())
                        .flashAttr("information", info))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-informations"))
                .andExpect(flash().attribute("successMessage", "Information updated successfully!"));
    }

    @Test
    @WithMockUser
    void testDeleteInformation() throws Exception {
        Long infoId = 1L;
        Information info = new Information();
        info.setId(infoId);
        UserEntity user = new UserEntity();
        user.setId(1L);
        info.setUserEntity(user);

        when(informationService.findById(infoId)).thenReturn(info);
        when(customUserDetailsService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/information/delete/{id}", infoId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-informations"))
                .andExpect(flash().attribute("successMessage", "Information deleted successfully!"));
    }

    @Test
    @WithMockUser
    void testViewInformation() throws Exception {
        Long infoId = 1L;
        Information information = new Information();
        information.setId(infoId);
        UserEntity user = new UserEntity();
        user.setId(1L);
        information.setUserEntity(user);
        Category category = new Category();
        category.setName("category1");
        information.setCategory(category);

        when(informationService.findById(infoId)).thenReturn(information);
        when(customUserDetailsService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/information/view/{id}", infoId))
                .andExpect(status().isOk())
                .andExpect(view().name("viewInfo"))
                .andExpect(model().attribute("information", information));
    }

}
