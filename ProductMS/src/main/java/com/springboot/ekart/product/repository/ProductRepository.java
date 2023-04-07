package com.springboot.ekart.product.repository;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.product.entity.Product;

public interface ProductRepository extends CrudRepository <Product, Integer>{

}
