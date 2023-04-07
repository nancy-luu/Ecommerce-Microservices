package com.springboot.ekart.payment.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.springboot.ekart.payment.service.PaymentCircuitBreakerService;
import com.springboot.ekart.payment.service.PaymentService;


@CrossOrigin
@RestController
@Validated
@RequestMapping(value="/payment-api")
public class PaymentAPI {
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestTemplate template;
	
	Log logger = LogFactory.getLog(PaymentAPI.class);

	@Autowired
	private PaymentCircuitBreakerService paymentCircuitBreakerService;
	
	
	

}
