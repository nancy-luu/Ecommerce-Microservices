package com.springboot.ekart.customer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.customer.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String>{
	
	List<Customer> findByPhoneNumber(String phoneNumber);

}
