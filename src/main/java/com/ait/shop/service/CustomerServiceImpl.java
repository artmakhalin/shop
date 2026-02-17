package com.ait.shop.service;

import com.ait.shop.domain.Cart;
import com.ait.shop.domain.Customer;
import com.ait.shop.domain.Position;
import com.ait.shop.domain.Product;
import com.ait.shop.dto.customer.CustomerDto;
import com.ait.shop.dto.customer.CustomerSaveDto;
import com.ait.shop.dto.customer.CustomerUpdateDto;
import com.ait.shop.dto.mapping.CustomerMapper;
import com.ait.shop.dto.position.PositionUpdateDto;
import com.ait.shop.exceptions.types.EntityNotFoundException;
import com.ait.shop.exceptions.types.EntityUpdateException;
import com.ait.shop.repository.CustomerRepository;
import com.ait.shop.service.interfaces.CustomerService;
import com.ait.shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository repository;
    private final ProductService productService;
    private final CustomerMapper mapper;

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService, CustomerMapper mapper) {
        this.repository = repository;
        this.productService = productService;
        this.mapper = mapper;
    }

    @Override
    public CustomerDto save(CustomerSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "CustomerSaveDto cannot be null");
        Customer entity = mapper.mapDtoToEntity(saveDto);

        Cart cart = new Cart();
        entity.setCart(cart);
        cart.setCustomer(entity);

        entity.setActive(true);
        repository.save(entity);

        logger.info("Customer saved in database: {}", entity);

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
        Objects.requireNonNull(id, "Customer id cannot be null");

        return repository.findByIdAndActiveTrue(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(Customer.class, id)
                );
    }

    @Override
    @Transactional
    public void update(Long id, CustomerUpdateDto updateDto) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Objects.requireNonNull(updateDto, "CustomerSaveDto cannot be null");

        repository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(Customer.class, id)
                )
                .setName(updateDto.getNewName());

        logger.info("Customer id {} updated. New name: {}", id, updateDto.getNewName());

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");

        getEntityById(id).setActive(false);
        logger.info("Customer id {} marked as inactive", id);
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");

        repository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(Customer.class, id)
                )
                .setActive(true);
        logger.info("Customer id {} marked as active", id);
    }

    @Override
    public long getAllCustomersCount() {
        return repository.countByActiveTrue();
    }

    private List<Position> getCustomerActivePositions(Customer customer) {
        return customer.getCart()
                .getPositions()
                .stream()
                .filter(x -> x.getProduct().isActive())
                .toList();
    }

    private BigDecimal getPositionsTotalCost(List<Position> positions) {
        return positions.stream()
                .map(x -> x.getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(x.getQuantity()))
                )
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private int getProductsCountInPositions(List<Position> positions) {
        return positions.stream()
                .map(Position::getQuantity)
                .reduce(Integer::sum)
                .orElse(0);
    }


    @Override
    public BigDecimal getCustomerCartTotalCost(Long id) {
        Customer customer = getEntityById(id);

        return getPositionsTotalCost(getCustomerActivePositions(customer));
    }

    @Override
    public BigDecimal getCustomerCartAveragePrice(Long id) {
        Customer customer = getEntityById(id);
        List<Position> activePositions = getCustomerActivePositions(customer);
        int productsQuantity = getProductsCountInPositions(activePositions);

        return productsQuantity == 0
                ? BigDecimal.ZERO
                : getPositionsTotalCost(activePositions)
                .divide(BigDecimal.valueOf(productsQuantity), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void addPositionToCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Objects.requireNonNull(positionUpdateDto, "PositionUpdateDto cannot be null");

        Product product = productService.getActiveEntityById(productId);
        Customer customer = getEntityById(customerId);

        if (positionUpdateDto.getQuantity() < 1) {
            throw new EntityUpdateException("Quantity should be positive");
        }

        for (Position position : customer.getCart().getPositions()) {
            if (position.getProduct().equals(product)) {
                position.setQuantity(position.getQuantity() + positionUpdateDto.getQuantity());

                logger.info("Customer id {}, position id {}, quantity added, new value: {}",
                        customerId, position.getId(), position.getQuantity());

                return;
            }
        }

        Cart cart = customer.getCart();
        Position position = new Position(product, positionUpdateDto.getQuantity(), cart);
        cart.getPositions().add(position);

        logger.info("Customer id {}, position added to cart: {}", customerId, position);
    }


    @Override
    @Transactional
    public void deletePositionFromCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Product product = productService.getActiveEntityById(productId);
        Customer customer = getEntityById(customerId);

        if (positionUpdateDto.getQuantity() < 1) {
            throw new EntityUpdateException("Quantity should be positive");
        }

        Iterator<Position> iterator = customer.getCart().getPositions().iterator();
        while (iterator.hasNext()) {
            Position position = iterator.next();
            if (position.getProduct().equals(product)) {
                if (position.getQuantity() > positionUpdateDto.getQuantity()) {
                    position.setQuantity(position.getQuantity() - positionUpdateDto.getQuantity());

                    logger.info("Customer id {}, position id {}, quantity reduced, new value: {}",
                            customerId, position.getId(), position.getQuantity());
                } else {
                    iterator.remove();

                    logger.info("Customer id {}, position removed from cart: {}", customerId, position);
                }
                break;
            }
        }
    }

    @Override
    @Transactional
    public void clearCustomerCart(Long customerId) {
        Customer customer = getEntityById(customerId);
        Cart cart = customer.getCart();
        cart.getPositions().clear();

        logger.info("Customer id {} cleared the cart", customerId);
    }
}
