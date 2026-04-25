package com.yas.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private UserAddressService userAddressService;

    private UserAddress userAddress;
    private AddressDetailVm addressDetailVm;
    private AddressPostVm addressPostVm;
    private AddressVm addressVm;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user-id");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userAddress = new UserAddress();
        userAddress.setId(1L);
        userAddress.setUserId("test-user-id");
        userAddress.setAddressId(10L);
        userAddress.setIsActive(true);

        addressDetailVm = new AddressDetailVm(10L, "John Doe", "123456789", "Line 1", "City", "12345", 1L, "District", 1L, "State", 1L, "Country");
        
        addressPostVm = new AddressPostVm("John Doe", "123456789", "Line 1", "City", "12345", 1L, 1L, 1L);
        
        addressVm = new AddressVm(10L, "John Doe", "123456789", "Line 1", "City", "12345", 1L, 1L, 1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetUserAddressList() {
        when(userAddressRepository.findAllByUserId("test-user-id")).thenReturn(List.of(userAddress));
        when(locationService.getAddressesByIdList(List.of(10L))).thenReturn(List.of(addressDetailVm));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    void testGetUserAddressList_AnonymousUser_ThrowsAccessDeniedException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void testGetAddressDefault_Success() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue("test-user-id")).thenReturn(Optional.of(userAddress));
        when(locationService.getAddressById(10L)).thenReturn(addressDetailVm);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.contactName()).isEqualTo("John Doe");
    }

    @Test
    void testGetAddressDefault_AnonymousUser_ThrowsAccessDeniedException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_NotFound() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue("test-user-id")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testCreateAddress_FirstAddress() {
        when(userAddressRepository.findAllByUserId("test-user-id")).thenReturn(List.of());
        when(locationService.createAddress(addressPostVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(userAddress);

        UserAddressVm result = userAddressService.createAddress(addressPostVm);

        assertThat(result.isActive()).isTrue();
        verify(userAddressRepository).save(any(UserAddress.class));
    }

    @Test
    void testDeleteAddress_Success() {
        when(userAddressRepository.findOneByUserIdAndAddressId("test-user-id", 10L)).thenReturn(userAddress);

        userAddressService.deleteAddress(10L);

        verify(userAddressRepository).delete(userAddress);
    }

    @Test
    void testDeleteAddress_NotFound() {
        when(userAddressRepository.findOneByUserIdAndAddressId("test-user-id", 10L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(10L));
    }

    @Test
    void testChooseDefaultAddress() {
        UserAddress userAddress2 = new UserAddress();
        userAddress2.setId(2L);
        userAddress2.setUserId("test-user-id");
        userAddress2.setAddressId(11L);
        userAddress2.setIsActive(false);

        when(userAddressRepository.findAllByUserId("test-user-id")).thenReturn(List.of(userAddress, userAddress2));

        userAddressService.chooseDefaultAddress(11L);

        assertThat(userAddress.getIsActive()).isFalse();
        assertThat(userAddress2.getIsActive()).isTrue();
        verify(userAddressRepository).saveAll(List.of(userAddress, userAddress2));
    }
}
