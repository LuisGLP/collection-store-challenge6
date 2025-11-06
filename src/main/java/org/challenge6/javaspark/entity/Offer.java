package org.challenge6.javaspark.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Offer {
    private Integer id;
    private Integer userId;
    private String itemId;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    // Para incluir información adicional en respuestas
    private String userName;
    private String itemName;

    public Offer() {
    }

    public Offer(Integer id, Integer userId, String itemId, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemId='" + itemId + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}