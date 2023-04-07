package com.springboot.ekart.product.api;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ekart.product.dto.ProductDTO;
import com.springboot.ekart.product.exception.EkartProductException;
import com.springboot.ekart.product.service.CustomerProductService;

@CrossOrigin
@RestController
@Validated
@RequestMapping(value = "/product-api")
public class CustomerProductAPI {
	
	@Autowired
	private CustomerProductService customerProductService;
	
	@Autowired
	private Environment environment;
	
	Log logger = LogFactory.getLog(CustomerProductAPI.class);
	
	@GetMapping(value = "/products")
	public ResponseEntity<List<ProductDTO>> getAllProducts() throws EkartProductException {
		return new ResponseEntity<List<ProductDTO>>(customerProductService.getAllProducts(), HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/product/{productId}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId) throws EkartProductException {
		return new ResponseEntity<ProductDTO> (customerProductService.getProductById(productId), HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/update/{productId}")
	public ResponseEntity<String> reduceAvailableQuantity(@PathVariable Integer productId, @RequestBody Integer quantity) throws EkartProductException {
		customerProductService.reduceAvailableQuantity(productId, quantity);
		return new ResponseEntity<String> (environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFUL"), HttpStatus.OK);
	}
	
	

}
