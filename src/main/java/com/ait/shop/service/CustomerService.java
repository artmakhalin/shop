package com.ait.shop.service;

import com.ait.shop.domain.Customer;
import com.ait.shop.dto.customer.CustomerDto;
import com.ait.shop.dto.customer.CustomerSaveUpdateDto;
import com.ait.shop.dto.position.PositionSaveDto;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

//    Сохранить покупателя в базе данных.
    CustomerDto save(CustomerSaveUpdateDto saveDto);

//    Вернуть всех покупателей из базы данных.
    List<CustomerDto> getAllCustomers();

//    Вернуть одного покупателя из базы данных по его идентификатору.
    CustomerDto getCustomerById(Long id);
    Customer getEntityById(Long id);

//    Изменить одного покупателя в базе данных по его идентификатору.
    void update(Long id, CustomerSaveUpdateDto updateDto);

//    Удалить покупателя из базы данных по его идентификатору.
    void deleteById(Long id);

//    Восстановить удалённого покупателя в базе данных по его идентификатору.
    void restoreById(Long id);

//    Вернуть общее количество покупателей в базе данных.
    long getAllCustomersCount();

//    Вернуть стоимость корзины покупателя по его идентификатору.
    BigDecimal getCustomerCartTotalCost(Long id);

//    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    BigDecimal getCustomerCartAveragePrice(Long id);

//    Добавить товар в корзину покупателя по их идентификаторам.
    void addPositionToCustomerCart(Long customerId, Long productId, PositionSaveDto saveDto);

//    Удалить товар из корзины покупателя по их идентификаторам.
    void deletePositionFromCustomerCart(Long customerId, Long productId);
//    Полностью очистить корзину покупателя по его идентификатору.
    void clearCustomerCart(Long customerId);
}
