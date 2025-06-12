package com.accenture.dominio.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {
    @Test
    void testBranchConstructorAndGetters() {
        Branch branch = new Branch(1L, "Main Branch", 2L);
        assertEquals(1L, branch.getId());
        assertEquals("Main Branch", branch.getName());
        assertEquals(2L, branch.getFranchiseId());
    }

    @Test
    void testBranchSetters() {
        Branch branch = new Branch();
        branch.setId(2L);
        branch.setName("Secondary Branch");
        branch.setFranchiseId(3L);
        assertEquals(2L, branch.getId());
        assertEquals("Secondary Branch", branch.getName());
        assertEquals(3L, branch.getFranchiseId());
    }
}
