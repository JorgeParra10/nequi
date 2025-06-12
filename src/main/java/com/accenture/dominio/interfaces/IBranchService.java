package com.accenture.dominio.interfaces;

import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.model.responses.BranchWithFranchise;

import reactor.core.publisher.Mono;

public interface IBranchService {
    Mono<Branch> createBranch(String name, Long franchiseId);
    Mono<PageResponse<BranchWithFranchise>> getAllBranchesWithFranchisePaged(int page, int size);
    Mono<Branch> updateBranchName(Long id, String name);
}
