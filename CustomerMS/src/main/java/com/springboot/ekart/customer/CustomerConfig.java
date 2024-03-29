package com.springboot.ekart.customer;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomerConfig {
	
private RestTemplate template = new RestTemplate();
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return template;
	}
	

}
