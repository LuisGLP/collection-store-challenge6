package org.challenge6.javaspark.exceptions;

import java.util.Map;

class NotFoundException extends CustomException {
    public NotFoundException(String resource, String id) {
        super(404, "Not Found",
                String.format("%s with id '%s' was not found", resource, id),
                Map.of("resource", resource, "id", id));
    }

    public NotFoundException(String message) {
        super(404, "Not Found", message);
    }
}
