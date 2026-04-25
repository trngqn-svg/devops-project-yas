package com.yas.backofficebff.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUser_whenAuthenticated_returnsUser() throws Exception {
        mockMvc.perform(
                get("/authentication/user")
                    .with(oauth2Login().attributes(attrs -> {
                        attrs.put("preferred_username", "admin");
                    }))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }
}
