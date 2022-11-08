package com.example.crypto.exception;

import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
    }

    public NotFoundException(String description) {
        super(description);
    }

    public NotFoundException(String description, Throwable cause) {
        super(description, cause);
    }
}
