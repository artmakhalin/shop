package com.ait.shop.service;

import com.ait.shop.domain.Cart;
import com.ait.shop.domain.Customer;
import com.ait.shop.domain.Position;
import com.ait.shop.domain.Product;
import com.ait.shop.dto.customer.CustomerDto;
import com.ait.shop.dto.customer.CustomerSaveUpdateDto;
import com.ait.shop.dto.mapping.CustomerMapper;
import com.ait.shop.dto.position.PositionSaveDto;
import com.ait.shop.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final ProductService productService;
    private final CustomerMapper mapper;

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService, CustomerMapper mapper) {
        this.repository = repository;
        this.productService = productService;
        this.mapper = mapper;
    }

    @Override
    public CustomerDto save(CustomerSaveUpdateDto saveDto) {
        Customer entity = new Customer();
        entity.setName(saveDto.getName());
        entity.setActive(true);
        Cart cart = new Cart();
        entity.setCart(cart);
        cart.setCustomer(entity);
        repository.save(entity);

        return mapper.mapEntityToDto(entity);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer entity = getEntityById(id);

        return mapper.mapEntityToDto(entity);
    }

    @Override
    public Customer getEntityById(Long id) {
        return repository.findByIdAndActiveTrue(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public void update(Long id, CustomerSaveUpdateDto updateDto) {
        repository.findById(id)
                .ifPresent(x -> x.setName(updateDto.getName()));
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
    public long getAllCustomersCount() {
        return repository.countByActiveTrue();
    }

    @Override
    public BigDecimal getCustomerCartTotalCost(Long id) {
        Customer customer = getEntityById(id);
        if (customer == null) {
            return BigDecimal.ZERO;
        }
        return customer.getCart()
                .getPositions()
                .stream()
                .map(position -> position
                        .getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(position.getQuantity()))
                )
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getCustomerCartAveragePrice(Long id) {
        Customer customer = getEntityById(id);

        if (customer == null) {
            return BigDecimal.ZERO;
        }

        long productsCount = customer.getCart()
                .getPositions()
                .stream()
                .map(Position::getQuantity)
                .reduce(Integer::sum)
                .orElse(0);

        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }

        return getCustomerCartTotalCost(id).divide(
                BigDecimal.valueOf(productsCount),
                2,
                RoundingMode.HALF_UP
        );
    }

    @Override
    @Transactional
    public void addPositionToCustomerCart(Long customerId, Long productId, PositionSaveDto saveDto) {
        Product product = productService.getActiveEntityById(productId);
        Customer customer = getEntityById(customerId);

        if (customer != null && product != null) {
            Cart cart = customer.getCart();
            Position position = getPosition(cart, product);

            if (position == null) {
                position = new Position(product, saveDto.getQuantity(), cart);
                position.setCart(cart);
                cart.getPositions().add(position);

            } else {
                position.setQuantity(position.getQuantity() + saveDto.getQuantity());
            }
        }
    }


    @Override
    @Transactional
    public void deletePositionFromCustomerCart(Long customerId, Long productId) {
        Product product = productService.getActiveEntityById(productId);
        Customer customer = getEntityById(customerId);

        if (customer != null && product != null) {
            Cart cart = customer.getCart();
            Position position = getPosition(cart, product);

            if (position != null) {
                cart.getPositions().remove(position);
                position.setCart(null);
            }
        }
    }

    @Override
    @Transactional
    public void clearCustomerCart(Long customerId) {
        Customer customer = getEntityById(customerId);

        if (customer != null) {
            Cart cart = customer.getCart();

            cart.getPositions().clear();
        }
    }

    private Position getPosition(Cart cart, Product product) {
        return cart.getPositions()
                .stream()
                .filter(x -> x.getProduct()
                        .equals(product))
                .findAny()
                .orElse(null);
    }
}
