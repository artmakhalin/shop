package com.ait.shop.dto.customer;

import com.ait.shop.dto.cart.CartDto;

public class CustomerDto {

    private Long id;
    private String name;
    private CartDto cart;

    public CustomerDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return String.format("Customer: id - %d, name - %s, cart - %s", id, name, cart);
    }
}
