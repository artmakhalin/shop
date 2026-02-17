package com.ait.shop.dto.position;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PositionUpdateDto {

    private int quantity;

    public PositionUpdateDto() {
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
