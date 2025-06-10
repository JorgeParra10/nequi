package com.branch.nequi_test.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private int stock;

        @ManyToOne
        @JoinColumn(name = "branch_id", nullable = false)
        private Branch branch;
}
