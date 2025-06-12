package com.accenture.dominio.servicios;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.accenture.dominio.excepcion.ErrorException;
import com.accenture.dominio.excepcion.ErrorNotFound;
import com.accenture.dominio.excepcion.ExceptionAlreadyExist;
import com.accenture.dominio.interfaces.IBranchPersistence;
import com.accenture.dominio.interfaces.IBranchService;
import com.accenture.dominio.interfaces.IFranchisePersistence;
import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.model.responses.BranchWithFranchise;
import com.accenture.dominio.util.ConstantsDomain;
import com.accenture.dominio.util.validator.ReactiveValidator;

import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {
    private final IBranchPersistence branchPersistence;
    private final IFranchisePersistence franchisePersistence;

    @Override
    public Mono<Branch> createBranch(String name, Long franchiseId) {
        return ReactiveValidator.validateNotEmpty(name, ConstantsDomain.ERROR_NAME_NULL)
            .flatMap(validName -> ReactiveValidator.validateNotNull(franchiseId, ConstantsDomain.ERROR_FRANCHISE_ID_NULL)
                .flatMap(validFranchiseId -> branchPersistence.findByNameIgnoreCase(validName)
                    .flatMap(existing -> Mono.<Branch>error(new ExceptionAlreadyExist(ConstantsDomain.ERROR_BRANCH_ALREADY_EXISTS)))
                    .switchIfEmpty(
                        franchisePersistence.findById(validFranchiseId)
                            .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_FRANCHISE_NOT_FOUND + validFranchiseId)))
                            .flatMap(franchise -> branchPersistence.save(new Branch(null, validName, validFranchiseId)))
                    )
                )
            )
            .timeout(Duration.ofSeconds(5))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_OBTAINING_BRANCHES));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<PageResponse<BranchWithFranchise>> getAllBranchesWithFranchisePaged(int page, int size) {
        return branchPersistence.count()
            .flatMap(total -> branchPersistence.findAllPaged(page, size)
                .flatMap(branch -> franchisePersistence.findById(branch.getFranchiseId())
                    .map(franchise -> new BranchWithFranchise(branch, franchise))
                    .onErrorResume(e -> {
                        if (e instanceof ErrorNotFound) {
                            return Mono.empty();
                        }
                        return Mono.error(e);
                    })
                )
                .collectList()
                .map(content -> new PageResponse<>(content, page, size, total, (int) Math.ceil((double) total / size)))
            )
            .timeout(Duration.ofSeconds(5))
            .retry(3)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_OBTAINING_BRANCHES));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<Branch> updateBranchName(Long id, String name) {
        return ReactiveValidator.validateNotNull(id, ConstantsDomain.ERROR_ID_NULL)
            .flatMap(validId -> ReactiveValidator.validateNotEmpty(name, ConstantsDomain.ERROR_NAME_NULL)
                .flatMap(validName -> branchPersistence.findById(validId)
                    .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_BRANCH_NOT_FOUND + validId)))
                    .flatMap(branch -> {
                        branch.setName(validName);
                        return branchPersistence.save(branch);
                    })
                )
            )
            .timeout(Duration.ofSeconds(3))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_OBTAINING_BRANCHES));
                }
                return Mono.error(e);
            });
    }
}
