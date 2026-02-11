package com.ait.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {

	//Что происходит при запуске приложения:
	//1. Запускается втроенный Tomcat
	//2. SpringBoot сам собирает наше приложение в нужный архив
	//3. SpringBoot сам прописывает конфигурацию для Tomcat
	//4. SpringBoot сам делает деплой нашего приложения на Tomcat

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

}
