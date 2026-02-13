package com.ait.shop.dto.product;

import java.math.BigDecimal;

//Dto для изменения продукта
public class ProductUpdateDto {

    private BigDecimal newPrice;

    public ProductUpdateDto() {
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return String.format("ProductSaveDto: new price - %.2f", newPrice);
    }
}
