package org.challenge6.javaspark.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.challenge6.javaspark.config.LocalDateTimeAdapter;
import org.challenge6.javaspark.entity.User;
import org.challenge6.javaspark.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserController {
    private final UserService userService;
    private final Gson gson;
    private static final Logger httpLogger = LoggerFactory.getLogger("HTTP_LOGGER");

    public UserController() {
        this.userService = UserService.getInstance();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();;
    }

    // JSON API - Obtener todos los usuarios
    public String getAllUsers(Request req, Response res) {
        httpLogger.info("GET /api/users - Method: {} - IP: {}", req.requestMethod(), req.ip());

        try {
            List<User> users = userService.getAllUsers();
            res.type("application/json");
            res.status(200);
            return gson.toJson(users);
        } catch (Exception e) {
            httpLogger.error("Error getting all users", e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting all users", "message", e.getMessage()));
        }
    }

    // JSON API - Obtener usuario por ID
    public String getUserById(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("GET /api/users/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            Optional<User> user = userService.getUserById(id);
            res.type("application/json");

            if (user.isPresent()) {
                res.status(200);
                return gson.toJson(user.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "User doesnt exist", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error get user: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting user", "message", e.getMessage()));
        }
    }

    // JSON API - Crear usuario
    public String addUser(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("POST /api/users/{} - Method: {} - IP: {} - Body: {}",
                id, req.requestMethod(), req.ip(), req.body());

        try {
            User user = gson.fromJson(req.body(), User.class);
            user.setId(Integer.parseInt(id));
            User createdUser = userService.addUser(user);

            res.type("application/json");
            res.status(201);
            return gson.toJson(createdUser);
        } catch (Exception e) {
            httpLogger.error("Error creating user: {}", e.getMessage());
            res.status(400);
            return gson.toJson(Map.of("error", "Error creating user", "message", e.getMessage()));
        }
    }

    // JSON API - Actualizar usuario
    public String updateUser(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("PUT /api/users/{} - Method: {} - IP: {} - Body: {}",
                id, req.requestMethod(), req.ip(), req.body());

        try {
            User user = gson.fromJson(req.body(), User.class);
            Optional<User> updatedUser = userService.updateUser(id, user);

            res.type("application/json");

            if (updatedUser.isPresent()) {
                res.status(200);
                return gson.toJson(updatedUser.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Cant find user", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error updating user: {}", e.getMessage());
            res.status(400);
            return gson.toJson(Map.of("error", "Error updating user", "message", e.getMessage()));
        }
    }

    // JSON API - Verificar si existe usuario
    public String checkUserExists(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("OPTIONS /api/users/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            boolean exists = userService.userExists(id);
            res.type("application/json");
            res.status(200);
            return gson.toJson(Map.of("exists", exists, "id", id));
        } catch (Exception e) {
            httpLogger.error("Error verify user: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error verify user", "message", e.getMessage()));
        }
    }

    // JSON API - Eliminar usuario
    public String deleteUser(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("DELETE /api/users/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            boolean deleted = userService.deleteUser(id);
            res.type("application/json");

            if (deleted) {
                res.status(200);
                return gson.toJson(Map.of("message", "User delete sucessfully", "id", id));
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "User delete sucessfully", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error deleting user: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error deleting user", "message", e.getMessage()));
        }
    }
}