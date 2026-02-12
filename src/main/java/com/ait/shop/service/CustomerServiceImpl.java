package com.ait.shop.service;

import com.ait.shop.domain.Cart;
import com.ait.shop.domain.Customer;
import com.ait.shop.domain.Position;
import com.ait.shop.domain.Product;
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

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

    @Override
    public Customer save(Customer customer) {
        customer.setActive(true);
        Cart cart = new Cart();
        customer.setCart(cart);
        cart.setCustomer(customer);

        return repository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repository.findAllByActiveTrue();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return repository.findByIdAndActiveTrue(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public void update(Long id, Customer customer) {
        repository.findById(id)
                .ifPresent(x -> x.setName(customer.getName()));
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
        Customer customer = getCustomerById(id);
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
        Customer customer = getCustomerById(id);

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
    public void addPositionToCustomerCart(Long customerId, Long productId) {
        Product product = productService.getActiveProductById(productId);
        Customer customer = getCustomerById(customerId);

        if (customer != null && product != null) {
            Cart cart = customer.getCart();
            Position position = getPosition(cart, product);

            if (position == null) {
                position = new Position(product, 1, cart);
                position.setCart(cart);
                cart.getPositions().add(position);

            } else {
                position.setQuantity(position.getQuantity() + 1);
            }
        }
    }


    @Override
    @Transactional
    public void deletePositionFromCustomerCart(Long customerId, Long productId) {
        Product product = productService.getActiveProductById(productId);
        Customer customer = getCustomerById(customerId);

        if (customer != null && product != null) {
            Cart cart = customer.getCart();
            Position position = getPosition(cart, product);

            if (position != null) {
                position.setQuantity(0);
            }
        }
    }

    @Override
    @Transactional
    public void clearCustomerCart(Long customerId) {
        Customer customer = getCustomerById(customerId);

        if (customer != null) {
            Cart cart = customer.getCart();

            cart.getPositions().forEach(x -> x.setQuantity(0));
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
