package com.springboot.ekart.customer.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ekart.customer.dto.CustomerDTO;
import com.springboot.ekart.customer.entity.Customer;
import com.springboot.ekart.customer.exception.EkartCustomerException;
import com.springboot.ekart.customer.repository.CustomerRepository;

@Service(value ="customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public CustomerDTO authenticateCustomer(String emailId, String password) throws EkartCustomerException {
		CustomerDTO customerDTO = null;
		
		// retrieving customer data from repository
		Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new EkartCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		
		// comparing entered password with password stored in DB
		if (!password.equals(customer.getPassword())) throw new EkartCustomerException("CustomerService.INVALID_CREDENTIALS");
		
		customerDTO = new CustomerDTO();
		customerDTO.setEmailId(customer.getEmailId());
		customerDTO.setName(customer.getName());
		customerDTO.setPhoneNumber(customer.getPhoneNumber());
		customerDTO.setAddress(customer.getAddress());
		return customerDTO;
	}
	
	@Override
	public String registerNewCustomer(CustomerDTO customerDTO) throws EkartCustomerException {
		String registeredWithEmailId = null;
		boolean isEmailNotAvailable = customerRepository.findById(customerDTO.getEmailId().toLowerCase()).isEmpty();
		boolean isPhoneNumberNotAvailable = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isEmpty();
		if (isEmailNotAvailable) {
			if (isPhoneNumberNotAvailable) {
				Customer customer = new Customer();
				customer.setEmailId(customerDTO.getEmailId().toLowerCase());
				customer.setName(customerDTO.getName());
				customer.setPassword(customerDTO.getPassword());
				customer.setPhoneNumber(customerDTO.getPhoneNumber());
				customer.setAddress(customerDTO.getAddress());
				customerRepository.save(customer);
				registeredWithEmailId = customer.getEmailId();
			} else {
				throw new EkartCustomerException("CustomerService.PHONE_NUMBER_ALREADY_IN_USE");
			}
		} else {
			throw new EkartCustomerException("CustomerService.EMAIL_ID_ALREADY_IN_USE");
		}
		return registeredWithEmailId;
	}
	
	
	@Override
	public void updateShippingAddress(String customerId, String address) throws EkartCustomerException {
		Optional<Customer> optionalCustomer = customerRepository.findById(customerId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new EkartCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(address);
	}
	
	@Override
	public void deleteShippingAddress(String customerEmailId) throws EkartCustomerException{
		Optional<Customer> optionalCustomer = customerRepository.findById(customerEmailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new EkartCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		customer.setAddress(null);
	}
	
	@Override
	public CustomerDTO getCustomerByEmailId(String emailId) throws EkartCustomerException {
		CustomerDTO customerDTO = null;
		
		Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
		Customer customer = optionalCustomer.orElseThrow(() -> new EkartCustomerException("CustomerService.CUSTOMER_NOT_FOUND"));
		
		customerDTO = new CustomerDTO();
		customerDTO.setEmailId(customer.getEmailId());
		customerDTO.setName(customer.getName());
		customerDTO.setPhoneNumber(customer.getPhoneNumber());
		customerDTO.setAddress(customer.getAddress());
		return customerDTO;
	}
	
	

}
