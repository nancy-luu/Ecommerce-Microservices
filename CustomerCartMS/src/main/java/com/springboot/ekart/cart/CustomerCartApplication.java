package com.springboot.ekart.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:messages.properties"})
public class CustomerCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerCartApplication.class, args);
	}

}
