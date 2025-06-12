package com.accenture.infraestructura.manage_rp.adapter;

import com.accenture.dominio.interfaces.IBranchPersistence;
import com.accenture.dominio.interfaces.IPaginator;
import com.accenture.dominio.model.Branch;
import com.accenture.infraestructura.manage_rp.entity.BranchEntity;
import com.accenture.infraestructura.manage_rp.mapper.BranchEntityMapper;
import com.accenture.infraestructura.manage_rp.persistence.IBranchRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchPersistenceAdapter implements IBranchPersistence, IPaginator<Branch> {
    private final IBranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = branchEntityMapper.toEntity(branch);
        if (branch.getId() == null) {
            entity.setId(null);
        }
        return branchRepository.save(entity)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Flux<Branch> findAll() {
        return branchRepository.findAll().map(branchEntityMapper::toModel);
    }

    @Override
    public Flux<Branch> findAllPaged(int page, int size) {
        return branchRepository.findAll()
                .skip((long) (page - 1) * size)
                .take(size)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Long> count() {
        return branchRepository.count();
    }

    @Override
    public Flux<Branch> findPage(int page, int size) {
        return branchRepository.findAll()
                .skip((long) (page - 1) * size)
                .take(size)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> findByNameIgnoreCase(String name) {
        return branchRepository.findAll()
                .filter(b -> b.getName() != null && b.getName().equalsIgnoreCase(name))
                .next()
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepository.findById(id).map(branchEntityMapper::toModel);
    }
}
