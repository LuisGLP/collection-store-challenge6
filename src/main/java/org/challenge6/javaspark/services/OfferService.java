package org.challenge6.javaspark.services;

import org.challenge6.javaspark.config.DatabaseConfig;
import org.challenge6.javaspark.entity.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OfferService {
    private static OfferService instance;
    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    private OfferService() {
    }

    public static synchronized OfferService getInstance() {
        if (instance == null) {
            instance = new OfferService();
        }
        return instance;
    }

    private Offer mapResultSetToOffer(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setId(rs.getInt("id"));
        offer.setUserId(rs.getInt("user_id"));
        offer.setItemId(rs.getString("item_id"));
        offer.setAmount(rs.getBigDecimal("amount"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            offer.setCreatedAt(createdAt.toLocalDateTime());
        }

        // Si hay columnas adicionales (como en JOIN)
        try {
            offer.setUserName(rs.getString("user_name"));
        } catch (SQLException e) {
            // Columna no existe, ignorar
        }

        try {
            offer.setItemName(rs.getString("item_name"));
        } catch (SQLException e) {
            // Columna no existe, ignorar
        }

        return offer;
    }

    public List<Offer> getAllOffers() {
        List<Offer> offers = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, i.name as item_name " +
                "FROM offers o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN items i ON o.item_id = i.id " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                offers.add(mapResultSetToOffer(rs));
            }
            logger.info("Retrieved {} offers", offers.size());

        } catch (SQLException e) {
            logger.error("Error retrieving all offers", e);
            throw new RuntimeException("Error al obtener ofertas", e);
        }

        return offers;
    }

    public List<Offer> getOffersByItem(String itemId) {
        List<Offer> offers = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, i.name as item_name " +
                "FROM offers o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN items i ON o.item_id = i.id " +
                "WHERE o.item_id = ? " +
                "ORDER BY o.amount DESC, o.created_at ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                offers.add(mapResultSetToOffer(rs));
            }
            logger.info("Retrieved {} offers for item: {}", offers.size(), itemId);

        } catch (SQLException e) {
            logger.error("Error retrieving offers for item: {}", itemId, e);
            throw new RuntimeException("Error al obtener ofertas del item", e);
        }

        return offers;
    }

    public List<Offer> getOffersByUser(Integer userId) {
        List<Offer> offers = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, i.name as item_name " +
                "FROM offers o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN items i ON o.item_id = i.id " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                offers.add(mapResultSetToOffer(rs));
            }
            logger.info("Retrieved {} offers for user: {}", offers.size(), userId);

        } catch (SQLException e) {
            logger.error("Error retrieving offers for user: {}", userId, e);
            throw new RuntimeException("Error al obtener ofertas del usuario", e);
        }

        return offers;
    }

    public Optional<Offer> getOfferById(Integer id) {
        String sql = "SELECT o.*, u.name as user_name, i.name as item_name " +
                "FROM offers o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN items i ON o.item_id = i.id " +
                "WHERE o.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Offer offer = mapResultSetToOffer(rs);
                logger.info("Offer found: {}", id);
                return Optional.of(offer);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving offer: {}", id, e);
            throw new RuntimeException("Error al obtener oferta", e);
        }

        logger.warn("Offer not found: {}", id);
        return Optional.empty();
    }

    public Offer addOffer(Offer offer) {
        String sql = "INSERT INTO offers (user_id, item_id, amount) VALUES (?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, offer.getUserId());
            pstmt.setString(2, offer.getItemId());
            pstmt.setBigDecimal(3, offer.getAmount());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Offer createdOffer = mapResultSetToOffer(rs);
                logger.info("Offer created: {}", createdOffer.getId());
                return getOfferById(createdOffer.getId()).orElseThrow();
            }

        } catch (SQLException e) {
            logger.error("Error creating offer", e);
            if (e.getMessage().contains("foreign key")) {
                throw new RuntimeException("Usuario o item no existen", e);
            }
            throw new RuntimeException("Error al crear oferta", e);
        }

        throw new RuntimeException("No se pudo crear la oferta");
    }

    public boolean deleteOffer(Integer id) {
        String sql = "DELETE FROM offers WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Offer deleted: {}", id);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error deleting offer: {}", id, e);
            throw new RuntimeException("Error al eliminar oferta", e);
        }

        logger.warn("Offer not found for deletion: {}", id);
        return false;
    }

    public Optional<Offer> getHighestOfferForItem(String itemId) {
        String sql = "SELECT o.*, u.name as user_name, i.name as item_name " +
                "FROM offers o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN items i ON o.item_id = i.id " +
                "WHERE o.item_id = ? " +
                "ORDER BY o.amount DESC, o.created_at ASC " +
                "LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToOffer(rs));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving highest offer for item: {}", itemId, e);
            throw new RuntimeException("Error al obtener oferta más alta", e);
        }

        return Optional.empty();
    }
}