package com.ait.shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Application Shop",
                description = "Application for various operations with Stores, Customers and Products",
                version = "1.0.0",
                contact = @Contact(
                        name = "Andrey",
                        email = "andrey@ait-tr.de",
                        url = "http://ait-tr.de"
                )
        )
)
public class SwaggerConfig {
}
