package com.ait.shop.controller;

import com.ait.shop.domain.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    //    Сохранить покупателя в базе данных.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer save(@RequestBody Customer customer) {
        System.out.println("Request for saving customer:");
        System.out.println(customer);
        return null;
    }


    //    Вернуть всех покупателей из базы данных.
    @GetMapping
    public List<Customer> getAll() {
        System.out.println("Request for reading all customers");
        return null;
    }

    //    Вернуть одного покупателя из базы данных по его идентификатору.
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        System.out.println("Request for reading customer by id " + id);
        return null;
    }

    //    Изменить одного покупателя в базе данных по его идентификатору.
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Customer customer) {
        System.out.println("Request for updating customer with id " + id);
        System.out.println("New name - " + customer.getName());
    }

    //    Удалить покупателя из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        System.out.println("Request for deleting customer with id " + id);
    }

    //    Восстановить удалённого покупателя в базе данных по его идентификатору.
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        System.out.println("Request for restoring customer with id " + id);
    }

    //    Вернуть общее количество покупателей в базе данных.
    @GetMapping("/count")
    public int getCustomersQuantity() {
        System.out.println("Request for reading quantity of all customers");

        return 0;
    }

    //    Вернуть стоимость корзины покупателя по его идентификатору.
    @GetMapping("/{id}/total-cost")
    public BigDecimal getCustomerCartTotalCost(@PathVariable Long id) {
        System.out.println("Request for reading customer's with id " + id + " cart total cost");

        return null;
    }

    //    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    @GetMapping("/{id}/avg-price")
    public BigDecimal getCustomerCartAvgPrice(@PathVariable Long id) {
        System.out.println("Request for reading customer's with id " + id + " cart average price");

        return null;
    }

    //    Добавить товар в корзину покупателя по их идентификаторам.
    @PostMapping("/{customerId}/cart/items/{productId}")
    public void addPositionToCustomerCart(@PathVariable Long customerId, @PathVariable Long productId) {
        System.out.printf("Request for adding product id = %d to cart of customer id = %d", productId, customerId);
    }

    //    Удалить товар из корзины покупателя по их идентификаторам.
    @DeleteMapping("/{customerId}/cart/items/{productId}")
    public void deletePositionFromCustomerCart(@PathVariable Long customerId, @PathVariable Long productId) {
        System.out.printf("Request for deleting product id = %d from cart of customer id = %d", productId, customerId);
    }

    //    Полностью очистить корзину покупателя по его идентификатору.
    @DeleteMapping("/{customerId}/cart/items")
    public void clearCustomerCart(@PathVariable Long customerId) {
        System.out.printf("Request for deleting all positions from cart of customer id = %d", customerId);
    }
}
