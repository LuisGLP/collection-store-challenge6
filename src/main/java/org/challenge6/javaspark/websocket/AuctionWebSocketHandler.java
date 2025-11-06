package org.challenge6.javaspark.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.challenge6.javaspark.config.LocalDateTimeAdapter;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class AuctionWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuctionWebSocketHandler.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // Mapa de itemId -> Queue de Sessions
    private static final Map<String, Queue<Session>> itemSessions = new ConcurrentHashMap<>();

    // Cola global de todas las sesiones
    private static final Queue<Session> allSessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        String itemId = getItemIdFromSession(session);

        if (itemId != null) {
            // Agregar a las sesiones específicas del item
            itemSessions.computeIfAbsent(itemId, k -> new ConcurrentLinkedQueue<>()).add(session);
            logger.info("WebSocket connected for item: {} - Total connections: {}",
                    itemId, itemSessions.get(itemId).size());

            // Enviar mensaje de bienvenida
            sendMessage(session, Map.of(
                    "type", "connected",
                    "itemId", itemId,
                    "message", "Connected to auction updates"
            ));
        } else {
            // Conexión global
            allSessions.add(session);
            logger.info("WebSocket connected (global) - Total connections: {}", allSessions.size());

            sendMessage(session, Map.of(
                    "type", "connected",
                    "message", "Connected to all auctions"
            ));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String itemId = getItemIdFromSession(session);

        if (itemId != null && itemSessions.containsKey(itemId)) {
            itemSessions.get(itemId).remove(session);
            logger.info("WebSocket closed for item: {} - Remaining: {}",
                    itemId, itemSessions.get(itemId).size());

            // Limpiar si no hay más sesiones
            if (itemSessions.get(itemId).isEmpty()) {
                itemSessions.remove(itemId);
            }
        } else {
            allSessions.remove(session);
            logger.info("WebSocket closed (global) - Remaining: {}", allSessions.size());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        logger.info("WebSocket message received: {}", message);

        try {
            Map<String, Object> data = gson.fromJson(message, Map.class);
            String type = (String) data.get("type");

            if ("ping".equals(type)) {
                sendMessage(session, Map.of("type", "pong"));
            }
        } catch (Exception e) {
            logger.error("Error processing WebSocket message", e);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        logger.error("WebSocket error", error);
    }

    // Metodo para obtener el itemId de la query string de la sesión
    private String getItemIdFromSession(Session session) {
        try {
            String query = session.getUpgradeRequest().getRequestURI().getQuery();
            if (query != null && query.startsWith("itemId=")) {
                return query.substring(7);
            }
        } catch (Exception e) {
            logger.error("Error extracting itemId from session", e);
        }
        return null;
    }

    // Metodo para enviar un mensaje a una sesión específica
    private void sendMessage(Session session, Object data) {
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString(gson.toJson(data));
            } catch (IOException e) {
                logger.error("Error sending WebSocket message", e);
            }
        }
    }

    // Método público para broadcast a todas las sesiones de un item específico
    public static void broadcastToItem(String itemId, Object data) {
        Queue<Session> sessions = itemSessions.get(itemId);
        if (sessions != null) {
            String message = gson.toJson(data);
            sessions.removeIf(session -> {
                if (session.isOpen()) {
                    try {
                        session.getRemote().sendString(message);
                        return false;
                    } catch (IOException e) {
                        logger.error("Error broadcasting to session", e);
                        return true;
                    }
                }
                return true;
            });
            logger.info("Broadcast to {} sessions for item: {}", sessions.size(), itemId);
        }
    }

    // Método público para broadcast global
    public static void broadcastToAll(Object data) {
        String message = gson.toJson(data);
        allSessions.removeIf(session -> {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(message);
                    return false;
                } catch (IOException e) {
                    logger.error("Error broadcasting to session", e);
                    return true;
                }
            }
            return true;
        });
        logger.info("Broadcast to {} global sessions", allSessions.size());
    }

    // Obtener número de conexiones activas para un item
    public static int getActiveConnections(String itemId) {
        Queue<Session> sessions = itemSessions.get(itemId);
        return sessions != null ? sessions.size() : 0;
    }

    // Obtener número total de conexiones
    public static int getTotalConnections() {
        return itemSessions.values().stream()
                .mapToInt(Queue::size)
                .sum() + allSessions.size();
    }
}