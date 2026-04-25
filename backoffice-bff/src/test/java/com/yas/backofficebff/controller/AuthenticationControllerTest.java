package com.yas.backofficebff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@WebFluxTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testUser_whenAuthenticated_returnsUser() {
        webTestClient
                .mutateWith(mockOAuth2Login()
                        .attributes(attrs -> attrs.put("preferred_username", "test-user")))
                .get()
                .uri("/authentication/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("test-user");
    }
}
