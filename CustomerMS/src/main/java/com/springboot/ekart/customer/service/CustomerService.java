package com.springboot.ekart.customer.service;

import com.springboot.ekart.customer.dto.CustomerDTO;
import com.springboot.ekart.customer.exception.EkartCustomerException;

public interface CustomerService {
	
	CustomerDTO authenticateCustomer(String emailId, String password) throws EkartCustomerException;
	String registerNewCustomer(CustomerDTO customerDTO) throws EkartCustomerException;
	// void updateProfile(CustomerDTO customerDTO) throws EkartCustomerException;
	// void changePassword(String customerEmailId, String currentPassword, String newPassword) throws EkartCustomerException;
	void updateShippingAddress(String customerEmailId, String address) throws EkartCustomerException;
	void deleteShippingAddress(String customerEmailId) throws EkartCustomerException;
	CustomerDTO getCustomerByEmailId(String emailId) throws EkartCustomerException;
	
	
}
