package com.yas.backofficebff.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.mockito.Mockito;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUser_whenAuthenticated_returnsUser() throws Exception {
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin");

        mockMvc.perform(get("/authentication/user")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(principal)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }
}
