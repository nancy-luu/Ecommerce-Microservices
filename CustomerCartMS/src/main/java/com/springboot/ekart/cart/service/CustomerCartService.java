package com.springboot.ekart.cart.service;

import java.util.Set;

import com.springboot.ekart.cart.dto.CartProductDTO;
import com.springboot.ekart.cart.dto.CustomerCartDTO;
import com.springboot.ekart.cart.exception.EkartCustomerCartException;

public interface CustomerCartService {
	
	Integer addProductToCart(CustomerCartDTO customerCart) throws EkartCustomerCartException;
	Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws EkartCustomerCartException;
	void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity) throws EkartCustomerCartException;
	void deleteProductFromCart(String customerEmailId, Integer productId) throws EkartCustomerCartException;
	void deleteAllProductsFromCart(String customerEmailId) throws EkartCustomerCartException;

}
