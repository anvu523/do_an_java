package com.brewpoint.pos.model;

public class Category {
    private int categoryId;
    private String name;
    private int displayOrder;
    private boolean active;

    public Category() {
    }

    public Category(int categoryId, String name, int displayOrder, boolean active) {
        this.categoryId = categoryId;
        this.name = name;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name == null ? "" : name;
    }
}
