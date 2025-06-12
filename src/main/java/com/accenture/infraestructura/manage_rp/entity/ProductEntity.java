package com.accenture.infraestructura.manage_rp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "products")
public class ProductEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("stock")
    private Integer stock;

    @Column("branch_id")
    private Long branchId;
}