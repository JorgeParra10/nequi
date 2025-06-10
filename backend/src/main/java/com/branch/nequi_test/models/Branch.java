package com.branch.nequi_test.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "branch")
public class Branch {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @ManyToOne
        @JoinColumn(name = "franchise_id", nullable = false)
        private Franchise franchise;

        @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Product> products;
}
