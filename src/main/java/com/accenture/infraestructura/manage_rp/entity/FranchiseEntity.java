package com.accenture.infraestructura.manage_rp.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "franchises")
@RequiredArgsConstructor
@Getter
@Setter
public class FranchiseEntity {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;
}
