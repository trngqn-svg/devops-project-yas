package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class MediaServiceTest {
    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        mediaService = new MediaService(restClient, serviceUrlConfig);
    }

    @Test
    void testGetMedia_IdNull_ReturnsEmptyVm() {
        var result = mediaService.getMedia(null);
        assertThat(result.id()).isNull();
    }

    @Test
    void testHandleBodilessFallback_ThrowsException() {
        assertThrows(RuntimeException.class, () -> mediaService.handleBodilessFallback(new RuntimeException("Test")));
    }
}
