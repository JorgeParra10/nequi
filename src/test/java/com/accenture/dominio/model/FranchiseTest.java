package com.accenture.dominio.model;

import org.junit.jupiter.api.Test;

import com.accenture.dominio.model.Franchise;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {
    @Test
    void testFranchiseConstructorAndGetters() {
        Franchise franchise = new Franchise(1L, "Franchise A");
        assertEquals(1L, franchise.getId());
        assertEquals("Franchise A", franchise.getName());
    }

    @Test
    void testFranchiseSetters() {
        Franchise franchise = new Franchise();
        franchise.setId(2L);
        franchise.setName("Franchise B");
        assertEquals(2L, franchise.getId());
        assertEquals("Franchise B", franchise.getName());
    }
}
