package org.challenge6.javaspark.entity;

import java.math.BigDecimal;

public class Item {
    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String status;

    public Item() {
    }

    public Item(String id, String name,String description, BigDecimal basePrice, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.status = status;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", status='" + status + '\'' +
                '}';
    }
}