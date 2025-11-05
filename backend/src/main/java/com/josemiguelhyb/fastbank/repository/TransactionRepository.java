package com.josemiguelhyb.fastbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	@Query("DELETE FROM Transaction t WHERE t.account.id = :accountId")
	void deleteByAccountId(@Param("accountId") Long accountId);

	@Modifying
	@Query("DELETE FROM Transaction t WHERE t.account.user.id = :userId")
	void deleteByAccountUserId(@Param("userId") Long userId);
}
