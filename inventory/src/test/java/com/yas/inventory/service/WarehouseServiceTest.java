package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;
    private WarehousePostVm warehousePostVm;
    private AddressDetailVm addressDetailVm;
    private AddressVm addressVm;

    @BeforeEach
    void setUp() {
        warehouse = Warehouse.builder().id(1L).name("Warehouse 1").addressId(10L).build();
        warehousePostVm = new WarehousePostVm("wh-1", "Warehouse 1", "John", "123", "Line1", "Line2", "City", "Zip", 1L,
                1L, 1L);
        addressDetailVm = new AddressDetailVm(10L, "John", "123", "Line1", "Line2", "City", "Zip", 1L, "Dist", 1L,
                "State", 1L, "Country");
        addressVm = new AddressVm(10L, "John", "123", "Line1", "City", "Zip", 1L, 1L, 1L);
    }

    @Test
    void findAllWarehouses_ReturnsList() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));
        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();
        assertThat(result).hasSize(1);
    }

    @Test
    void findById_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(locationService.getAddressById(10L)).thenReturn(addressDetailVm);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.name()).isEqualTo("Warehouse 1");
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_Success() {
        when(warehouseRepository.existsByName(anyString())).thenReturn(false);
        when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        Warehouse result = warehouseService.create(warehousePostVm);

        assertThat(result).isNotNull();
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void create_DuplicateName_ThrowsException() {
        when(warehouseRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> warehouseService.create(warehousePostVm));
    }

    @Test
    void update_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId(anyString(), anyLong())).thenReturn(false);

        warehouseService.update(warehousePostVm, 1L);

        verify(locationService).updateAddress(anyLong(), any(AddressPostVm.class));
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void delete_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(10L);
    }

    @Test
    void getPageableWarehouses_ReturnsPage() {
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).hasSize(1);
    }
}
