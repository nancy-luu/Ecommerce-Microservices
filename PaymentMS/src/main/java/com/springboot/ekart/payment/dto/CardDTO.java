package com.springboot.ekart.payment.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDTO {

	private Integer cardId;
	private String cardType;
	@NotNull(message= "{transaction.cardnumber.notpresent}")
	private String cardNumber;
	private String nameOnCard;
	private String hashCvv;
	@NotNull(message= "{transaction.cvv.notpresent}")
	private Integer cvv;
	private String expiryDate;
	private String customerEmailId;
	
	
	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	
	
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	
	public String getNameOnCard() {
		return nameOnCard;
	}
	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}
	
	
	public String getHashCvv() {
		return hashCvv;
	}
	public void setHashCvv(String hashCvv) {
		this.hashCvv = hashCvv;
	}
	
	
	public Integer getCvv() {
		return cvv;
	}
	public void setCvv(Integer cvv) {
		this.cvv = cvv;
	}
	
	
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	
	public String getCustomerEmailId() {
		return customerEmailId;
	}
	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}
	
	
	
}
