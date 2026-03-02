package com.ait.shop.controller;

import com.ait.shop.constants.Constants;
import com.ait.shop.domain.Product;
import com.ait.shop.domain.User;
import com.ait.shop.domain.enums.Role;
import com.ait.shop.dto.product.ProductDto;
import com.ait.shop.dto.product.ProductSaveDto;
import com.ait.shop.repository.ProductRepository;
import com.ait.shop.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${KEY_PHRASE_ACCESS}")
    private String accessPhrase;
    private String adminAccessToken;
    private static final String PRODUCT_RESOURCE = "/products";


    @Test
    public void shouldSaveProduct() {
        //Создаем тело запроса, им является в данном случае ДТО для сохранения
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        String tokenCookie = Constants.ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        //Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        //Отправляем запрос и получаем ответ (response) для put/delete - exchange
        ResponseEntity<ProductDto> response = httpClient.postForEntity(
                PRODUCT_RESOURCE,
                request,
                ProductDto.class
        );

        //Проверяем, что нам действительно пришел ожидаемый статус ответа
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        //Проверяем корректность того, что пришло в теле ответа
        ProductDto actualDto = response.getBody();
        assertNotNull(actualDto, "Response body should not be null");
        assertNotNull(actualDto.getId(), "Returned product id should not be null");
        assertEquals(actualDto.getTitle(), saveDto.getTitle(), "Returned product has incorrect title");
        assertEquals(actualDto.getPrice(), saveDto.getPrice(), "Returned product has incorrect price");

        //Проверяем что продукт действительно сохранился в БД
        Product savedProduct = repository.findByIdAndActiveTrue(actualDto.getId()).orElse(null);
        assertNotNull(savedProduct, "Product was not properly saved to database");
        assertEquals(actualDto.getTitle(), savedProduct.getTitle(), "Saved product has incorrect title");
        assertEquals(actualDto.getPrice(), savedProduct.getPrice(), "Saved product has incorrect price");
    }

    @Test
    public void shouldReturn400WhenTitleIsEmpty() {
        //Создаем тело запроса, им является в данном случае ДТО для сохранения
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        String tokenCookie = Constants.ACCESS_TOKEN_COOKIE_NAME + "=" + adminAccessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, tokenCookie);

        //Создаем объект http-запроса
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto, httpHeaders);

        //Отправляем запрос и получаем ответ (response) для put/delete - exchange
        ResponseEntity<String> response = httpClient.postForEntity(
                PRODUCT_RESOURCE,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has unexpected status");

        String actualBody = response.getBody();
        assertNotNull(actualBody, "Response body should not be null");
        assertTrue(actualBody.contains("title"), "Response body doesn't contain expected message");
    }

    @BeforeEach
    public void startUp() {
        Product activeProduct = new Product();
        activeProduct.setTitle("Test active product");
        activeProduct.setPrice(new BigDecimal("111.00"));
        activeProduct.setActive(true);

        Product inactiveProduct = new Product();
        inactiveProduct.setTitle("Test inactive product");
        inactiveProduct.setPrice(new BigDecimal("222.00"));
        inactiveProduct.setActive(false);

        repository.saveAll(List.of(activeProduct, inactiveProduct));
    }

    @BeforeEach
    public void setUp() {
        addUsersToDatabase();
        createAdminAccessToken();
    }

    private void createAdminAccessToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 60 * 1000);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));

        adminAccessToken = Jwts.builder()
                .subject("admin@test.com")
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private void addUsersToDatabase() {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setConfirmed(true);
        userRepository.save(admin);
    }

    //Метод для очистки БД после каждого теста
    @AfterEach
    public void cleanDatabase() {
        repository.deleteAll();
        userRepository.deleteAll();
    }
}