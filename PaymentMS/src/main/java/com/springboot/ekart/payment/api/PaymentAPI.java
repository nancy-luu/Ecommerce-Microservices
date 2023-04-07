package com.springboot.ekart.payment.api;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.springboot.ekart.payment.dto.CardDTO;
import com.springboot.ekart.payment.dto.OrderDTO;
import com.springboot.ekart.payment.dto.TransactionDTO;
import com.springboot.ekart.payment.exception.EkartPaymentException;
import com.springboot.ekart.payment.exception.PayOrderFallbackException;
import com.springboot.ekart.payment.service.PaymentCircuitBreakerService;
import com.springboot.ekart.payment.service.PaymentService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


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
	
	public static final Log LOGGER = LogFactory.getLog(PaymentAPI.class);

	@Autowired
	private PaymentCircuitBreakerService paymentCircuitBreakerService;
	
	@PostMapping(value = "/customer/{customerEmailId:.+}/cards")
	public ResponseEntity<String> addNewCard(@RequestBody CardDTO cardDTO, @Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId) throws EkartPaymentException, NoSuchAlgorithmException {
		LOGGER.info("Received request to add new card for customer : " + cardDTO.getCustomerEmailId());
		int cardId;
		cardId = paymentService.addCustomerCard(customerEmailId, cardDTO);
		String message = environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS");
		String toReturn = message + cardId;
		toReturn = toReturn.trim();
		
		return new ResponseEntity<>(toReturn, HttpStatus.OK);		
	}
	
	
	@PutMapping(value = "/update/card")
	public ResponseEntity<String> updateCustomerCard(@Valid @RequestBody CardDTO cardDTO) throws EkartPaymentException, NoSuchAlgorithmException {
		LOGGER.info("Received request to update card:" + cardDTO.getCardId() + " of customer: " + cardDTO.getCustomerEmailId());
		paymentService.updateCustomerCard(cardDTO);
		String modificationSuccessMsg = environment.getProperty("PaymentAPI.UPDATE_CARD_SUCCESS");

		return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/customer/{customerEmailId:.+}/card/{cardID}/delete")
	public ResponseEntity<String> deleteCustomerCard(@PathVariable("cardID") Integer cardID, @Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId) throws EkartPaymentException {
		LOGGER.info("Received request to delet card :" + cardID + " of customer : " + customerEmailId);
		paymentService.deleteCustomerCard(customerEmailId, cardID);
		String modificationSuccessMsg = environment.getProperty("PaymentAPI.CUSTOMER_CARD_DELETED_SUCCESS");
		
		return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/customer/{customerEmailId}/card-type/{cardType}")
	public ResponseEntity<List<CardDTO>> getCardsOfCustomer (@PathVariable String customerEmailId, @PathVariable String cardType) throws EkartPaymentException {
		List<CardDTO> cards = paymentService.getCustomerCardOfCardType(customerEmailId, cardType);
		
		return new ResponseEntity<List<CardDTO>>(cards, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/customer/{customerEmailId}/pay-order")
	@CircuitBreaker(name = "paymentService", fallbackMethod = "payForOrderFallback")
	public ResponseEntity<String> payForOrder(@Pattern(regexp = "[a-zA-Z0-9]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}") @PathVariable("customerEmailId") String customerEmailId, @Valid @RequestBody TransactionDTO transactionDTO) throws EkartPaymentException, NoSuchAlgorithmException, PayOrderFallbackException {
		Integer orderId = transactionDTO.getOrder().getOrderId();
		OrderDTO order = template.getForEntity("http://CustomerMS/Ekart/customerorder-api/order/"+orderId, OrderDTO.class).getBody();
		transactionDTO.setOrder(order);
		transactionDTO.setTransactionDate(LocalDateTime.now());
		transactionDTO.setTotalPrice(order.getTotalPrice());
		transactionDTO = paymentService.authenticatePayment(customerEmailId, transactionDTO);
		Integer transactionId = paymentService.addTransaction(transactionDTO);
		paymentCircuitBreakerService.updateOrderAfterPayment(orderId, transactionDTO.getTransactionStatus().toString());
		String successMessage = environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFUL_ONE") + transactionDTO.getTotalPrice() + " " + environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFUL_TWO") + orderId + environment.getProperty("PaymentAPI.TRANSACTION_SUCCESSFUL_THREE") + transactionId;
		
		return new ResponseEntity<String>(successMessage, HttpStatus.OK);
	}
	
	
	// Implementing fallback method
	public ResponseEntity<String> payForOrderFallback(String customerEmailId, TransactionDTO transactionDTO, RuntimeException exception) throws RuntimeException {
		LOGGER.info("**********In Fallback**********");
		LOGGER.info("Customer with customer Email Id: " + customerEmailId + "\n and Order Id: " + transactionDTO.getOrder().getOrderId());
		
		String message;
		
		if(exception.getMessage().equals("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING")) {
			message = environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
		} else {
			message = environment.getProperty("PaymentAPI.PAYMENT_FAILURE_FALLBACK");
		}
		
		return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
	}
	
	
	

}
