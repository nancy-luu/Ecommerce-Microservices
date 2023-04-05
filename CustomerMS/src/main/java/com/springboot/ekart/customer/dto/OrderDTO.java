package com.springboot.ekart.customer.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class OrderDTO {

	private Integer orderId;
	@NotNull(message = "{email.absent}")
	@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+", message = "{invalid.email.format}")
	private String customerEmailId;
	private LocalDateTime dateOfOrder;
	private Double totalPrice;
	private String orderStatus;
	private Double discount;
	@NotNull(message = "{order.paymentthrough.absent")
	@Pattern(regexp = "{DEBIT_CARD|CREDIT_CARD}", message = "{order.paymentthrough.invalid}")
	private String paymentThrough;
	@NotNull(message = "{order.dateofdelivery.absent}")
	@Future(message = "{order.dateofdelivery.invalid}")
	private LocalDateTime dateOfDelivery;
	private String deliveryAddress;
	private List<OrderedProductDTO> orderedProducts;
	
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	
	public String getCustomerEmailId() {
		return customerEmailId;
	}
	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}
	
	
	public LocalDateTime getDateOfOrder() {
		return dateOfOrder;
	}
	public void setDateOfOrder(LocalDateTime dateOfOrder) {
		this.dateOfOrder = dateOfOrder;
	}
	
	
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	
	public String getPaymentThrough() {
		return paymentThrough;
	}
	public void setPaymentThrough(String paymentThrough) {
		this.paymentThrough = paymentThrough;
	}
	
	
	public LocalDateTime getDateOfDelivery() {
		return dateOfDelivery;
	}
	public void setDateOfDelivery(LocalDateTime dateOfDelivery) {
		this.dateOfDelivery = dateOfDelivery;
	}
	
	
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	
	
	public List<OrderedProductDTO> getOrderedProducts() {
		return orderedProducts;
	}
	public void setOrderedProducts(List<OrderedProductDTO> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}
	
	
	
}
