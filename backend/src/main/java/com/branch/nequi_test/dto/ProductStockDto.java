package com.branch.nequi_test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductStockDto {
    private String productName;
    private Long productId;
    private int stock;
    private String branchName;
}