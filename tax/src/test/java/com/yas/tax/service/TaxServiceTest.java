package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TaxRateService.class)
public class TaxServiceTest {
    @MockitoBean
    TaxRateRepository taxRateRepository;
    @MockitoBean
    LocationService locationService;
    @MockitoBean
    TaxClassRepository taxClassRepository;

    @Autowired
    TaxRateService taxRateService;

    TaxRate taxRate;
    @BeforeEach
    void setUp() {
        TaxClass taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create();
        lenient().when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));
    }

    @Test
    void  testFindAll_shouldReturnAllTaxRate() {
        // run
        List<TaxRateVm> result = taxRateService.findAll();
        // assert
        assertThat(result).hasSize(1).contains(TaxRateVm.fromModel(taxRate));
    }
    
    @Test
    void testFindById_NotFound_ThrowsNotFoundException() {
        lenient().when(taxRateRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(com.yas.commonlibrary.exception.NotFoundException.class, () -> taxRateService.findById(1L));
    }

    @Test
    void testDelete_NotFound_ThrowsNotFoundException() {
        lenient().when(taxRateRepository.existsById(1L)).thenReturn(false);
        assertThrows(com.yas.commonlibrary.exception.NotFoundException.class, () -> taxRateService.delete(1L));
    }

    @Test
    void testGetTaxPercent_ReturnsValue() {
        lenient().when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(10.0);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");
        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void testGetTaxPercent_ReturnsZeroWhenNull() {
        lenient().when(taxRateRepository.getTaxPercent(1L, 1L, "123", 1L)).thenReturn(null);
        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "123");
        assertThat(result).isEqualTo(0);
    }
}
