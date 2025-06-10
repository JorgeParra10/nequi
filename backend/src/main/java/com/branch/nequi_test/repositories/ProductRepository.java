package com.branch.nequi_test.repositories;

import com.branch.nequi_test.dto.ProductStockDto;
import com.branch.nequi_test.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new com.branch.nequi_test.dto.ProductStockDto(p.name,p.id, p.stock, b.name) " +
            "FROM Product p JOIN p.branch b " +
            "WHERE b.franchise.id = :franchiseId " +
            "AND p.stock = (SELECT MAX(p2.stock) FROM Product p2 WHERE p2.branch.id = b.id) order by p.stock desc")
    List<ProductStockDto> findHighestStockProductsByFranchise(Long franchiseId);
}
