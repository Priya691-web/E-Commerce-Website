package com.fashionstore.model;

import java.sql.Timestamp;

public class Cart {
    private int id;
    private int userId;
    private Timestamp createdAt;

    public Cart() {}

    public Cart(int id, int userId, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
