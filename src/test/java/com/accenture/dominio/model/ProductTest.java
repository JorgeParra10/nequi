package com.accenture.dominio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void testProductConstructorAndGetters() {
        Product product = new Product(1L, "Test Product", 10, 5L);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(10, product.getStock());
        assertEquals(5L, product.getBranchId());
    }

    @Test
    void testProductSetters() {
        Product product = new Product();
        product.setId(2L);
        product.setName("Another Product");
        product.setStock(20);
        product.setBranchId(6L);
        assertEquals(2L, product.getId());
        assertEquals("Another Product", product.getName());
        assertEquals(20, product.getStock());
        assertEquals(6L, product.getBranchId());
    }
}
