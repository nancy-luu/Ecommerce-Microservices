package com.springboot.ekart.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ekart.product.dto.ProductDTO;
import com.springboot.ekart.product.entity.Product;
import com.springboot.ekart.product.exception.EkartProductException;
import com.springboot.ekart.product.repository.ProductRepository;

@Service(value = "customerProductService")
@Transactional
public class CustomerProductServiceImpl implements CustomerProductService{
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<ProductDTO> getAllProducts() throws EkartProductException {
		Iterable<Product> products = productRepository.findAll();
		List<ProductDTO> productDTOs = new ArrayList<>();
		
		products.forEach(p -> {
			ProductDTO dto = new ProductDTO();
			dto.setAvailableQuantity(p.getAvailableQuantity());
			dto.setCategory(p.getCategory());
			dto.setBrand(p.getBrand());
			dto.setDescription(p.getDescription());
			dto.setName(p.getName());
			dto.setPrice(p.getPrice());
			dto.setProductId(p.getProductId());
			productDTOs.add(dto);
		});
		
		return null;
	}

	
	
	@Override
	public ProductDTO getProductById(Integer productId) throws EkartProductException {
		Optional<Product> productOptional = productRepository.findById(productId);
		
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			ProductDTO productDTO = new ProductDTO();
			productDTO.setAvailableQuantity(product.getAvailableQuantity());
			productDTO.setBrand(product.getBrand());
			productDTO.setCategory(product.getCategory());
			productDTO.setDescription(product.getDescription());
			productDTO.setName(product.getName());
			productDTO.setPrice(product.getPrice());
			productDTO.setProductId(product.getProductId());
			return productDTO;
		} else {
			throw new EkartProductException("ProductService.PRODUCT_NOT_AVAILABLE");
		}
	}
	
	

	@Override
	public void reduceAvailableQuantity(Integer productId, Integer quantity) throws EkartProductException {
		Optional<Product> productOptional = productRepository.findById(productId);
		if(productOptional.isPresent()) {
			Product product = productOptional.get();
			product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
		} else {
			throw new EkartProductException("ProductService.PRODUCT_NOT_AVAILABLE");
		}
		
	}

	
	
}
