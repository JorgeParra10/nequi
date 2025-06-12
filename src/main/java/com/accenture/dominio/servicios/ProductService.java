package com.accenture.dominio.servicios;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.accenture.dominio.excepcion.ErrorException;
import com.accenture.dominio.excepcion.ErrorNotFound;
import com.accenture.dominio.excepcion.ExceptionAlreadyExist;
import com.accenture.dominio.interfaces.IBranchPersistence;
import com.accenture.dominio.interfaces.IFranchisePersistence;
import com.accenture.dominio.interfaces.IProductPersistence;
import com.accenture.dominio.interfaces.IProductService;
import com.accenture.dominio.model.PageResponse;
import com.accenture.dominio.model.Product;
import com.accenture.dominio.model.responses.ProductBranchPair;
import com.accenture.dominio.util.ConstantsDomain;
import com.accenture.dominio.util.validator.ReactiveValidator;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductPersistence productPersistence;
    private final IBranchPersistence branchPersistence;
    private final IFranchisePersistence franchisePersistence;

    @Override
    public Mono<Product> addProductToBranch(String name, Integer stock, Long branchId) {
        return ReactiveValidator.validateNotEmpty(name, ConstantsDomain.ERROR_PRODUCT_NAME_NULL)
            .flatMap(validName -> ReactiveValidator.validate(stock, s -> s != null && s > 0, ConstantsDomain.ERROR_PRODUCT_STOCK_NEGATIVE)
                .flatMap(validStock -> ReactiveValidator.validateNotNull(branchId, ConstantsDomain.ERROR_BRANCH_NOT_FOUND)
                    .flatMap(validBranchId -> branchPersistence.findById(validBranchId)
                        .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_BRANCH_NOT_FOUND + validBranchId)))
                        .flatMap(branch -> productPersistence.findAllProductsByBranch(validBranchId)
                            .filter(product -> product.getName().equalsIgnoreCase(validName))
                            .hasElements()
                            .flatMap(exists -> exists
                                ? Mono.<Product>error(new ExceptionAlreadyExist(ConstantsDomain.ERROR_PRODUCT_ALREADY_EXISTS))
                                : productPersistence.saveProduct(new Product(null, validName, validStock, validBranchId))
                            )
                        )
                    )
                )
            )
            .timeout(Duration.ofSeconds(5))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_ADD_PRODUCT_TO_BRANCH));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<Void> removeProductFromBranch(Long productId) {
        return ReactiveValidator.validateNotNull(productId, ConstantsDomain.ERROR_ID_NULL)
            .flatMap(validProductId -> productPersistence.findProductById(validProductId)
                .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_PRODUCT_NOT_FOUND + validProductId)))
                .flatMap(product -> productPersistence.deleteProduct(validProductId))
            )
            .timeout(Duration.ofSeconds(3))
            .retry(1)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_REMOVE_PRODUCT_FROM_BRANCH));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<Product> updateProductStock(Long productId, Integer stock) {
        return ReactiveValidator.validateNotNull(productId, ConstantsDomain.ERROR_ID_NULL)
            .flatMap(validProductId -> ReactiveValidator.validate(stock, s -> s != null && s > 0, ConstantsDomain.ERROR_PRODUCT_STOCK_NEGATIVE)
                .flatMap(validStock -> productPersistence.findProductById(validProductId)
                    .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_PRODUCT_NOT_FOUND + validProductId)))
                    .flatMap(product -> {
                        product.setStock(validStock);
                        return productPersistence.saveProduct(product);
                    })
                )
            )
            .timeout(Duration.ofSeconds(3))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_UPDATE_PRODUCT_STOCK));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<PageResponse<ProductBranchPair>> getProductsMaxStockByBranchForFranchise(Long franchiseId, int page, int size) {
        return ReactiveValidator.validateNotNull(franchiseId, ConstantsDomain.ERROR_FRANCHISE_ID_NULL)
            .flatMap(validFranchiseId -> franchisePersistence.findById(validFranchiseId)
                .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_FRANCHISE_NOT_FOUND + validFranchiseId)))
                .then(
                    branchPersistence.findAll()
                        .filter(branch -> branch.getFranchiseId().equals(validFranchiseId))
                        .flatMap(branch -> productPersistence.findAllProductsByBranch(branch.getId())
                            .collectList()
                            .filter(list -> !list.isEmpty())
                            .map(list -> {
                                Product maxStockProduct = list.stream().max(java.util.Comparator.comparing(Product::getStock)).orElse(null);
                                if (maxStockProduct != null) {
                                    Product productWithoutBranchId = new Product(maxStockProduct.getId(), maxStockProduct.getName(), maxStockProduct.getStock(), null);
                                    return new ProductBranchPair(productWithoutBranchId, branch);
                                }
                                return null;
                            })
                            .filter(pair -> pair != null)
                        )
                        .collectList()
                        .map(list -> {
                            int totalElements = list.size();
                            int totalPages = (int) Math.ceil((double) totalElements / size);
                            int fromIndex = Math.max(0, Math.min((page - 1) * size, totalElements));
                            int toIndex = Math.max(0, Math.min(fromIndex + size, totalElements));
                            java.util.List<ProductBranchPair> pagedList = list.subList(fromIndex, toIndex);
                            return new PageResponse<>(pagedList, page, size, totalElements, totalPages);
                        })
                )
            )
            .timeout(Duration.ofSeconds(10))
            .retry(1)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_OBTAINING_PRODUCTS));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<PageResponse<Product>> getProductsByBranch(Long branchId, int page, int size) {
        return ReactiveValidator.validateNotNull(branchId, ConstantsDomain.ERROR_BRANCH_NOT_FOUND)
            .flatMap(validBranchId -> branchPersistence.findById(validBranchId)
                .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_BRANCH_NOT_FOUND + validBranchId)))
                .then(productPersistence.findAllProductsByBranch(validBranchId)
                    .collectList()
                    .flatMap(list -> {
                        int totalElements = list.size();
                        int totalPages = (int) Math.ceil((double) totalElements / size);
                        int fromIndex = Math.max(0, Math.min((page - 1) * size, totalElements));
                        int toIndex = Math.max(0, Math.min(fromIndex + size, totalElements));
                        java.util.List<Product> pagedList = list.subList(fromIndex, toIndex);
                        PageResponse<Product> response = new PageResponse<>(pagedList, page, size, totalElements, totalPages);
                        return Mono.just(response);
                    })
                )
            )
            .timeout(Duration.ofSeconds(5))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_OBTAINING_PRODUCTS));
                }
                return Mono.error(e);
            });
    }

    @Override
    public Mono<Product> updateProductName(Long productId, String name) {
        return ReactiveValidator.validateNotNull(productId, ConstantsDomain.ERROR_ID_NULL)
            .flatMap(validProductId -> ReactiveValidator.validateNotEmpty(name, ConstantsDomain.ERROR_PRODUCT_NAME_NULL)
                .flatMap(validName -> productPersistence.findProductById(validProductId)
                    .switchIfEmpty(Mono.error(new ErrorNotFound(ConstantsDomain.ERROR_PRODUCT_NOT_FOUND + validProductId)))
                    .flatMap(product -> {
                        product.setName(validName);
                        return productPersistence.saveProduct(product);
                    })
                )
            )
            .timeout(Duration.ofSeconds(3))
            .retry(2)
            .onErrorResume(e -> {
                if (e instanceof TimeoutException) {
                    return Mono.error(new ErrorException(ConstantsDomain.ERROR_TIMEOUT_UPDATE_PRODUCT_NAME));
                }
                return Mono.error(e);
            });
    }
}
