package com.ait.shop.dto.customer;

public class CustomerUpdateDto {

    private String newName;

    public CustomerUpdateDto() {
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return String.format("Customer Update DTO: new name - %s", newName);
    }
}
