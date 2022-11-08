package com.example.crypto.exception;

import lombok.Data;

@Data
public class WrongParameterValueException extends RuntimeException {
//    private String description;
//    private Throwable cause;

    public WrongParameterValueException() {
    }

    public WrongParameterValueException(String description) {
        super(description);
//        this.description = description;
    }

    public WrongParameterValueException(String description, Throwable cause) {
        super(description, cause);
//        this.description = description;
//        this.cause = cause;
    }

}
