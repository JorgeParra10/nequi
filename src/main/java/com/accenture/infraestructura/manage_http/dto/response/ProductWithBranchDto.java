package com.accenture.infraestructura.manage_http.dto.response;

import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithBranchDto {
    private Branch branch;
    @JsonIgnoreProperties({"branchId"})
    private Product product;
    
}
