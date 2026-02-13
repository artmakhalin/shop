package com.ait.shop.dto.cart;

import com.ait.shop.dto.position.PositionDto;

import java.util.Set;

public class CartDto {

    private Long id;
    private Set<PositionDto> positions;

    public CartDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        return String.format("Cart: id - %d, ppositions - %s", id, positions);
    }
}
