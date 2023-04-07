package com.springboot.ekart.product.service;

import java.util.List;

import com.springboot.ekart.product.dto.ProductDTO;
import com.springboot.ekart.product.exception.EkartProductException;

public interface CustomerProductService {

	List<ProductDTO> getAllProducts() throws EkartProductException;
	ProductDTO getProductById(Integer productId) throws EkartProductException;
	void reduceAvailableQuantity(Integer productId, Integer quantity) throws EkartProductException;
	
}
