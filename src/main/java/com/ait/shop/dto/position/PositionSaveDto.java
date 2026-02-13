package com.ait.shop.dto.position;

public class PositionSaveDto {

    private int quantity;

    public PositionSaveDto() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Position: quantity - %d", quantity);
    }
}
