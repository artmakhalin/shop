package com.ait.shop.controller;

import com.ait.shop.dto.product.ProductDto;
import com.ait.shop.dto.product.ProductSaveDto;
import com.ait.shop.dto.product.ProductUpdateDto;
import com.ait.shop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/*
    @RequestMapping("/products") - благодаря этой аннтоации Spring понимает,
    что все запросы которые пришли на http://10.20.30.40:8080/products
    нужно адресовать именно этому контроллеру
 */

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    //    Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    // POST -> http://10.20.30.40:8080/products -> ожидаем данные продукта в теле запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto save(@RequestBody ProductSaveDto saveDto) {
        return service.save(saveDto);
    }

    //    Вернуть все продукты из базы данных (активные).
    // GET -> http://10.20.30.40:8080/products
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllActiveProducts();
    }


    //    Вернуть один продукт из базы данных по его идентификатору (если он активен).
    // GET -> http://10.20.30.40:8080/products/5
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return service.getActiveProductById(id);
    }

    //    Изменить один продукт в базе данных по его идентификатору.
// PUT -> http://10.20.30.40:8080/products/5
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductUpdateDto updateDto) {
        service.update(id, updateDto);
    }

    //    Удалить продукт из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    //    Восстановить удалённый продукт в базе данных по его идентификатору.
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    //    Вернуть общее количество продуктов в базе данных (активных).
    @GetMapping("/count")
    public long getProductsQuantity() {
        return service.getAllActiveProductsCount();
    }

    //    Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    @GetMapping("/total-cost")
    public BigDecimal getProductsTotalCost() {
        return service.getAllActiveProductsTotalCost();
    }

    //    Вернуть среднюю стоимость продукта в базе данных (из активных).
    @GetMapping("/avg-price")
    public BigDecimal getProductsAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }
}
