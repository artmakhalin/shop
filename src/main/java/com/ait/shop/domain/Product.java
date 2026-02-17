package com.ait.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //Название продукта должно отвечать след требованиям:
    //Длина - не менее 3х букв
    //Первая буква должна быть в верхнем регистре
    //Вторая и послед буквы должны быть в нижнем регистре
    //Не допускаются цифры, спец символы и кириллица (можно пробелы)
    @Column(name = "title")
    @NotNull(message = "Product title cannot be null")
    @NotBlank(message = "Product title cannot be empty")
//    @Length(min = 3, max = 50)
    @Pattern(
            regexp = "[A-Z][a-z ]{2,99}",
            message = "Product title should be at least 3 chars length and starts with capital letter"
    )
    private String title;

    @Column(name = "price")
    @NotNull(message = "Product price cannot be null")
    @DecimalMin(
            value = "0.00",
            message = "Product price should be greater or equal than zero"
    )
    @DecimalMax(
            value = "1000.00",
            inclusive = false,
            message = "Product price should be lesser than 1000"
    )
    private BigDecimal price;

    @Column(name = "active")
    private boolean active;

    public Product() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    //!!!
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Product product)) {
            return false;
        }

        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Product: id - %d, title - %s, price - %.2f, active - %s", id, title, price, active ? "yes" : "no");
    }
}
