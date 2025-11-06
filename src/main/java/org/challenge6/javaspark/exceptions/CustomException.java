package org.challenge6.javaspark.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepción personalizada base
 */
public class CustomException extends RuntimeException {
    private final int status;
    private final String error;
    private final Map<String, Object> details;

    public CustomException(int status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
        this.details = new HashMap<>();
    }

    public CustomException(int status, String error, String message, Map<String, Object> details) {
        super(message);
        this.status = status;
        this.error = error;
        this.details = details != null ? details : new HashMap<>();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
