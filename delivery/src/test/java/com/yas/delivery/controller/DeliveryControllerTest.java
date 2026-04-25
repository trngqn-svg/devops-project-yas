package com.yas.delivery.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DeliveryControllerTest {

    private final DeliveryController deliveryController =
            new DeliveryController();

    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(deliveryController).build();

    @Test
    void testDeliveryControllerInitialization() {
        assertNotNull(mockMvc);
    }
}
