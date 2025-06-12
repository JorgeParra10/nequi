package com.accenture.infraestructura.manage_rp.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.accenture.dominio.interfaces.IFranchisePersistence;
import com.accenture.dominio.interfaces.IPaginator;
import com.accenture.dominio.model.Franchise;
import com.accenture.infraestructura.manage_rp.entity.FranchiseEntity;
import com.accenture.infraestructura.manage_rp.mapper.FranchiseEntityMapper;
import com.accenture.infraestructura.manage_rp.persistence.IFranchiseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements IFranchisePersistence, IPaginator<Franchise> {
    private final IFranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = franchiseEntityMapper.toEntity(franchise);
        entity.setId(null); 
        return franchiseRepository.save(entity)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> updateName(Long id, String name) {
        return franchiseRepository.findById(id)
            .flatMap(entity -> {
                entity.setName(name);
                return franchiseRepository.save(entity);
            })
            .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return franchiseRepository.findByNameIgnoreCase(name)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return franchiseRepository.findById(id)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Flux<Franchise> findPage(int page, int size) {
        return franchiseRepository.findAll()
                .skip((long) (page - 1) * size)
                .take(size)
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Long> count() {
        return franchiseRepository.count();
    }
}
