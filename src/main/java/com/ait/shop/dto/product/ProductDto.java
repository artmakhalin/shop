package com.ait.shop.dto.product;

import java.math.BigDecimal;

//Dto для отправки данных клиенту
public class ProductDto {

    private Long id;
    private String title;
    private BigDecimal price;

    public ProductDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("ProductDto: id - %d, title - %s, price - %.2f", id, title, price);
    }
}
