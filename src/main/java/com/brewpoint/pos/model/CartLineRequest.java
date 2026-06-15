package com.brewpoint.pos.model;

import java.util.ArrayList;
import java.util.List;

public class CartLineRequest {
    private int productId;
    private int productSizeId;
    private List<Integer> toppingIds = new ArrayList<Integer>();
    private int quantity;
    private String note;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductSizeId() {
        return productSizeId;
    }

    public void setProductSizeId(int productSizeId) {
        this.productSizeId = productSizeId;
    }

    public List<Integer> getToppingIds() {
        return toppingIds;
    }

    public void setToppingIds(List<Integer> toppingIds) {
        this.toppingIds = toppingIds == null ? new ArrayList<Integer>() : new ArrayList<Integer>(toppingIds);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
