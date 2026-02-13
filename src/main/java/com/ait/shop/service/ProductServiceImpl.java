package com.ait.shop.service;

import com.ait.shop.domain.Product;
import com.ait.shop.dto.mapping.ProductMapper;
import com.ait.shop.dto.product.ProductDto;
import com.ait.shop.dto.product.ProductSaveDto;
import com.ait.shop.dto.product.ProductUpdateDto;
import com.ait.shop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/*
Что происходит при старте приложения:
    1. Spring сканирует приложение и находит интерфейсы JPA репозиториев
    2. Spring Data JPA генерирует класс нашего интерфейса репозитория с методами,
    в которых прописаны все нужные SQL-запросы в БД
    3. Спринг создает объект этого класса (репозитория) и помещает его в Спринг контекст
    4. Спринг сканирует приложение и видит класс ProductServiceImpl, помеченный @Service
    5. Спринг создает объект этого класса при этом используя конструктор public ProductServiceImpl(ProductRepository repository),
    потому что другого нет
    6. Спринг видит что у конструктора есть входящий параметр ProductRepository repository
    7. Спринг извлекает из Спринг контекста объект репозитория и передает его в конструктор
    8. Конструктор сохраняет объект репозитория в поле private final ProductRepository repository;
    9. Мы в коде класса обращаемся к repository и вызывает его методы для доступа к БД
 */

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProductDto save(ProductSaveDto saveDto) {
        Product entity = mapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        repository.save(entity);

        return mapper.mapEntityToDto(entity);
    }

    @Override
    public List<ProductDto> getAllActiveProducts() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    @Override
    public ProductDto getActiveProductById(Long id) {
        Product product = getActiveEntityById(id);

        return mapper.mapEntityToDto(product);
    }

    @Override
    public Product getActiveEntityById(Long id) {
        return repository.findByIdAndActiveTrue(id).orElse(null);
    }

    @Override
    @Transactional
    public void update(Long id, ProductUpdateDto updateDto) {
        repository.findById(id)
                .ifPresent(x -> x.setPrice(updateDto.getNewPrice()));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.findByIdAndActiveTrue(id)
                .ifPresent(x -> x.setActive(false));
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        repository.findById(id)
                .ifPresent(x -> x.setActive(true));
    }

    @Override
    public long getAllActiveProductsCount() {
        return repository.countByActiveTrue();
    }

    @Override
    public BigDecimal getAllActiveProductsTotalCost() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productsCount = getAllActiveProductsCount();

        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }

        return getAllActiveProductsTotalCost().divide(
                BigDecimal.valueOf(productsCount),
                2,
                RoundingMode.HALF_UP
        );
    }
}
