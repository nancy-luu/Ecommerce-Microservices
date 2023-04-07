package com.springboot.ekart.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = { "classpath:messages.properties" })
public class PaymentMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentMsApplication.class, args);
	}

}
