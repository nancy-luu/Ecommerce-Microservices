package com.springboot.ekart.cart.repository;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.cart.entity.CartProduct;

public interface CartProductRepository extends CrudRepository<CartProduct, Integer>{
	

}
