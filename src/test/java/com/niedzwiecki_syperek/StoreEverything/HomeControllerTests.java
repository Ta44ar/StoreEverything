package com.niedzwiecki_syperek.StoreEverything;

import com.niedzwiecki_syperek.StoreEverything.Controllers.HomeController;
import com.niedzwiecki_syperek.StoreEverything.Repositories.UserRepository;
import com.niedzwiecki_syperek.StoreEverything.db.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void testShowHomePage() throws Exception {
        UserEntity user = new UserEntity();
        user.setFirstName("John");

        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("first_name"));
    }
}
