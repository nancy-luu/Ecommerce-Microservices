package com.springboot.ekart.cart.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ekart.cart.dto.CartProductDTO;
import com.springboot.ekart.cart.dto.CustomerCartDTO;
import com.springboot.ekart.cart.dto.ProductDTO;
import com.springboot.ekart.cart.entity.CartProduct;
import com.springboot.ekart.cart.entity.CustomerCart;
import com.springboot.ekart.cart.exception.EkartCustomerCartException;
import com.springboot.ekart.cart.repository.CartProductRepository;
import com.springboot.ekart.cart.repository.CustomerCartRepository;

@Service(value = "customerCartService")
@Transactional
public class CustomerCartServiceImpl implements CustomerCartService{
	
	@Autowired
	private CustomerCartRepository customerCartRepository;
	
	@Autowired
	private CartProductRepository cartProductRepository;
	
	@Override
	public Integer addProductToCart(CustomerCartDTO customerCartDTO) throws EkartCustomerCartException {
		Set<CartProduct> cartProducts= new HashSet<>();
		Integer cartId = null;
		
		for(CartProductDTO cartProductDTO : customerCartDTO.getCartProducts()) 
		{
			CartProduct cartProduct = new CartProduct();
			cartProduct.setProductId(cartProductDTO.getProduct().getProductId());
			cartProduct.setQuantity(cartProductDTO.getQuantity());
			cartProducts.add(cartProduct);
		}
		
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerCartDTO.getCustomerEmailId());
		if(cartOptional.isEmpty()) 
		{
			CustomerCart newCart = new CustomerCart();
			newCart.setCustomerEmailId(customerCartDTO.getCustomerEmailId());
			newCart.setCartProducts(cartProducts);
			customerCartRepository.save(newCart);
			cartId = newCart.getCartId();
		}
		else {
			CustomerCart cart = cartOptional.get();
			for(CartProduct cartProductToBeAdded: cartProducts) {
				Boolean checkProductAlreadyPresent = false; {
					for(CartProduct cartProductFromCart: cart.getCartProducts()) {
						if (cartProductFromCart.equals(cartProductToBeAdded)) {
							cartProductFromCart.setQuantity(cartProductToBeAdded.getQuantity()+cartProductFromCart.getQuantity());
							checkProductAlreadyPresent=true;
						}
						if (checkProductAlreadyPresent == false) {
							cart.getCartProducts().add(cartProductToBeAdded);
						}
					}
				}
			}
			cartId = cart.getCartId();
		}	
		return cartId;
	}
	
	
	// THIS METHOD RETRIEVES CUSTOMER DATA FROM REPOSITORY AND RETURNS CART DETAILS OF THAT CUSTOMER
	@Override
	public Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws EkartCustomerCartException {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		Set<CartProductDTO> cartProductsDTO = new HashSet<>();
		CustomerCart cart = cartOptional
				.orElseThrow(() -> new EkartCustomerCartException("CustomerCartService.NO_CART_FOUND"));
		
		if (cart.getCartProducts().isEmpty()) {
			throw new EkartCustomerCartException("CustmoerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		
		Set<CartProduct> cartProducts = cart.getCartProducts();
		for(CartProduct cartProduct : cartProducts) {
			CartProductDTO cartProductDTO = new CartProductDTO();
			cartProductDTO.setCartProductId(cartProduct.getCartProductId());
			cartProductDTO.setQuantity(cartProduct.getQuantity());
			ProductDTO productDTO = new ProductDTO();
			productDTO.setProductId(cartProduct.getProductId());
			cartProductDTO.setProduct(productDTO);
			cartProductsDTO.add(cartProductDTO);
		}
		return cartProductsDTO;
	}
	
	
	// THIS METHOD RETRIEVES CUSTOMER DATA FROM REPOSITORY AND RETURNS CART DETAILS OF THAT CUSTOMER
	@Override
	public void deleteProductFromCart(String customerEmailId, Integer productId) throws EkartCustomerCartException {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional
				.orElseThrow(() -> new EkartCustomerCartException("CustomerCartService.NO_CART_FOUND"));
		
		if (cart.getCartProducts().isEmpty()) {
			throw new EkartCustomerCartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		CartProduct selectedProduct = null;
		for (CartProduct product : cart.getCartProducts()) {
			if (product.getProductId().equals(productId)) {
				selectedProduct = product;
			}
		}
		if (selectedProduct == null) {
			throw new EkartCustomerCartException("CustomerCartService.PRODUCT_ALREADY_NOT_AVAILABLE");	
		}
		
		cart.getCartProducts().remove(selectedProduct);
		cartProductRepository.delete(selectedProduct);
	}
	
	@Override
	public void deleteAllProductsFromCart(String customerEmailId) throws EkartCustomerCartException {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional
				.orElseThrow(() -> new EkartCustomerCartException("CustomerCartService.NO_CARTR_FOUND"));
		
		if (cart.getCartProducts().isEmpty()) {
			throw new EkartCustomerCartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		
		List<Integer> productIds = new ArrayList<>();
		cart.getCartProducts().parallelStream().forEach(cp -> {
			productIds.add(cp.getCartProductId());
			cart.getCartProducts().remove(cp);
		});
		
		productIds.forEach(pid -> {
			cartProductRepository.deleteById(pid);
		});
			
	}

	@Override
	public void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity) throws EkartCustomerCartException {
		Optional<CustomerCart> cartOptional = customerCartRepository.findByCustomerEmailId(customerEmailId);
		CustomerCart cart = cartOptional
				.orElseThrow(() -> new EkartCustomerCartException("CustomerCartService.NO_CART_FOUND"));
		
		if (cart.getCartProducts().isEmpty()) {
			throw new EkartCustomerCartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
		}
		
		CartProduct selectedProduct = null;
		for (CartProduct product : cart.getCartProducts()) {
			if (product.getProductId().equals(productId)) {
				selectedProduct = product;
			}
		}
		if (selectedProduct == null) {
			throw new EkartCustomerCartException("CustomerCartService.PRODUCT_ALREADY_NOTAVAILABLE");
		}
		selectedProduct.setQuantity(quantity);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
