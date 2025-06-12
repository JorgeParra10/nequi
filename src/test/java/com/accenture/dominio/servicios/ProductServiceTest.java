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
import com.accenture.dominio.interfaces.IProductPersistence;
import com.accenture.dominio.model.Branch;
import com.accenture.dominio.model.Franchise;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.model.Product;
import com.accenture.dominio.model.responses.ProductBranchPair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private IProductPersistence productPersistence;
    private IBranchPersistence branchPersistence;
    private IFranchisePersistence franchisePersistence;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productPersistence = Mockito.mock(IProductPersistence.class);
        branchPersistence = Mockito.mock(IBranchPersistence.class);
        franchisePersistence = Mockito.mock(IFranchisePersistence.class);
        productService = new ProductService(productPersistence, branchPersistence, franchisePersistence);
    }

    @Test
    void testAddProductToBranch_Success() {
        // Arrange
        when(branchPersistence.findById(2L)).thenReturn(Mono.just(new Branch(2L, "Sucursal", 1L)));
        when(productPersistence.findAllProductsByBranch(2L)).thenReturn(Flux.empty());
        when(productPersistence.saveProduct(any(Product.class))).thenReturn(Mono.just(new Product(1L, "P2", 5, 2L)));
        
        // Act
        Mono<Product> result = productService.addProductToBranch("P2", 5, 2L);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(product -> 
                product.getName().equals("P2") && 
                product.getStock() == 5 &&
                product.getBranchId() == 2L)
            .verifyComplete();
    }

    @Test
    void testAddProductToBranch_BranchNotFound() {
        // Arrange
        when(branchPersistence.findById(99L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Product> result = productService.addProductToBranch("P2", 5, 99L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorNotFound);
                assertTrue(ex.getMessage().toLowerCase().contains("branch"));
            })
            .verify();
    }

    @Test
    void testAddProductToBranch_ProductAlreadyExists() {
        // Arrange
        when(branchPersistence.findById(2L)).thenReturn(Mono.just(new Branch(2L, "Sucursal", 1L)));
        when(productPersistence.findAllProductsByBranch(2L))
            .thenReturn(Flux.just(new Product(2L, "P2", 10, 2L)));
        when(productPersistence.saveProduct(any(Product.class))).thenThrow(new RuntimeException("Should not be called"));
        
        // Act
        Mono<Product> result = productService.addProductToBranch("P2", 5, 2L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ExceptionAlreadyExist);
                assertTrue(ex.getMessage().contains("already exists"));
            })
            .verify();
    }

    @Test
    void testRemoveProductFromBranch_Success() {
        // Arrange
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(new Product(1L, "P1", 5, 2L)));
        when(productPersistence.deleteProduct(1L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = productService.removeProductFromBranch(1L);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    void testRemoveProductFromBranch_NotFound() {
        // Arrange
        when(productPersistence.findProductById(99L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = productService.removeProductFromBranch(99L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorNotFound);
                assertTrue(ex.getMessage().contains("not found"));
            })
            .verify();
    }

    @Test
    void testUpdateProductStock_Success() {
        // Arrange
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(new Product(1L, "P1", 5, 2L)));
        when(productPersistence.saveProduct(any(Product.class))).thenReturn(Mono.just(new Product(1L, "P1", 20, 2L)));
        
        // Act
        Mono<Product> result = productService.updateProductStock(1L, 20);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(product -> product.getStock() == 20)
            .verifyComplete();
    }

    @Test
    void testUpdateProductStock_NotFound() {
        // Arrange
        when(productPersistence.findProductById(99L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Product> result = productService.updateProductStock(99L, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorNotFound);
                assertTrue(ex.getMessage().contains("not found"));
            })
            .verify();
    }
    
    @Test
    void testUpdateProductStock_NegativeStock() {
        // Arrange
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(new Product(1L, "P1", 5, 2L)));
        
        // Act
        Mono<Product> result = productService.updateProductStock(1L, -5);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
                assertTrue(ex.getMessage().contains("negative"));
            })
            .verify();
    }
    
    @Test
    void testUpdateProductStock_NullStock() {
        // Arrange
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(new Product(1L, "P1", 5, 2L)));
        
        // Act
        Mono<Product> result = productService.updateProductStock(1L, null);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
                assertTrue(ex.getMessage().contains("negative"));
            })
            .verify();
    }
    
    @Test
    void testUpdateProductStock_Timeout() {
        // Arrange
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(new Product(1L, "P1", 5, 2L))
            .delayElement(Duration.ofSeconds(4))); // Simular un timeout
        
        // Act
        Mono<Product> result = productService.updateProductStock(1L, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorException);
                assertTrue(ex.getMessage().contains("Timeout"));
            })
            .verify();
    }

    @Test
    void testGetProductsMaxStockByBranchForFranchise_Success_Empty() {
        // Arrange
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.just(Mockito.mock(Franchise.class)));
        when(branchPersistence.findAll()).thenReturn(Flux.empty());
        
        // Act
        Mono<PageResponse<ProductBranchPair>> result = productService.getProductsMaxStockByBranchForFranchise(1L, 0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page != null && 
                page.getContent().isEmpty() && 
                page.getTotalElements() == 0)
            .verifyComplete();
     }

    @Test
    void testGetProductsMaxStockByBranchForFranchise_Success_WithProducts() {
        // Arrange
        when(franchisePersistence.findById(anyLong())).thenReturn(Mono.just(Mockito.mock(Franchise.class)));
        when(branchPersistence.findAll()).thenReturn(Flux.just(
            new Branch(1L, "Sucursal1", 1L),
            new Branch(2L, "Sucursal2", 1L)
        ));
        when(productPersistence.findAllProductsByBranch(1L)).thenReturn(Flux.just(
            new Product(1L, "P1", 10, 1L),
            new Product(2L, "P2", 15, 1L)
        ));
        when(productPersistence.findAllProductsByBranch(2L)).thenReturn(Flux.just(
            new Product(3L, "P3", 20, 2L)
        ));
        
        // Act
        Mono<PageResponse<ProductBranchPair>> result = productService.getProductsMaxStockByBranchForFranchise(1L, 0, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page != null && 
                !page.getContent().isEmpty() && 
                page.getTotalElements() > 0)
            .verifyComplete();
    }
    
    @Test
    void testGetProductsMaxStockByBranchForFranchise_FranchiseNotFound() {
        // Arrange
        when(franchisePersistence.findById(99L)).thenReturn(Mono.empty());
        when(branchPersistence.findAll()).thenReturn(Flux.empty());
        
        // Act
        Mono<PageResponse<ProductBranchPair>> result = productService.getProductsMaxStockByBranchForFranchise(99L, 1, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectError(ErrorNotFound.class)
            .verify();
    }
    
    @Test
    void testGetProductsMaxStockByBranchForFranchise_NullFranchiseId() {
        // Act
        Mono<PageResponse<ProductBranchPair>> result = productService.getProductsMaxStockByBranchForFranchise(null, 1, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
            })
            .verify();
    }
    
    @Test
    void testGetProductsByBranch_Success() {
        // Arrange
        when(branchPersistence.findById(1L)).thenReturn(Mono.just(new Branch(1L, "Sucursal1", 1L)));
        when(productPersistence.findAllProductsByBranch(1L)).thenReturn(Flux.just(
            new Product(1L, "P1", 10, 1L),
            new Product(2L, "P2", 15, 1L)
        ));
        
        // Act
        Mono<PageResponse<Product>> result = productService.getProductsByBranch(1L, 1, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(page -> 
                page != null && 
                page.getContent().size() == 2 && 
                page.getTotalElements() == 2)
            .verifyComplete();
    }
    
    @Test
    void testGetProductsByBranch_BranchNotFound() {
        // Arrange
        when(branchPersistence.findById(99L)).thenReturn(Mono.empty());
        when(productPersistence.findAllProductsByBranch(anyLong())).thenReturn(Flux.empty());
        
        // Act
        Mono<PageResponse<Product>> result = productService.getProductsByBranch(99L, 1, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectError(ErrorNotFound.class)
            .verify();
    }
    
    @Test
    void testGetProductsByBranch_NullBranchId() {
        // Act
        Mono<PageResponse<Product>> result = productService.getProductsByBranch(null, 1, 10);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
            })
            .verify();
    }
    
    @Test
    void testAddProductToBranch_NullName() {
        // Act
        Mono<Product> result = productService.addProductToBranch(null, 5, 1L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
            })
            .verify();
    }
    
    @Test
    void testAddProductToBranch_EmptyName() {
        // Act
        Mono<Product> result = productService.addProductToBranch("", 5, 1L);
        
        // Assert
        StepVerifier.create(result)
            .expectErrorSatisfies(ex -> {
                assertTrue(ex instanceof ErrorBadRequest);
            })
            .verify();
    }

    @Test
    void testUpdateProductName_Success() {
        // Arrange
        Product existingProduct = new Product(1L, "Old Product", 10, 2L);
        Product updatedProduct = new Product(1L, "New Product", 10, 2L);
        
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(existingProduct));
        when(productPersistence.saveProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));
        
        // Act
        Mono<Product> result = productService.updateProductName(1L, "New Product");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(product -> 
                product.getId().equals(1L) && 
                product.getName().equals("New Product") &&
                product.getStock() == 10 &&
                product.getBranchId() == 2L)
            .verifyComplete();
            
        verify(productPersistence).saveProduct(argThat(product -> 
            product.getId().equals(1L) && 
            product.getName().equals("New Product") &&
            product.getStock() == 10 &&
            product.getBranchId() == 2L));
    }
    
    @Test
    void testUpdateProductName_ProductNotFound() {
        // Arrange
        when(productPersistence.findProductById(99L)).thenReturn(Mono.empty());
        
        // Act
        Mono<Product> result = productService.updateProductName(99L, "New Product");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(error -> 
                error instanceof ErrorNotFound && 
                error.getMessage().contains("not found"))
            .verify();
    }
    
    @Test
    void testUpdateProductName_EmptyName() {
        // Act & Assert
        StepVerifier.create(productService.updateProductName(1L, ""))
            .expectErrorMatches(error -> 
                error instanceof ErrorBadRequest && 
                error.getMessage().contains("name"))
            .verify();
    }
    
    @Test
    void testUpdateProductName_NullName() {
        // Act & Assert
        StepVerifier.create(productService.updateProductName(1L, null))
            .expectErrorMatches(error -> 
                error instanceof ErrorBadRequest && 
                error.getMessage().contains("name"))
            .verify();
    }
    
    
    @Test
    void testUpdateProductName_Timeout() {
        // Arrange
        Product existingProduct = new Product(1L, "Old Product", 10, 2L);
        
        when(productPersistence.findProductById(1L)).thenReturn(Mono.just(existingProduct));
        when(productPersistence.saveProduct(any(Product.class)))
            .thenReturn(Mono.just(new Product(1L, "New Product", 10, 2L)).delayElement(Duration.ofSeconds(4)));
        
        // Act
        Mono<Product> result = productService.updateProductName(1L, "New Product");
        
        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(error -> 
                error instanceof ErrorException && 
                error.getMessage().contains("Timeout"))
            .verify();
    }
}
