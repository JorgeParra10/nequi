package com.accenture.infraestructura.manage_rp.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.infraestructura.manage_rp.entity.BranchEntity;

@Repository
public interface IBranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
}
