package org.challenge6.javaspark.exceptions;

import java.util.Map;

class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super(400, "Bad Request", message);
    }

    public BadRequestException(String message, Map<String, Object> details) {
        super(400, "Bad Request", message, details);
    }
}