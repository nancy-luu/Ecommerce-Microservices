package com.springboot.ekart.customer.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.customer.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Integer>{
	
	List<Order> findByCustomerEmailId(String customerEmailId);

}
