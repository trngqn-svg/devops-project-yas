package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookMapper webhookMapper;
    @Mock
    WebhookApi webHookApi;

    @InjectMocks
    WebhookService webhookService;

    @Test
    void test_notifyToWebhook_ShouldNotException() {

        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("")
            .secret("")
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
    }

    @Test
    void test_getPageableWebhooks_ShouldReturnWebhookListGetVm() {
        int pageNo = 0;
        int pageSize = 10;
        Page<Webhook> webhooks = mock(Page.class);
        WebhookListGetVm webhookListGetVm = WebhookListGetVm.builder().build();

        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(webhooks);
        when(webhookMapper.toWebhookListGetVm(webhooks, pageNo, pageSize)).thenReturn(webhookListGetVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(pageNo, pageSize);

        assertThat(result).isEqualTo(webhookListGetVm);
        verify(webhookRepository).findAll(any(PageRequest.class));
    }

    @Test
    void test_findById_WhenIdExists_ShouldReturnWebhookDetailVm() {
        Long id = 1L;
        Webhook webhook = new Webhook();
        WebhookDetailVm webhookDetailVm = new WebhookDetailVm();

        when(webhookRepository.findById(id)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.findById(id);

        assertThat(result).isEqualTo(webhookDetailVm);
        verify(webhookRepository).findById(id);
    }

    @Test
    void test_findById_WhenIdNotExists_ShouldThrowNotFoundException() {
        Long id = 1L;
        when(webhookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(id));
    }

    @Test
    void test_delete_WhenIdExists_ShouldSuccess() {
        Long id = 1L;
        when(webhookRepository.existsById(id)).thenReturn(true);

        webhookService.delete(id);

        verify(webhookEventRepository).deleteByWebhookId(id);
        verify(webhookRepository).deleteById(id);
    }

    @Test
    void test_delete_WhenIdNotExists_ShouldThrowNotFoundException() {
        Long id = 1L;
        when(webhookRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> webhookService.delete(id));
    }

    @Test
    void test_create_ShouldReturnWebhookDetailVm() {
        WebhookPostVm webhookPostVm = new WebhookPostVm();
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        WebhookDetailVm webhookDetailVm = new WebhookDetailVm();

        when(webhookMapper.toCreatedWebhook(webhookPostVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.create(webhookPostVm);

        assertThat(result).isEqualTo(webhookDetailVm);
        verify(webhookRepository).save(webhook);
    }
