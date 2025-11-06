package org.challenge6.javaspark.exceptions;

import java.util.HashMap;
import java.util.Map;

class ValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public ValidationException(String field, String message) {
        super("Validation failed");
        this.errors = Map.of(field, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}