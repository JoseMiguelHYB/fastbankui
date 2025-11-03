package com.josemiguelhyb.fastbank.service;

import java.math.BigDecimal;
import java.util.List;

import com.josemiguelhyb.fastbank.model.Account;
import com.josemiguelhyb.fastbank.model.Transaction;

public interface TransactionService {
	Transaction deposit(Long accountId, BigDecimal amount, String description);
	Transaction withdraw(Long accountId, BigDecimal amount, String description);
	Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description);
	List<Transaction> getTransactionsByAccount(Account accountId);
	List<Transaction> getAllTransactions();
	List<Transaction> getAllTransactions(String order);
}
