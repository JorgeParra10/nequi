package com.accenture.dominio.interfaces;

import com.accenture.dominio.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductPersistence {
    Mono<Product> saveProduct(Product product);
    Mono<Void> deleteProduct(Long productId);
    Mono<Product> findProductById(Long productId);
    Flux<Product> findAllProductsByBranch(Long branchId);
}
