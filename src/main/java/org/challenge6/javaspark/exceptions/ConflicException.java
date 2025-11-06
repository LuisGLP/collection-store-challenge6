package org.challenge6.javaspark.exceptions;

import java.util.Map;

class ConflictException extends CustomException {
    public ConflictException(String message) {
        super(409, "Conflict", message);
    }

    public ConflictException(String resource, String field, String value) {
        super(409, "Conflict",
                String.format("%s with %s '%s' already exists", resource, field, value),
                Map.of("resource", resource, "field", field, "value", value));
    }
}