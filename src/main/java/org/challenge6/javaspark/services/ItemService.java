package org.challenge6.javaspark.services;

import org.challenge6.javaspark.config.DatabaseConfig;
import org.challenge6.javaspark.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemService {
    private static ItemService instance;
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private ItemService() {
    }

    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getString("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setBasePrice(rs.getBigDecimal("base_price"));
        item.setStatus(rs.getString("status"));
        return item;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
            logger.info("Retrieved {} items", items.size());

        } catch (SQLException e) {
            logger.error("Error retrieving all items", e);
            throw new RuntimeException("Error al obtener items", e);
        }

        return items;
    }

    public List<Item> getItemsByStatus(String status) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE status = ? ORDER BY id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
            logger.info("Retrieved {} items with status: {}", items.size(), status);

        } catch (SQLException e) {
            logger.error("Error retrieving items by status: {}", status, e);
            throw new RuntimeException("Error al obtener items por estado", e);
        }

        return items;
    }

    public Optional<Item> getItemById(String id) {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Item item = mapResultSetToItem(rs);
                logger.info("Item found: {}", id);
                return Optional.of(item);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving item: {}", id, e);
            throw new RuntimeException("Error al obtener item", e);
        }

        logger.warn("Item not found: {}", id);
        return Optional.empty();
    }

    public Item addItem(Item item) {
        String sql = "INSERT INTO items (id, name,description, base_price, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getDescription());
            pstmt.setBigDecimal(4, item.getBasePrice());
            pstmt.setString(5, item.getStatus() != null ? item.getStatus() : "active");

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Item created: {}", item.getId());
                return getItemById(item.getId()).orElseThrow();
            }

        } catch (SQLException e) {
            logger.error("Error creating item", e);
            if (e.getMessage().contains("duplicate key")) {
                throw new RuntimeException("Ya existe un item con ese ID", e);
            }
            throw new RuntimeException("Error al crear item", e);
        }

        throw new RuntimeException("No se pudo crear el item");
    }

    public Optional<Item> updateItem(String id, Item item) {
        String sql = "UPDATE items SET name = ?,SET description = ?, base_price = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setBigDecimal(3, item.getBasePrice());
            pstmt.setString(4, item.getStatus());
            pstmt.setString(5, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Item updated: {}", id);
                return getItemById(id);
            }

        } catch (SQLException e) {
            logger.error("Error updating item: {}", id, e);
            throw new RuntimeException("Error al actualizar item", e);
        }

        logger.warn("Item not found for update: {}", id);
        return Optional.empty();
    }

    public boolean deleteItem(String id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Item deleted: {}", id);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error deleting item: {}", id, e);
            throw new RuntimeException("Error al eliminar item", e);
        }

        logger.warn("Item not found for deletion: {}", id);
        return false;
    }

    public boolean itemExists(String id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM items WHERE id = ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }

        } catch (SQLException e) {
            logger.error("Error checking item existence: {}", id, e);
            throw new RuntimeException("Error al verificar item", e);
        }

        return false;
    }
}