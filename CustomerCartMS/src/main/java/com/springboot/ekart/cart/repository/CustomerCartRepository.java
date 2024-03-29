package com.springboot.ekart.cart.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.cart.entity.CustomerCart;

public interface CustomerCartRepository extends CrudRepository<CustomerCart, Integer>{
	
	Optional<CustomerCart> findByCustomerEmailId(String customerEmailId);

}
