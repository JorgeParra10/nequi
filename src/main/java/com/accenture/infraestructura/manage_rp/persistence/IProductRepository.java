package com.accenture.infraestructura.manage_rp.persistence;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

import com.accenture.infraestructura.manage_rp.entity.ProductEntity;

@Repository
public interface IProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Flux<ProductEntity> findAllByBranchId(Long branchId);
}