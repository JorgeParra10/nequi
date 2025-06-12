package com.accenture.dominio.model.responses;

import org.junit.jupiter.api.Test;

import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.Product;
import com.accenture.dominio.model.responses.ProductBranchPair;

import static org.junit.jupiter.api.Assertions.*;

class ProductBranchPairTest {
    @Test
    void testConstructorAndGetters() {
        Product product = new Product(1L, "P1", 10, 2L);
        Branch branch = new Branch(2L, "B1", 3L);
        ProductBranchPair pair = new ProductBranchPair(product, branch);
        assertEquals(product, pair.getProduct());
        assertEquals(branch, pair.getBranch());
    }
}
