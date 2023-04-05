package com.springboot.ekart.customer.service;

import java.util.List;

import com.springboot.ekart.customer.dto.OrderDTO;
import com.springboot.ekart.customer.dto.OrderStatus;
import com.springboot.ekart.customer.dto.PaymentThrough;
import com.springboot.ekart.customer.exception.EkartCustomerException;

public interface OrderService {
	
	Integer placeOrder(OrderDTO orderDTO) throws EkartCustomerException;
	OrderDTO getOrderDetails(Integer orderId) throws EkartCustomerException;
	List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws EkartCustomerException;
	void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws EkartCustomerException;
	void updatePaymentThrough(Integer orderId, PaymentThrough payment) throws EkartCustomerException;

}
