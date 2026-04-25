package com.yas.backofficebff.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@WebFluxTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testUser_whenAuthenticated_returnsUser() {
        webTestClient.get()
                .uri("/authentication/user")
                .headers(headers -> headers.setBearerAuth("dummy-token"))
                .exchange()
                .expectStatus().isOk();
    }
}
