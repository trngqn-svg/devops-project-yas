package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private StockHistoryService stockHistoryService;

    @InjectMocks
    private StockService stockService;

    private StockPostVm stockPostVm;
    private Warehouse warehouse;
    private ProductInfoVm productInfoVm;

    @BeforeEach
    void setUp() {
        stockPostVm = new StockPostVm(1L, 1L);
        warehouse = Warehouse.builder().id(1L).name("Warehouse 1").build();
        productInfoVm = new ProductInfoVm(1L, "Product 1", "SKU1", true);
    }

    @Test
    void addProductIntoWarehouse_Success() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        stockService.addProductIntoWarehouse(List.of(stockPostVm));

        verify(stockRepository).saveAll(anyList());
    }

    @Test
    void addProductIntoWarehouse_StockAlreadyExisted_ThrowsException() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(true);

        assertThrows(StockExistingException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void addProductIntoWarehouse_ProductNotFound_ThrowsException() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void addProductIntoWarehouse_WarehouseNotFound_ThrowsException() {
        when(stockRepository.existsByWarehouseIdAndProductId(1L, 1L)).thenReturn(false);
        when(productService.getProduct(1L)).thenReturn(productInfoVm);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> stockService.addProductIntoWarehouse(List.of(stockPostVm)));
    }

    @Test
    void updateProductQuantityInStock_Success() {
        Stock stock = Stock.builder().id(1L).productId(1L).quantity(10L).build();
        StockQuantityVm stockQuantityVm = new StockQuantityVm(1L, 5L, "Test Note");
        StockQuantityUpdateVm updateVm = new StockQuantityUpdateVm(List.of(stockQuantityVm));

        when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

        stockService.updateProductQuantityInStock(updateVm);

        assertThat(stock.getQuantity()).isEqualTo(15L);
        verify(stockRepository).saveAll(anyList());
        verify(stockHistoryService).createStockHistories(anyList(), anyList());
        verify(productService).updateProductQuantity(anyList());
    }
}
