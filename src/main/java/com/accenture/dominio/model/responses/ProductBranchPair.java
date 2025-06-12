package com.accenture.dominio.model.responses;

import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.Product;

public class ProductBranchPair {
    private final Product product;
    private final Branch branch;

    public ProductBranchPair(Product product, Branch branch) {
        this.product = product;
        this.branch = branch;
    }
    public Product getProduct() { return product; }
    public Branch getBranch() { return branch; }
}
