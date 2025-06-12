package com.accenture.dominio.interfaces;

import com.accenture.dominio.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBranchPersistence {
    Mono<Branch> save(Branch branch);
    Flux<Branch> findAll();
    Flux<Branch> findAllPaged(int page, int size);
    Mono<Long> count();
    Mono<Branch> findByNameIgnoreCase(String name);
    Mono<Branch> findById(Long id);
}
