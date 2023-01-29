package com.ming.mingcommerce.product.exception;

public class ProductException extends RuntimeException {
    public ProductException() {
        super();
    }

    public ProductException(String message) {
        super(message);
    }

    public static class ProductNotFoundException extends ProductException {
        public ProductNotFoundException() {
            super();
        }

        public ProductNotFoundException(String message) {
            super(message);
        }
    }
}
