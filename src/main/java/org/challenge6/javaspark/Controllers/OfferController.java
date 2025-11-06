package org.challenge6.javaspark.Controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.challenge6.javaspark.config.LocalDateTimeAdapter;
import org.challenge6.javaspark.entity.Offer;
import org.challenge6.javaspark.services.OfferService;
import org.challenge6.javaspark.websocket.AuctionWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OfferController {
    private final OfferService offerService;
    private final Gson gson;
    private static final Logger httpLogger = LoggerFactory.getLogger("HTTP_LOGGER");

    public OfferController() {
        this.offerService = OfferService.getInstance();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public String getAllOffers(Request req, Response res) {
        httpLogger.info("GET /api/offers - Method: {} - IP: {}", req.requestMethod(), req.ip());

        try {
            List<Offer> offers = offerService.getAllOffers();
            res.type("application/json");
            res.status(200);
            return gson.toJson(offers);
        } catch (Exception e) {
            httpLogger.error("Error getting all offers", e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting offers", "message", e.getMessage()));
        }
    }

    public String getOffersByItem(Request req, Response res) {
        String itemId = req.params(":itemId");
        httpLogger.info("GET /api/items/{}/offers - Method: {} - IP: {}", itemId, req.requestMethod(), req.ip());

        try {
            List<Offer> offers = offerService.getOffersByItem(itemId);
            res.type("application/json");
            res.status(200);
            return gson.toJson(offers);
        } catch (Exception e) {
            httpLogger.error("Error getting offers for item: {}", itemId, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting offers", "message", e.getMessage()));
        }
    }

    public String getOffersByUser(Request req, Response res) {
        String userId = req.params(":userId");
        httpLogger.info("GET /api/users/{}/offers - Method: {} - IP: {}", userId, req.requestMethod(), req.ip());

        try {
            List<Offer> offers = offerService.getOffersByUser(Integer.parseInt(userId));
            res.type("application/json");
            res.status(200);
            return gson.toJson(offers);
        } catch (Exception e) {
            httpLogger.error("Error getting offers for user: {}", userId, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting offers", "message", e.getMessage()));
        }
    }

    public String getOfferById(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("GET /api/offers/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            Optional<Offer> offer = offerService.getOfferById(Integer.parseInt(id));
            res.type("application/json");

            if (offer.isPresent()) {
                res.status(200);
                return gson.toJson(offer.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Offer doesn't exist", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error getting offer: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting offer", "message", e.getMessage()));
        }
    }

    public String addOffer(Request req, Response res) {
        httpLogger.info("POST /api/offers - Method: {} - IP: {} - Body: {}",
                req.requestMethod(), req.ip(), req.body());

        try {
            Offer offer = gson.fromJson(req.body(), Offer.class);
            Offer createdOffer = offerService.addOffer(offer);

            // Broadcast via WebSocket a todas las conexiones del item
            Map<String, Object> wsMessage = new HashMap<>();
            wsMessage.put("type", "new_offer");
            wsMessage.put("offer", createdOffer);
            wsMessage.put("itemId", createdOffer.getItemId());

            AuctionWebSocketHandler.broadcastToItem(createdOffer.getItemId(), wsMessage);
            httpLogger.info("WebSocket broadcast sent for item: {}", createdOffer.getItemId());

            res.type("application/json");
            res.status(201);
            return gson.toJson(createdOffer);
        } catch (Exception e) {
            httpLogger.error("Error creating offer: {}", e.getMessage());
            res.status(400);
            return gson.toJson(Map.of("error", "Error creating offer", "message", e.getMessage()));
        }
    }

    public String deleteOffer(Request req, Response res) {
        String id = req.params(":id");
        httpLogger.info("DELETE /api/offers/{} - Method: {} - IP: {}", id, req.requestMethod(), req.ip());

        try {
            // Obtener la oferta antes de eliminarla para el broadcast
            Optional<Offer> offerToDelete = offerService.getOfferById(Integer.parseInt(id));

            boolean deleted = offerService.deleteOffer(Integer.parseInt(id));
            res.type("application/json");

            if (deleted) {
                // Broadcast de eliminación via WebSocket
                if (offerToDelete.isPresent()) {
                    Map<String, Object> wsMessage = new HashMap<>();
                    wsMessage.put("type", "offer_deleted");
                    wsMessage.put("offerId", Integer.parseInt(id));
                    wsMessage.put("itemId", offerToDelete.get().getItemId());

                    AuctionWebSocketHandler.broadcastToItem(offerToDelete.get().getItemId(), wsMessage);
                }

                res.status(200);
                return gson.toJson(Map.of("message", "Offer deleted successfully", "id", id));
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "Offer not found", "id", id));
            }
        } catch (Exception e) {
            httpLogger.error("Error deleting offer: {}", id, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error deleting offer", "message", e.getMessage()));
        }
    }

    public String getHighestOffer(Request req, Response res) {
        String itemId = req.params(":itemId");
        httpLogger.info("GET /api/items/{}/highest-offer - Method: {} - IP: {}", itemId, req.requestMethod(), req.ip());

        try {
            Optional<Offer> offer = offerService.getHighestOfferForItem(itemId);
            res.type("application/json");

            if (offer.isPresent()) {
                res.status(200);
                return gson.toJson(offer.get());
            } else {
                res.status(404);
                return gson.toJson(Map.of("error", "No offers found for this item", "itemId", itemId));
            }
        } catch (Exception e) {
            httpLogger.error("Error getting highest offer for item: {}", itemId, e);
            res.status(500);
            return gson.toJson(Map.of("error", "Error getting highest offer", "message", e.getMessage()));
        }
    }
}