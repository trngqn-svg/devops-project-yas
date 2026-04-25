package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void testAuthenticatedUser_record() {
        String username = "test-user";
        AuthenticatedUser user = new AuthenticatedUser(username);

        assertEquals(username, user.username());
    }
}
