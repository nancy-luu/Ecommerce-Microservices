package com.springboot.ekart.customer.api;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.springboot.ekart.customer.dto.CartProductDTO;
import com.springboot.ekart.customer.dto.CustomerCartDTO;
import com.springboot.ekart.customer.dto.CustomerDTO;
import com.springboot.ekart.customer.dto.ProductDTO;
import com.springboot.ekart.customer.exception.EkartCustomerException;
import com.springboot.ekart.customer.service.CustomerService;

@CrossOrigin
@RequestMapping(value = "/customer-api")
@RestController
@Validated
public class CustomerAPI {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	private Environment environment;
	
	static Log logger = LogFactory.getLog(CustomerAPI.class);
	
	@PostMapping(value = "/login")
	public ResponseEntity<CustomerDTO> authenticateCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws EkartCustomerException {
		logger.info("CUSTOMER TRYING TO LOGIN, VALIDATING CREDENTIALS. CUSTOMER EMAIL ID: " + customerDTO.getEmailId());
		CustomerDTO customerDTOFromDB = customerService.authenticateCustomer(customerDTO.getEmailId(), customerDTO.getPassword());
		logger.info("CUSTOMER LOGIN SUCCESS, CUSTOMER EMAIL: " + customerDTOFromDB.getEmailId());
		
		return new ResponseEntity<>(customerDTOFromDB, HttpStatus.OK);
	}
	
	
	
	@PostMapping(value = "/regster")
	public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) throws EkartCustomerException {
		logger.info("CUSTOMER TRYING TO REGISTER. CUSTOMER EMAIL ID: " + customerDTO.getEmailId());
		String registeredWithEmailID = customerService.registerNewCustomer(customerDTO);
		registeredWithEmailID = environment.getProperty("CustomerAPI.CUSTOMER_REGISTRATION_SUCCESS") + registeredWithEmailID;
		
		return new ResponseEntity<>(registeredWithEmailID, HttpStatus.OK);
	}
	
	
	
	@PutMapping(value = "/customer/{customerEmailId:.+}/address/")
	public ResponseEntity<String> updateShippingAddress(@Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") String customerEmailId, @RequestBody String address) throws EkartCustomerException {
		customerService.updateShippingAddress(customerEmailId, address);
		String modificationSuccessMsg = environment.getProperty("CustomerAPI.UPDATE_ADDRESS_SUCCESS");
		
		return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);
	}
	
	
	
	@DeleteMapping(value = "/customer/{customerEmailId:.+}")
	public ResponseEntity<String> deleteShippingAddress(@Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId) throws EkartCustomerException {
		  customerService.deleteShippingAddress(customerEmailId);
		  String modificationSuccessMsg = environment.getProperty("CustomerAPI.CUSTOMER_ADDRESS_DELETED_SUCCESS");
		  
		  return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);
	}
	
	
	
	@PostMapping(value = "/customercarts/add-product")
	public ResponseEntity<String>addProductToCart(@Valid @RequestBody CustomerCartDTO customerCartDTO) throws EkartCustomerException {
		customerService.getCustomerByEmailId(customerCartDTO.getCustomerEmailId());
		
		for(CartProductDTO cartProductDTO : customerCartDTO.getCartProducts()) {
			// Calling the product API using hard-coded URI with appropriate micro-service name
			// using load balanced template to make call the the product API
			template.getForEntity("http://ProductMS/Ekart/product-api/product/"+cartProductDTO.getProduct().getProductId(), ProductDTO.class);
		}
		
		// Calling the cart API using hard-coded URI with appropriate micro-service name
		// template is already in config file
		ResponseEntity<String> productAddToCartMessage = template.postForEntity("http://CustomerCartMS/Ekart/customercart-api/products", customerCartDTO, String.class);
		return productAddToCartMessage;
	}
	

}
