package org.challenge6.javaspark.services;

import org.challenge6.javaspark.config.DatabaseConfig;
import org.challenge6.javaspark.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class UserService {
   private static UserService instance;
   private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

   private UserService() {
   }
   public static synchronized UserService getInstance() {
       if (instance == null) {
           instance = new UserService();
       }
       return instance;
   }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        Timestamp createdAt = rs.getTimestamp("created_at");

        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            logger.info("Retrieved {} users", users.size());

        } catch (SQLException e) {
            logger.error("Error retrieving all users", e);
            throw new RuntimeException("Error al obtener usuarios", e);
        }

        return users;
    }
    public Optional<User> getUserById(String id) {
        // Validar que el ID sea un número válido
        int userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", id);
            return Optional.empty();
        }

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId); // Usar setInt en lugar de setString
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                logger.info("User found: {}", id);
                return Optional.of(user);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving user: {}", id, e);
            throw new RuntimeException("Error al obtener usuario", e);
        }

        logger.warn("User not found: {}", id);
        return Optional.empty();
    }

    public User addUser(User user) {
        String sql = "INSERT INTO users (name, email) VALUES ( ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User createdUser = mapResultSetToUser(rs);
                logger.info("User created: {}", createdUser.getId());
                return createdUser;
            }

        } catch (SQLException e) {
            logger.error("Error creating user", e);
            if (e.getMessage().contains("duplicate key")) {
                throw new RuntimeException("Ya existe un usuario con ese ID o email", e);
            }
            throw new RuntimeException("Error al crear usuario", e);
        }

        throw new RuntimeException("No se pudo crear el usuario");
    }

    public Optional<User> updateUser(String id, User user) {
        String sql = "UPDATE users SET name = ?, email = ? " +
                "WHERE id = ? RETURNING *";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User updatedUser = mapResultSetToUser(rs);
                logger.info("User updated: {}", id);
                return Optional.of(updatedUser);
            }

        } catch (SQLException e) {
            logger.error("Error updating user: {}", id, e);
            if (e.getMessage().contains("duplicate key")) {
                throw new RuntimeException("Ya existe un usuario con ese email", e);
            }
            throw new RuntimeException("Error al actualizar usuario", e);
        }

        logger.warn("User not found for update: {}", id);
        return Optional.empty();
    }

    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("User deleted: {}", id);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error deleting user: {}", id, e);
            throw new RuntimeException("Error al eliminar usuario", e);
        }

        logger.warn("User not found for deletion: {}", id);
        return false;
    }

    public boolean userExists(String id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }

        } catch (SQLException e) {
            logger.error("Error checking user existence: {}", id, e);
            throw new RuntimeException("Error al verificar usuario", e);
        }

        return false;
    }
}
