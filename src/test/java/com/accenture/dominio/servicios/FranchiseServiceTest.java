package com.accenture.dominio.servicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.accenture.dominio.excepcion.ErrorBadRequest;
import com.accenture.dominio.excepcion.ErrorException;
import com.accenture.dominio.excepcion.ErrorNotFound;
import com.accenture.dominio.excepcion.ExceptionAlreadyExist;
import com.accenture.dominio.interfaces.IFranchisePersistence;
import com.accenture.dominio.interfaces.IPaginator;
import com.accenture.dominio.model.Franchise;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.util.ConstantsDomain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FranchiseServiceTest {
    private IFranchisePersistence franchisePersistence;
    private IPaginator<Franchise> paginator;
    private FranchiseService franchiseService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        franchisePersistence = Mockito.mock(IFranchisePersistence.class);
        paginator = Mockito.mock(IPaginator.class);
        franchiseService = new FranchiseService(franchisePersistence, paginator);
    }

    @Test
    void testCreateFranchise_Success() {
        // Arrange
        when(franchisePersistence.findByName(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.save(any(Franchise.class))).thenReturn(Mono.just(new Franchise(1L, "Franchise1")));
        
        // Act
        Mono<Franchise> result = franchiseService.createFranchise("Franchise1");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(franchise -> franchise.getName().equals("Franchise1"))
            .verifyComplete();
    }


    @Test
    void testUpdateFranchiseName_Success() {
        // Arrange
        Franchise existing = new Franchise(1L, "OldName");
        when(franchisePersistence.findById(1L)).thenReturn(Mono.just(existing));
        when(franchisePersistence.updateName(1L, "NewName")).thenReturn(Mono.just(new Franchise(1L, "NewName")));
        
        // Act
        Mono<Franchise> result = franchiseService.updateFranchiseName(1L, "NewName");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(franchise -> franchise.getName().equals("NewName"))
            .verifyComplete();
    }

    @Test
    void testUpdateFranchiseName_NotFound() {
        // Arrange
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.empty());
        
        // Act
        Mono<Franchise> result = franchiseService.updateFranchiseName(1L, "NewName");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(ex -> 
                ex instanceof ErrorNotFound && 
                ex.getMessage().contains("not found"))
            .verify();
    }

    @Test
    void testGetAllFranchisesPaged() {
        // Arrange
        when(paginator.count()).thenReturn(Mono.just(1L));
        when(paginator.findPage(anyInt(), anyInt())).thenReturn(Flux.just(new Franchise(1L, "FranchiseA")));
        
        // Act
        Mono<PageResponse<Franchise>> result = franchiseService.getAllFranchisesPaged(0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page.getContent().size() == 1 && 
                page.getContent().get(0).getName().equals("FranchiseA") &&
                page.getTotalElements() == 1)
            .verifyComplete();
    }

    @Test
    void testGetAllFranchisesPaged_Empty() {
        // Arrange
        when(paginator.count()).thenReturn(Mono.just(0L));
        when(paginator.findPage(anyInt(), anyInt())).thenReturn(Flux.empty());
        
        // Act
        Mono<PageResponse<Franchise>> result = franchiseService.getAllFranchisesPaged(0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page.getContent().isEmpty() && 
                page.getTotalElements() == 0 &&
                page.getTotalPages() == 0)
            .verifyComplete();
    }

    @Test
    void testGetAllFranchisesPaged_MultiplePages() {
        // Arrange
        when(paginator.count()).thenReturn(Mono.just(3L));
        when(paginator.findPage(0, 2)).thenReturn(Flux.just(
            new Franchise(1L, "F1"),
            new Franchise(2L, "F2")
        ));
        when(paginator.findPage(1, 2)).thenReturn(Flux.just(
            new Franchise(3L, "F3")
        ));
        
        // Act & Assert - Page 0
        Mono<PageResponse<Franchise>> page0 = franchiseService.getAllFranchisesPaged(0, 2);
        StepVerifier.create(page0)
            .expectNextMatches(page -> 
                page.getContent().size() == 2 && 
                page.getTotalElements() == 3 &&
                page.getSize() == 2 &&
                page.getTotalPages() == 2)
            .verifyComplete();
        
        // Act & Assert - Page 1
        Mono<PageResponse<Franchise>> page1 = franchiseService.getAllFranchisesPaged(1, 2);
        StepVerifier.create(page1)
            .expectNextMatches(page -> 
                page.getContent().size() == 1 && 
                page.getTotalElements() == 3 &&
                page.getSize() == 2 &&
                page.getTotalPages() == 2)
            .verifyComplete();
    }
    
    @Test
    void testCreateFranchise_AlreadyExists() {
        // Arrange
        when(franchisePersistence.findByName("ExistingFranchise"))
            .thenReturn(Mono.just(new Franchise(1L, "ExistingFranchise")));
        
        when(franchisePersistence.save(any(Franchise.class)))
            .thenReturn(Mono.just(new Franchise(1L, "ExistingFranchise")));
        
        StepVerifier.create(franchiseService.createFranchise("ExistingFranchise"))
            .expectError(ExceptionAlreadyExist.class)
            .verify();
    }
    
    @Test
    void testCreateFranchise_Timeout() {
        // Arrange
        when(franchisePersistence.findByName(anyString())).thenReturn(Mono.empty());
        when(franchisePersistence.save(any(Franchise.class)))
            .thenReturn(Mono.just(new Franchise(1L, "Franchise1")).delayElement(Duration.ofSeconds(4)));
        
        // Act
        Mono<Franchise> result = franchiseService.createFranchise("Franchise1");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorException);
                assertEquals("Timeout when obtaining franchises", error.getMessage());
            })
            .verify();
    }
    
    @Test
    void testCreateFranchise_RetrySuccess() {
        when(franchisePersistence.findByName(anyString())).thenReturn(Mono.empty());
        
        doAnswer(invocation -> {
            return Mono.error(new RuntimeException("Transient error"));
        }).doAnswer(invocation -> {
            return Mono.just(new Franchise(1L, "Franchise1"));
        }).when(franchisePersistence).save(any(Franchise.class));
        
        // Act
        Mono<Franchise> result = franchiseService.createFranchise("Franchise1");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(franchise -> franchise.getName().equals("Franchise1"))
            .verifyComplete();
    }
    
    @Test
    void testCreateFranchise_NullName() {
        StepVerifier.create(franchiseService.createFranchise(null))
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorBadRequest);
                assertEquals(ConstantsDomain.ERROR_NAME_NULL, error.getMessage());
            })
            .verify();
    }
    
    @Test
    void testCreateFranchise_EmptyName() {
        // Act & Assert
        StepVerifier.create(franchiseService.createFranchise(""))
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorBadRequest);
                assertEquals(ConstantsDomain.ERROR_NAME_NULL, error.getMessage());
            })
            .verify();
    }
    
    @Test
    void testUpdateFranchiseName_NullName() {
        // Act & Assert
        StepVerifier.create(franchiseService.updateFranchiseName(1L, null))
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorBadRequest);
                assertEquals(ConstantsDomain.ERROR_NAME_NULL, error.getMessage());
            })
            .verify();
    }
    
    @Test
    void testUpdateFranchiseName_EmptyName() {
        // Act & Assert
        StepVerifier.create(franchiseService.updateFranchiseName(1L, ""))
            .expectErrorSatisfies(error -> {
                assertTrue(error instanceof ErrorBadRequest);
                assertEquals(ConstantsDomain.ERROR_NAME_NULL, error.getMessage());
            })
            .verify();
    }
}
