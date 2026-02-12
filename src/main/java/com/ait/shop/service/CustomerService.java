package com.ait.shop.service;

import com.ait.shop.domain.Customer;
import com.ait.shop.domain.Position;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

//    Сохранить покупателя в базе данных.
    Customer save(Customer customer);

//    Вернуть всех покупателей из базы данных.
    List<Customer> getAllCustomers();

//    Вернуть одного покупателя из базы данных по его идентификатору.
    Customer getCustomerById(Long id);

//    Изменить одного покупателя в базе данных по его идентификатору.
    void update(Long id, Customer customer);

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
    void addPositionToCustomerCart(Long customerId, Long productId);

//    Удалить товар из корзины покупателя по их идентификаторам.
    void deletePositionFromCustomerCart(Long customerId, Long productId);
//    Полностью очистить корзину покупателя по его идентификатору.
    void clearCustomerCart(Long customerId);
}
