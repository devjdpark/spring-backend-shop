package com.example.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.controller.UserController;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;

@SuppressWarnings("removal")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void me_endpoint_returns_minimal_shape() throws Exception {
        User mockUser = new User();
        mockUser.setUserId("user@test.com");
        mockUser.setName("一般ユーザ");
        mockUser.setStaff(false);
        mockUser.setSuperUser(false);

        Mockito.when(userService.findByUserId("user@test.com")).thenReturn(mockUser);

        mvc.perform(get("/api/users/me"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("一般ユーザ"))
           .andExpect(jsonPath("$.isStaff").value(false))
           .andExpect(jsonPath("$.superUser").value(false));
    }
}
