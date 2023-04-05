package com.springboot.ekart.customer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ekart.customer.dto.CustomerDTO;
import com.springboot.ekart.customer.dto.OrderDTO;
import com.springboot.ekart.customer.dto.OrderStatus;
import com.springboot.ekart.customer.dto.OrderedProductDTO;
import com.springboot.ekart.customer.dto.PaymentThrough;
import com.springboot.ekart.customer.dto.ProductDTO;
import com.springboot.ekart.customer.entity.Order;
import com.springboot.ekart.customer.entity.OrderedProduct;
import com.springboot.ekart.customer.exception.EkartCustomerException;
import com.springboot.ekart.customer.repository.OrderRepository;

@Service(value= "orderService")
@Transactional
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Override
	public Integer placeOrder(OrderDTO orderDTO) throws EkartCustomerException {
		CustomerDTO customerDTO = customerService.getCustomerByEmailId(orderDTO.getCustomerEmailId());
		if(customerDTO.getAddress().isBlank() || customerDTO.getAddress() == null) {
			throw new EkartCustomerException("OrderService.ADDRESS_NOT_AVAILABLE");
		}
		
		Order order = new Order();
		order.setDeliveryAddress(customerDTO.getAddress());
		order.setCustomerEmailId(orderDTO.getCustomerEmailId());
		order.setDateOfDelivery(orderDTO.getDateOfDelivery());
		order.setDateOfOrder(orderDTO.getDateOfOrder());
		order.setPaymentThrough(PaymentThrough.valueOf(orderDTO.getPaymentThrough()));
		if (order.getPaymentThrough().equals(PaymentThrough.CREDIT_CARD)) {
			order.setDiscount(10.00d);
		} else {
			order.setDiscount(5.00d);
		}
		order.setOrderStatus(OrderStatus.PLACED);
		
		Double price = 0.0;
		List<OrderedProduct> orderedProducts = new ArrayList<OrderedProduct>();
		
		for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
			if(orderedProductDTO.getProduct().getAvailableQuantity() < orderedProductDTO.getQuantity()) {
				throw new EkartCustomerException("OrderService.INSUFFICIENT_STOCK");
			}
			OrderedProduct orderedProduct = new OrderedProduct();
			orderedProduct.setProductId(orderedProductDTO.getProduct().getProductId());
			orderedProduct.setQuantity(orderedProductDTO.getQuantity());
			orderedProducts.add(orderedProduct);
			price = price + orderedProductDTO.getQuantity()*orderedProductDTO.getProduct().getPrice();
		}
		
		order.setOrderedProducts(orderedProducts);
		order.setTotalPrice(price * (100 - order.getDiscount()/100));
		orderRepository.save(order);
		
		return order.getOrderId();
	}
	
	
	@Override
	public OrderDTO getOrderDetails(Integer orderId) throws EkartCustomerException {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new EkartCustomerException("OrderService.ORDER_NOT_FOUND"));
		
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(orderId);
		orderDTO.setCustomerEmailId(order.getCustomerEmailId());
		orderDTO.setDateOfDelivery(order.getDateOfDelivery());
		orderDTO.setDateOfOrder(order.getDateOfOrder());
		orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
		orderDTO.setTotalPrice(order.getTotalPrice());
		orderDTO.setOrderStatus(order.getOrderStatus().toString());
		orderDTO.setDiscount(order.getDiscount());
		
		List<OrderedProductDTO> orderedProductDTOs = new ArrayList<OrderedProductDTO>();
		
		for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
			OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
			ProductDTO productDTO = new ProductDTO();
			productDTO.setProductId(orderedProduct.getProductId());
			orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
			orderedProductDTO.setQuantity(orderedProduct.getQuantity());
			orderedProductDTO.setProduct(productDTO);
			orderedProductDTOs.add(orderedProductDTO);
		}
		
		orderDTO.setOrderedProducts(orderedProductDTOs);
		return orderDTO;
		
	}
	
	
	@Override
	public void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws EkartCustomerException {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new EkartCustomerException("OrderService.ORDER_NOT_FOUND"));
		order.setOrderStatus(orderStatus);
	}
	
	
	@Override
	public void updatePaymentThrough(Integer orderId, PaymentThrough paymentThrough) throws EkartCustomerException {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
		Order order = optionalOrder.orElseThrow(() -> new EkartCustomerException("OrderService.ORDER_NOT_FOUND"));
		
		if(order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
			throw new EkartCustomerException("OrderService.TRANSACTION_ALREADY_DONE");
		}
		order.setPaymentThrough(paymentThrough);
	}
	
	
	@Override
	public List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws EkartCustomerException {
		List<Order> orders = orderRepository.findByCustomerEmailId(emailId);
		if(orders.isEmpty()) {
			throw new EkartCustomerException("OrderService.NO_ORDERS_FOUND");
		}
		List<OrderDTO> orderDTOs = new ArrayList<>();
		for(Order order : orders) {
			OrderDTO orderDTO = new OrderDTO();
			orderDTO.setOrderId(order.getOrderId());
			orderDTO.setCustomerEmailId(order.getCustomerEmailId());
			orderDTO.setDateOfDelivery(order.getDateOfDelivery());
			orderDTO.setDateOfOrder(order.getDateOfOrder());
			orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
			orderDTO.setTotalPrice(order.getTotalPrice());
			orderDTO.setOrderStatus(order.getOrderStatus().toString());
			orderDTO.setDiscount(order.getDiscount());
			List<OrderedProductDTO> orderedProductDTOs = new ArrayList<OrderedProductDTO>();
			for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
				OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
				ProductDTO productDTO = new ProductDTO();
				productDTO.setProductId(orderedProduct.getProductId());
				orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
				orderedProductDTO.setQuantity(orderedProduct.getQuantity());
				orderedProductDTO.setProduct(productDTO);
				orderedProductDTOs.add(orderedProductDTO);
			}
			orderDTO.setOrderedProducts(orderedProductDTOs);
			orderDTO.setDeliveryAddress(order.getDeliveryAddress());
			orderDTOs.add(orderDTO);
		}
		return orderDTOs;
	}
	


}
