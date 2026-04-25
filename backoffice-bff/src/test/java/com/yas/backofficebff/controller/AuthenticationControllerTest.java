package com.yas.backofficebff.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testUser_whenAuthenticated_returnsUser() {
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin");

        webTestClient.get()
                .uri("/authentication/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("admin");
    }
}
