package org.challenge6.javaspark.exceptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.challenge6.javaspark.config.LocalDateTimeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionHandler {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Maneja excepciones de recursos no encontrados (404)
     */
    public static String handleNotFound(Request req, Response res) {
        logger.warn("404 Not Found - Path: {} - Method: {}", req.pathInfo(), req.requestMethod());

        res.status(404);
        res.type("application/json");

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", "The requested resource was not found");
        error.put("path", req.pathInfo());
        error.put("method", req.requestMethod());
        error.put("timestamp", LocalDateTime.now());

        return gson.toJson(error);
    }

    /**
     * Maneja errores internos del servidor (500)
     */
    public static String handleInternalError(Exception e, Request req, Response res) {
        logger.error("500 Internal Server Error - Path: {} - Method: {}",
                req.pathInfo(), req.requestMethod(), e);

        res.status(500);
        res.type("application/json");

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred on the server");
        error.put("path", req.pathInfo());
        error.put("method", req.requestMethod());
        error.put("timestamp", LocalDateTime.now());

        // Solo incluir detalles en desarrollo
        if (isDevelopmentMode()) {
            error.put("details", e.getMessage());
            error.put("exceptionType", e.getClass().getSimpleName());
        }

        return gson.toJson(error);
    }

    /**
     * Maneja excepciones personalizadas
     */
    public static String handleCustomException(CustomException e, Request req, Response res) {
        logger.warn("Custom Exception - Status: {} - Path: {} - Message: {}",
                e.getStatus(), req.pathInfo(), e.getMessage());

        res.status(e.getStatus());
        res.type("application/json");

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", e.getStatus());
        error.put("error", e.getError());
        error.put("message", e.getMessage());
        error.put("path", req.pathInfo());
        error.put("method", req.requestMethod());
        error.put("timestamp", LocalDateTime.now());

        if (e.getDetails() != null) {
            error.put("details", e.getDetails());
        }

        return gson.toJson(error);
    }

    /**
     * Maneja excepciones de validación (400)
     */
    public static String handleValidationException(ValidationException e, Request req, Response res) {
        logger.warn("Validation Exception - Path: {} - Errors: {}", req.pathInfo(), e.getErrors());

        res.status(400);
        res.type("application/json");

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", "Validation failed");
        error.put("errors", e.getErrors());
        error.put("path", req.pathInfo());
        error.put("method", req.requestMethod());
        error.put("timestamp", LocalDateTime.now());

        return gson.toJson(error);
    }

    /**
     * Maneja errores de base de datos
     */
    public static String handleDatabaseException(DatabaseException e, Request req, Response res) {
        logger.error("Database Exception - Path: {} - Operation: {}",
                req.pathInfo(), e.getOperation(), e);

        res.status(500);
        res.type("application/json");

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 500);
        error.put("error", "Database Error");
        error.put("message", "A database error occurred");
        error.put("operation", e.getOperation());
        error.put("path", req.pathInfo());
        error.put("method", req.requestMethod());
        error.put("timestamp", LocalDateTime.now());

        if (isDevelopmentMode()) {
            error.put("details", e.getMessage());
        }

        return gson.toJson(error);
    }

    /**
     * Crea una respuesta de error genérica
     */
    public static String createErrorResponse(int status, String error, String message, Request req, Response res) {
        res.status(status);
        res.type("application/json");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", req.pathInfo());
        errorResponse.put("method", req.requestMethod());
        errorResponse.put("timestamp", LocalDateTime.now());

        return gson.toJson(errorResponse);
    }

    /**
     * Verifica si estamos en modo desarrollo
     */
    private static boolean isDevelopmentMode() {
        String env = System.getProperty("app.env", "development");
        return "development".equalsIgnoreCase(env);
    }
}