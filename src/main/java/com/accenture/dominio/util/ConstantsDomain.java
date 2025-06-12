package com.accenture.dominio.util;

public final  class ConstantsDomain {
    
    private ConstantsDomain(){
        throw new IllegalStateException("Utility class");
    }

    public static final String ERROR_NAME_AND_FRANCHISE_ID_NULL = "Name and franchiseId cannot be null";
    public static final String ERROR_BRANCH_ALREADY_EXISTS = "Branch name already exists";
    public static final String ERROR_FRANCHISE_NOT_FOUND = "Franchise not found: ";
    public static final String ERROR_NAME_NULL = "Name cannot be null";
    public static final String ERROR_FRANCHISE_ALREADY_EXISTS = "Franchise already exists";
    public static final String ERROR_FRANCHISE_NOT_FOUND_ID = "Franchise not found: ";
    public static final String ERROR_PRODUCT_NAME_NULL = "Product name cannot be null";
    public static final String ERROR_PRODUCT_ALREADY_EXISTS = "Product already exists";
    public static final String ERROR_PRODUCT_NOT_FOUND = "Product not found: ";
    public static final String ERROR_PRODUCT_STOCK_NEGATIVE = "Stock cannot be negative";
    public static final String ERROR_BRANCH_NOT_FOUND = "Branch not found: ";
    public static final String ERROR_PRODUCT_NOT_IN_BRANCH = "Product is not assigned to the branch";
    public static final String ERROR_FRANCHISE_ID_NULL = "Franchise id cannot be null";
    public static final String ERROR_ID_NULL = "ID cannot be null";
    public static final String ERROR_TIMEOUT_OBTAINING_FRANCHISES = "Timeout when obtaining franchises";
    public static final String ERROR_TIMEOUT_OBTAINING_BRANCHES = "Timeout when obtaining branches";
    public static final String ERROR_TIMEOUT_UPDATE_PRODUCT_NAME = "Timeout when updating product name";
    public static final String ERROR_TIMEOUT_OBTAINING_PRODUCTS = "Timeout when obtaining products";
    public static final String ERROR_TIMEOUT_ADD_PRODUCT_TO_BRANCH = "Timeout when adding product to branch";
    public static final String ERROR_TIMEOUT_REMOVE_PRODUCT_FROM_BRANCH = "Timeout when removing product from branch";
    public static final String ERROR_TIMEOUT_UPDATE_PRODUCT_STOCK = "Timeout when updating product stock";
}
