package com.springboot.ekart.customer.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.springboot.ekart.customer.dto.CartProductDTO;
import com.springboot.ekart.customer.dto.OrderDTO;
import com.springboot.ekart.customer.dto.OrderStatus;
import com.springboot.ekart.customer.dto.OrderedProductDTO;
import com.springboot.ekart.customer.dto.PaymentThrough;
import com.springboot.ekart.customer.dto.ProductDTO;
import com.springboot.ekart.customer.exception.EkartCustomerException;
import com.springboot.ekart.customer.service.OrderService;

@CrossOrigin
@RequestMapping(value = "/customerorder-api")
@RestController
@Validated
public class OrderAPI {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RestTemplate template;
	
	
		// Fetches cart-products of given customer by calling CartMS API
		// Removes all the products in the cart for the customer
		// For each cart-product, populate an ordered-product object
		// Update the ordered-product list of order
		// Save order details by calling placeOrder() from orderService
	@PostMapping(value = "/place-order")
	public ResponseEntity<String> placeOrder (@Valid @RequestBody OrderDTO order) throws EkartCustomerException {
		
		ResponseEntity<CartProductDTO[]> cartProductDTOsResponse = template.getForEntity("http://CustomerCartMS/Ekart/customercart-api/customer/" + order.getCustomerEmailId() + "/products", CartProductDTO[].class);
		
		CartProductDTO[] cartProductDTOs = cartProductDTOsResponse.getBody();
		
		// template is in config file
		template.delete("http://CustomerCartMS/Ekart/customercart-api/customer/" + order.getCustomerEmailId() + "/products");
		
		List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();
		for (CartProductDTO cartProductDTO : cartProductDTOs) {
			OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
			orderedProductDTO.setProduct(cartProductDTO.getProduct());
			orderedProductDTO.setQuantity(cartProductDTO.getQuantity());
			orderedProductDTOs.add(orderedProductDTO);
		}
		
		order.setOrderedProducts(orderedProductDTOs);
		
		Integer orderId = orderService.placeOrder(order);
		String modificationSuccessMsg = environment.getProperty("OrderAPI.ORDER_PLACED_SUCCESSFULLY");
		
		return new ResponseEntity<>(modificationSuccessMsg + orderId, HttpStatus.OK);
	}
	
	
		// Every OrderedProductDTO object has ProductDTO object which in turn has only productIDs
		// Iterate over OrderedProductDTO to get the product details
		// Update the product details of OrderedProductDTO with the fetched ProductDTO received in previous step
		// Return the order details
	@GetMapping(value = "order/{orderId}")
	public ResponseEntity<OrderDTO> getOrderDetails(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId) throws EkartCustomerException {
		OrderDTO orderDTO = orderService.getOrderDetails(orderId);
		
		for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
			ResponseEntity<ProductDTO> productResponse = template.getForEntity("http://ProductMS/Ekart/product-api/product/" + orderedProductDTO.getProduct().getProductId(), ProductDTO.class);
			orderedProductDTO.setProduct(productResponse.getBody());
		}
		return new ResponseEntity<>(orderDTO, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/customer/{customerEmailId}/orders")
	public ResponseEntity<List<OrderDTO>> getOrderCustomer(@Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable String customerEmailId) throws EkartCustomerException {
		List<OrderDTO> orderDTOs = orderService.findOrdersByCustomerEmailId(customerEmailId);
		
		for(OrderDTO orderDTO : orderDTOs) {
			for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
				ResponseEntity<ProductDTO> productResponse = template.getForEntity("http://ProductMS/Ekart/product-api/product/" + orderedProductDTO.getProduct().getProductId(), ProductDTO.class);
				orderedProductDTO.setProduct(productResponse.getBody());
			}
		}
		return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/order/{orderId}/update/order-status")
	public void updateOrderAfterPayment(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId, @RequestBody String transactionStatus) throws EkartCustomerException {
	
		if (transactionStatus.equals("TRANSACTION_SUCCESS")) {
			orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
			OrderDTO orderDTO = orderService.getOrderDetails(orderId);
			
			for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
				template.put("http://ProductMS/Ekart/product-api/update/" + orderedProductDTO.getProduct().getProductId(), orderedProductDTO.getQuantity());
			}
		} else {
			orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
		}
	}
	
	
	@PutMapping(value = "order/{orderId}/update/order-status")
	public void updatePaymentOption(@NotNull(message = "{orderedId.absent}") @PathVariable Integer orderId, @RequestBody String paymentThrough) throws EkartCustomerException {
		if(paymentThrough.equalsIgnoreCase("DEBIT_CARD")) {
			orderService.updatePaymentThrough(orderId, PaymentThrough.DEBIT_CARD);
		} else {
			orderService.updatePaymentThrough(orderId, PaymentThrough.CREDIT_CARD);
		}
	}
	

}
