package com.yas.delivery.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.yas.delivery.service.DeliveryService;

class DeliveryControllerTest {

    private final DeliveryService deliveryService = mock(DeliveryService.class);

    private final DeliveryController deliveryController =
            new DeliveryController(deliveryService);

    private final MockMvc mockMvc =
            MockMvcBuilders.standaloneSetup(deliveryController).build();

    @Test
    void testDeliveryControllerInitialization() {
        assertNotNull(mockMvc);
    }
}
