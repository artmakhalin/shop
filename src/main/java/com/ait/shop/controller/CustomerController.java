package com.ait.shop.controller;

import com.ait.shop.dto.customer.CustomerDto;
import com.ait.shop.dto.customer.CustomerSaveDto;
import com.ait.shop.dto.customer.CustomerUpdateDto;
import com.ait.shop.dto.position.PositionUpdateDto;
import com.ait.shop.service.interfaces.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    //    Сохранить покупателя в базе данных.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto save(@RequestBody CustomerSaveDto saveDto) {
        return service.save(saveDto);
    }


    //    Вернуть всех покупателей из базы данных.
    @GetMapping
    public List<CustomerDto> getAll() {
        return service.getAllCustomers();
    }

    //    Вернуть одного покупателя из базы данных по его идентификатору.
    @GetMapping("/{id}")
    public CustomerDto getById(@PathVariable Long id) {
        return service.getCustomerById(id);
    }

    //    Изменить одного покупателя в базе данных по его идентификатору.
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CustomerUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    //    Удалить покупателя из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    //    Восстановить удалённого покупателя в базе данных по его идентификатору.
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    //    Вернуть общее количество покупателей в базе данных.
    @GetMapping("/count")
    public long getCustomersQuantity() {
        return service.getAllCustomersCount();
    }

    //    Вернуть стоимость корзины покупателя по его идентификатору.
    @GetMapping("/{id}/cart/total-cost")
    public BigDecimal getCustomerCartTotalCost(@PathVariable Long id) {
        return service.getCustomerCartTotalCost(id);
    }

    //    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    @GetMapping("/{id}/cart/avg-price")
    public BigDecimal getCustomerCartAveragePrice(@PathVariable Long id) {
        return service.getCustomerCartAveragePrice(id);
    }

    //    Добавить товар в корзину покупателя по их идентификаторам.
    @PostMapping("/{customerId}/cart/items/{productId}")
    public void addPositionToCustomerCart(@PathVariable Long customerId, @PathVariable Long productId, @RequestBody PositionUpdateDto positionUpdateDto) {
        service.addPositionToCustomerCart(customerId, productId, positionUpdateDto);
    }

    //    Удалить товар из корзины покупателя по их идентификаторам.
    @DeleteMapping("/{customerId}/cart/items/{productId}")
    public void deletePositionFromCustomerCart(@PathVariable Long customerId, @PathVariable Long productId, @RequestBody PositionUpdateDto positionUpdateDto) {
        service.deletePositionFromCustomerCart(customerId, productId, positionUpdateDto);
    }

    //    Полностью очистить корзину покупателя по его идентификатору.
    @DeleteMapping("/{customerId}/cart/items")
    public void clearCustomerCart(@PathVariable Long customerId) {
        service.clearCustomerCart(customerId);
    }
}
