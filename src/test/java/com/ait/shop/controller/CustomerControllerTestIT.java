package com.ait.shop.controller;

import com.ait.shop.domain.Cart;
import com.ait.shop.domain.Customer;
import com.ait.shop.domain.Position;
import com.ait.shop.domain.Product;
import com.ait.shop.dto.customer.CustomerDto;
import com.ait.shop.dto.customer.CustomerSaveDto;
import com.ait.shop.dto.customer.CustomerUpdateDto;
import com.ait.shop.dto.position.PositionUpdateDto;
import com.ait.shop.repository.CustomerRepository;
import com.ait.shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final String CUSTOMER_RESOURCE = "/customers";

    @BeforeEach
    public void startUp() {
        Customer activeCustomer1 = new Customer();
        activeCustomer1.setName("Jack");
        Cart cart1 = new Cart();
        activeCustomer1.setCart(cart1);
        cart1.setCustomer(activeCustomer1);
        activeCustomer1.setActive(true);

        Customer activeCustomer2 = new Customer();
        activeCustomer2.setName("Mike");
        Cart cart2 = new Cart();
        activeCustomer2.setCart(cart2);
        cart2.setCustomer(activeCustomer2);
        activeCustomer2.setActive(true);

        Customer activeCustomer3 = new Customer();
        activeCustomer3.setName("Lena");
        Cart cart3 = new Cart();
        activeCustomer3.setCart(cart3);
        cart3.setCustomer(activeCustomer3);
        activeCustomer3.setActive(true);

        Customer inactiveCustomer = new Customer();
        inactiveCustomer.setName("Fedor");
        Cart cart4 = new Cart();
        inactiveCustomer.setCart(cart4);
        cart4.setCustomer(inactiveCustomer);
        inactiveCustomer.setActive(false);

        customerRepository.saveAll(List.of(activeCustomer1, activeCustomer2, activeCustomer3, inactiveCustomer));
    }

    @AfterEach
    public void cleanDatabase() {
        customerRepository.deleteAll();
    }

    @Test
    public void shouldSaveCustomer() {
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("Test customer");

        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<CustomerDto> response = httpClient.postForEntity(
                CUSTOMER_RESOURCE,
                request,
                CustomerDto.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        CustomerDto actualDto = response.getBody();
        assertNotNull(actualDto, "Response body should not be null");
        assertNotNull(actualDto.getId(), "Returned customer id should not be null");
        assertNotNull(actualDto.getCart(), "Returned customer cart should not be null");
        assertEquals(actualDto.getName(), saveDto.getName());

        Customer savedCustomer = customerRepository.findByIdAndActiveTrue(actualDto.getId()).orElse(null);
        assertNotNull(savedCustomer, "Customer was nor properly saved to database");
        assertEquals(actualDto.getName(), savedCustomer.getName(), "Saved customer has incorrect name");
    }

    @Test
    public void shouldFindAllActiveCustomers() {
        ParameterizedTypeReference<List<CustomerDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<CustomerDto>> response = httpClient.exchange(
                CUSTOMER_RESOURCE,
                HttpMethod.GET,
                null,
                responseType
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<CustomerDto> actualListDto = response.getBody();
        assertNotNull(actualListDto, "Response body should not be null");
        assertEquals(3, actualListDto.size());
        assertTrue(actualListDto.stream()
                .map(CustomerDto::getName)
                .toList()
                .containsAll(List.of("Jack", "Mike", "Lena"))
        );
    }

    @Test
    public void shouldReturn400WhenUpdateWithEmptyName() {
        Customer customerBeforeUpdate = new Customer();
        customerBeforeUpdate.setName("Anna");
        Cart cart1 = new Cart();
        customerBeforeUpdate.setCart(cart1);
        cart1.setCustomer(customerBeforeUpdate);
        customerBeforeUpdate.setActive(true);
        customerRepository.save(customerBeforeUpdate);

        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        customerBeforeUpdate.setName("Na");

        HttpEntity<CustomerUpdateDto> request = new HttpEntity<>(updateDto);

        ResponseEntity<String> response = httpClient.exchange(
                String.format("%s/%d", CUSTOMER_RESOURCE, customerBeforeUpdate.getId()),
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String actualBody = response.getBody();
        assertNotNull(actualBody, "Response body should not be null");
        assertTrue(actualBody.contains("name"), "Response body doesn't contain expected message");

        Customer customerAfterUpdate = customerRepository.findById(customerBeforeUpdate.getId()).orElse(null);
        assertNotNull(customerAfterUpdate, "Customer should not be null");
        assertEquals(customerAfterUpdate, customerBeforeUpdate, "Customer should not be changed in database");
    }
    
    @Test
    public void shouldReturn404WhenDeleteUnexistedCustomer() {
        ResponseEntity<String> response = httpClient.exchange(
                String.format("%s/%d", CUSTOMER_RESOURCE, Integer.MAX_VALUE),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String actualBody = response.getBody();
        assertNotNull(actualBody, "Response body should not be null");
        assertTrue(actualBody.contains("not found"), "Response body doesn't contain expected message");
    }

    @Test
    public void shouldAddPositionToCustomerCart() {
        Customer customer = new Customer();
        customer.setName("Peter");
        Cart cart1 = new Cart();
        customer.setCart(cart1);
        cart1.setCustomer(customer);
        customer.setActive(true);
        customerRepository.save(customer);

        Product product = new Product();
        product.setTitle("Banana");
        product.setPrice(new BigDecimal("111.00"));
        product.setActive(true);
        productRepository.save(product);

        PositionUpdateDto updateDto = new PositionUpdateDto();
        updateDto.setQuantity(5);

        HttpEntity<PositionUpdateDto> request = new HttpEntity<>(updateDto);

        ResponseEntity<?> response = httpClient.postForEntity(
                String.format("%s/%d/cart/items/%d", CUSTOMER_RESOURCE, customer.getId(), product.getId()),
                request,
                null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Customer customerAfterAddingProduct = customerRepository.findById(customer.getId()).orElse(null);
        Set<Position> positions = customerAfterAddingProduct.getCart().getPositions();
        assertNotNull(positions, "Positions in customer cart cannot be null");
        Position positionFromDb = positions.stream().toList().get(0);
        assertNotNull(positionFromDb, "Position cannot be null");
        assertEquals(positionFromDb.getProduct().getTitle(), product.getTitle(), "Product name has unexpected name");
        assertEquals(positionFromDb.getQuantity(), updateDto.getQuantity(), "Position has unexpected quantity");
    }
}