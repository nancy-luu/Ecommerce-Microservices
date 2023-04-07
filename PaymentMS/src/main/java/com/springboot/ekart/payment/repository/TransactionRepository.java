package com.springboot.ekart.payment.repository;

import org.springframework.data.repository.CrudRepository;

import com.springboot.ekart.payment.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Integer>{
		
}
