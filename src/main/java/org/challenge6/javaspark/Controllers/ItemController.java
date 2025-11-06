package org.challenge6.javaspark.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.challenge6.javaspark.entity.Item;
import org.challenge6.javaspark.services.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemController {
    private final ItemService itemService;
    private final Gson gson;
    private static final Logger httpLogger = LoggerFactory.getLogger("HTTP_LOGGER");

    public ItemController() {
        this.itemService = ItemService.getInstance();
        this.gson = new GsonBuilder().create();
    }

    public String getAllItems(Request req, Response res) {
        httpLogger.info("GET /api/items - Method: {} - IP: {}", req.requestMethod(), req.ip());

        try {
            String status = req.queryParams("status");
            List<Item> items;

            if (status != null && !status.isEmpty()) {
                items = itemService.getItemsByStatus(status);
            } else {
                items = itemService.getAllItems();
            }

            res.type("application/json");
            res.status(200);
            return gson.toJson(items);
        } catch (Exception e) {
            httpLogger.error("Error getting all items", e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting items", "message", e.getMessage()));
        }
    }

    public String getItemById(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("GET /api/items/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            Optional<Item> item = itemService.getItemById(id);
            res.type("application/json");

            if (item.isPresent()) {
                res.status(200);
                return gson.toJson(item.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Item doesn't exist", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error getting item: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting item", "message", e.getMessage()));
        }
    }

    public String addItem(Request req, Response res) {
        httpLogger.info("POST /api/items - Method: {} - IP: {} - Body: {}",
                req.requestMethod(), req.ip(), req.body());

        try {
            Item item = gson.fromJson(req.body(), Item.class);
            Item createdItem = itemService.addItem(item);

            res.type("application/json");
            res.status(201);
            return gson.toJson(createdItem);
        } catch (Exception e) {
            httpLogger.error("Error creating item: {}", e.getMessage());
            res.status(400);
            return gson.toJson(Map.of("error", "Error creating item", "message", e.getMessage()));
        }
    }

    public String updateItem(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("PUT /api/items/{} - Method: {} - IP: {} - Body: {}",
                id, req.requestMethod(), req.ip(), req.body());

        try {
            Item item = gson.fromJson(req.body(), Item.class);
            Optional<Item> updatedItem = itemService.updateItem(id, item);

            res.type("application/json");

            if (updatedItem.isPresent()) {
                res.status(200);
                return gson.toJson(updatedItem.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Can't find item", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error updating item: {}", e.getMessage());
            res.status(400);
            return gson.toJson(Map.of("error", "Error updating item", "message", e.getMessage()));
        }
    }

    public String deleteItem(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("DELETE /api/items/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            boolean deleted = itemService.deleteItem(id);
            res.type("application/json");

            if (deleted) {
                res.status(200);
                return gson.toJson(Map.of("message", "Item deleted successfully", "id", id));
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Item not found", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error deleting item: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error deleting item", "message", e.getMessage()));
        }
    }

    public String checkItemExists(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("OPTIONS /api/items/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            boolean exists = itemService.itemExists(id);
            res.type("application/json");
            res.status(200);
            return gson.toJson(Map.of("exists", exists, "id", id));
        } catch (Exception e) {
            httpLogger.error("Error verifying item: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error verifying item", "message", e.getMessage()));
        }
    }
}