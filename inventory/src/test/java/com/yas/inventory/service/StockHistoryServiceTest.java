package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockHistoryServiceTest {

    @Mock
    private StockHistoryRepository stockHistoryRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockHistoryService stockHistoryService;

    private Stock stock;
    private StockQuantityVm stockQuantityVm;
    private StockHistory stockHistory;
    private ProductInfoVm productInfoVm;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);

        stock = new Stock();
        stock.setId(1L);
        stock.setProductId(2L);
        stock.setWarehouse(warehouse);

        stockQuantityVm = new StockQuantityVm(1L, 10L, "Initial stock");

        stockHistory = new StockHistory();
        stockHistory.setId(1L);
        stockHistory.setProductId(2L);
        stockHistory.setAdjustedQuantity(10L);
        stockHistory.setNote("Initial stock");
        stockHistory.setWarehouse(warehouse);

        productInfoVm = new ProductInfoVm(2L, "Product A", "SKU-A", true);
    }

    @Test
    void testCreateStockHistories() {
        stockHistoryService.createStockHistories(List.of(stock), List.of(stockQuantityVm));

        verify(stockHistoryRepository).saveAll(anyList());
    }

    @Test
    void testGetStockHistories() {
        when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(2L, 1L))
                .thenReturn(List.of(stockHistory));
        when(productService.getProduct(2L)).thenReturn(productInfoVm);

        StockHistoryListVm result = stockHistoryService.getStockHistories(2L, 1L);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().get(0).productName()).isEqualTo("Product A");
        assertThat(result.data().get(0).adjustedQuantity()).isEqualTo(10L);
    }
}
