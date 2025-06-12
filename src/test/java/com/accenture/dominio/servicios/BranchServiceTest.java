package com.accenture.dominio.servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.accenture.dominio.excepcion.ErrorBadRequest;
import com.accenture.dominio.excepcion.ErrorException;
import com.accenture.dominio.excepcion.ErrorNotFound;
import com.accenture.dominio.excepcion.ExceptionAlreadyExist;
import com.accenture.dominio.interfaces.IBranchPersistence;
import com.accenture.dominio.interfaces.IFranchisePersistence;
import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.Franchise;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.model.responses.BranchWithFranchise;
import com.accenture.dominio.util.ConstantsDomain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BranchServiceTest {
    private IBranchPersistence branchPersistence;
    private IFranchisePersistence franchisePersistence;
    private BranchService branchService;

    @BeforeEach
    void setUp() {
        branchPersistence = Mockito.mock(IBranchPersistence.class);
        franchisePersistence = Mockito.mock(IFranchisePersistence.class);
        branchService = new BranchService(branchPersistence, franchisePersistence);
    }

    @Test
    void testCreateBranch() {
        // Arrange
        Branch mockBranch = new Branch(1L, "Main Branch", 2L);
        when(branchPersistence.save(any(Branch.class))).thenReturn(Mono.just(mockBranch));
        when(branchPersistence.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.just(new Franchise(2L, "Franchise")));
        
        // Act
        Mono<Branch> result = branchService.createBranch("Main Branch", 2L);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(branch -> 
                branch.getId().equals(1L) && 
                branch.getName().equals("Main Branch") &&
                branch.getFranchiseId().equals(2L))
            .verifyComplete();
    }

    @Test
    void testGetAllBranchesWithFranchisePaged() {
        // Arrange
        when(branchPersistence.count()).thenReturn(Mono.just(1L));
        when(branchPersistence.findAllPaged(0, 10))
            .thenReturn(Flux.just(new Branch(1L, "Branch1", 2L)));
        when(franchisePersistence.findById(anyLong()))
            .thenReturn(Mono.just(new Franchise(2L, "Franchise")));
        
        // Act
        Mono<PageResponse<BranchWithFranchise>> result = branchService.getAllBranchesWithFranchisePaged(0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page.getContent().size() == 1 && 
                page.getContent().get(0).getBranch().getName().equals("Branch1") &&
                page.getContent().get(0).getFranchise().getName().equals("Franchise"))
            .verifyComplete();
    }


    @Test
    void testCreateBranch_FranchiseNotFound() {
        // Arrange
        when(branchPersistence.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.empty());
        
        // Act
        Mono<Branch> result = branchService.createBranch("Sucursal", 99L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(ex -> 
                ex instanceof ErrorNotFound && 
                ex.getMessage().contains(ConstantsDomain.ERROR_FRANCHISE_NOT_FOUND + "99"))
            .verify();
    }

    @Test
    void testGetAllBranchesWithFranchisePaged_LessThanPageSize() {
        // Arrange
        when(branchPersistence.count()).thenReturn(Mono.just(1L));
        when(branchPersistence.findAllPaged(0, 10))
            .thenReturn(Flux.just(new Branch(1L, "Branch1", 2L)));
        when(franchisePersistence.findById(anyLong()))
            .thenReturn(Mono.just(new Franchise(2L, "Franchise")));
        
        // Act
        Mono<PageResponse<BranchWithFranchise>> result = branchService.getAllBranchesWithFranchisePaged(0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page.getContent().size() == 1 && 
                page.getTotalElements() == 1 &&
                page.getTotalPages() == 1)
            .verifyComplete();
    }

    @Test
    void testGetAllBranchesWithFranchisePaged_FranchiseNotFoundForBranch() {
        // Arrange
        when(branchPersistence.count()).thenReturn(Mono.just(1L));
        when(branchPersistence.findAllPaged(0, 10))
            .thenReturn(Flux.just(new Branch(1L, "Branch1", 2L)));
        when(franchisePersistence.findById(anyLong()))
            .thenReturn(Mono.error(new ErrorNotFound("Franchise not found")));
        
        // Act
        Mono<PageResponse<BranchWithFranchise>> result = branchService.getAllBranchesWithFranchisePaged(0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page.getContent().isEmpty() && 
                page.getTotalElements() == 1 &&
                page.getTotalPages() == 1)
            .verifyComplete();
    }
    
    @Test
    void testCreateBranch_AlreadyExists() {
        // Arrange
        when(branchPersistence.findByNameIgnoreCase("ExistingBranch"))
            .thenReturn(Mono.just(new Branch(1L, "ExistingBranch", 2L)));
        
        when(franchisePersistence.findById(anyLong()))
            .thenReturn(Mono.just(new Franchise(2L, "Franchise")));
        
        // Act
        Mono<Branch> result = branchService.createBranch("ExistingBranch", 2L);
        
        // Assert
        StepVerifier.create(result)
            .expectError(ExceptionAlreadyExist.class)
            .verify();
    }
    
    
    @Test
    void testCreateBranch_EmptyName() {
       
        // Act & Assert
        StepVerifier.create(branchService.createBranch("", 1L))
            .expectErrorMatches(error -> 
                error instanceof ErrorBadRequest && 
                error.getMessage().equals(ConstantsDomain.ERROR_NAME_NULL))
            .verify();
    }
    
    @Test
    void testCreateBranch_Timeout() {
        // Arrange
        when(branchPersistence.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.just(new Franchise(1L, "Franchise")));
        when(branchPersistence.save(any(Branch.class)))
            .thenReturn(Mono.just(new Branch(1L, "Branch1", 1L)).delayElement(Duration.ofSeconds(6)));
        
        // Act
        Mono<Branch> result = branchService.createBranch("Branch1", 1L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorException);
                assertEquals("Timeout when obtaining branches", error.getMessage());
            })
            .verify();
    }
    
    @Test
    void testCreateBranch_RetrySuccess() {
        // Arrange
        when(branchPersistence.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.just(new Franchise(1L, "Franchise")));
        
        doAnswer(invocation -> {
            return Mono.error(new RuntimeException("Transient error"));
        }).doAnswer(invocation -> {
            return Mono.just(new Branch(1L, "Branch1", 1L));
        }).when(branchPersistence).save(any(Branch.class));
        
        // Act
        Mono<Branch> result = branchService.createBranch("Branch1", 1L);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(branch -> branch.getName().equals("Branch1"))
            .verifyComplete();
    }

    @Test
    void testUpdateBranchName_Success() {
        // Arrange
        Branch existingBranch = new Branch(1L, "Old Name", 2L);
        Branch updatedBranch = new Branch(1L, "New Name", 2L);
        
        when(branchPersistence.findById(1L)).thenReturn(Mono.just(existingBranch));
        when(branchPersistence.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));
        
        // Act
        Mono<Branch> result = branchService.updateBranchName(1L, "New Name");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(branch -> 
                branch.getId().equals(1L) && 
                branch.getName().equals("New Name") &&
                branch.getFranchiseId().equals(2L))
            .verifyComplete();
        
        verify(branchPersistence).save(argThat(branch -> 
            branch.getId().equals(1L) && 
            branch.getName().equals("New Name") &&
            branch.getFranchiseId().equals(2L)));
    }
    
    @Test
    void testUpdateBranchName_BranchNotFound() {
        // Arrange
        when(branchPersistence.findById(99L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Branch> result = branchService.updateBranchName(99L, "New Name");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(error -> 
                error instanceof ErrorNotFound && 
                error.getMessage().contains(ConstantsDomain.ERROR_BRANCH_NOT_FOUND + "99"))
            .verify();
    }
    
    @Test
    void testUpdateBranchName_EmptyName() {
        // Act & Assert
        StepVerifier.create(branchService.updateBranchName(1L, ""))
            .expectErrorMatches(error -> 
                error instanceof ErrorBadRequest && 
                error.getMessage().equals(ConstantsDomain.ERROR_NAME_NULL))
            .verify();
    }
    
    @Test
    void testUpdateBranchName_NullId() {
        // Act & Assert
        StepVerifier.create(branchService.updateBranchName(null, "New Name"))
            .expectErrorMatches(error -> 
                error instanceof ErrorBadRequest && 
                error.getMessage().equals(ConstantsDomain.ERROR_ID_NULL))
            .verify();
    }
    
    @Test
    void testUpdateBranchName_Timeout() {
        // Arrange
        Branch existingBranch = new Branch(1L, "Old Name", 2L);
        
        when(branchPersistence.findById(1L)).thenReturn(Mono.just(existingBranch));
        when(branchPersistence.save(any(Branch.class)))
            .thenReturn(Mono.just(new Branch(1L, "New Name", 2L)).delayElement(Duration.ofSeconds(4)));
        
        // Act
        Mono<Branch> result = branchService.updateBranchName(1L, "New Name");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorException);
                assertEquals("Timeout when obtaining branches", error.getMessage());
            })
            .verify();
    }
}
