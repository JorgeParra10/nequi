package com.accenture.dominio.interfaces;

import com.accenture.dominio.model.Franchise;
import com.accenture.dominio.model.PageResponse;
import reactor.core.publisher.Mono;

public interface IFranchiseService {
    Mono<Franchise> createFranchise(String name);
    Mono<Franchise> updateFranchiseName(Long id, String name);
    Mono<PageResponse<Franchise>> getAllFranchisesPaged(int page, int size);
}
