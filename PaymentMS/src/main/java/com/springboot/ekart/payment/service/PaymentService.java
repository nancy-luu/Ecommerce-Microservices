package com.springboot.ekart.payment.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.springboot.ekart.payment.dto.CardDTO;
import com.springboot.ekart.payment.dto.TransactionDTO;
import com.springboot.ekart.payment.exception.EkartPaymentException;
import com.springboot.ekart.payment.exception.PayOrderFallbackException;

public interface PaymentService {
	
	Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws EkartPaymentException, NoSuchAlgorithmException;
	void updateCustomerCard(CardDTO cardDTO) throws EkartPaymentException, NoSuchAlgorithmException;
	void deleteCustomerCard(String customerEmailId, Integer cardId) throws EkartPaymentException;
	CardDTO getCard(Integer cardId) throws EkartPaymentException;
	List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType) throws EkartPaymentException;
	Integer addTransaction (TransactionDTO transactionDTO) throws EkartPaymentException, PayOrderFallbackException;
	TransactionDTO authenticatePayment(String customerEmailId, TransactionDTO transactionDTO) throws EkartPaymentException, NoSuchAlgorithmException;

}
