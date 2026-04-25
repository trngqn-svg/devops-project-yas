package com.yas.cart.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Test
    void testToCartItem_fromPostVm() {
        CartItemPostVm postVm = CartItemPostVm.builder()
            .productId(1L)
            .quantity(5)
            .build();
        String customerId = "customer-1";

        CartItem cartItem = cartItemMapper.toCartItem(postVm, customerId);

        assertEquals(customerId, cartItem.getCustomerId());
        assertEquals(1L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testToCartItem_fromFields() {
        String customerId = "customer-1";
        Long productId = 2L;
        Integer quantity = 10;

        CartItem cartItem = cartItemMapper.toCartItem(customerId, productId, quantity);

        assertEquals(customerId, cartItem.getCustomerId());
        assertEquals(productId, cartItem.getProductId());
        assertEquals(quantity, cartItem.getQuantity());
    }

    @Test
    void testToGetVm() {
        CartItem cartItem = CartItem.builder()
            .customerId("customer-1")
            .productId(1L)
            .quantity(5)
            .build();

        CartItemGetVm getVm = cartItemMapper.toGetVm(cartItem);

        assertEquals("customer-1", getVm.customerId());
        assertEquals(1L, getVm.productId());
        assertEquals(5, getVm.quantity());
    }

    @Test
    void testToGetVms() {
        CartItem cartItem1 = CartItem.builder()
            .customerId("customer-1")
            .productId(1L)
            .quantity(5)
            .build();
        CartItem cartItem2 = CartItem.builder()
            .customerId("customer-1")
            .productId(2L)
            .quantity(10)
            .build();

        List<CartItemGetVm> getVms = cartItemMapper.toGetVms(List.of(cartItem1, cartItem2));

        assertEquals(2, getVms.size());
        assertEquals(1L, getVms.get(0).productId());
        assertEquals(2L, getVms.get(1).productId());
    }
}
