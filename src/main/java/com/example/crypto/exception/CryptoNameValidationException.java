package com.example.crypto.exception;

import lombok.Data;

@Data
public class CryptoNameValidationException extends RuntimeException {
    public CryptoNameValidationException() {
    }

    public CryptoNameValidationException(String description) {
        super(description);
    }

    public CryptoNameValidationException(String description, Throwable cause) {
        super(description, cause);
    }

}
