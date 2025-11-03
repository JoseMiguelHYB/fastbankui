package com.josemiguelhyb.fastbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.josemiguelhyb.fastbank.model.Account;
import com.josemiguelhyb.fastbank.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByAccount(Account account);
	List<Transaction> findAllByOrderByCreatedAtDesc();
	List<Transaction> findAllByOrderByCreatedAtAsc();
	List<Transaction> findByAccountOrderByCreatedAtAsc(Account account);
	List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);

	// Borrado masivo por cuenta o por usuario (a través de la relación de cuenta)
	@Modifying
	void deleteByAccountId(Long accountId);

	@Modifying
	void deleteByAccountUserId(Long userId);
}
