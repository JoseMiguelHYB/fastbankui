package com.josemiguelhyb.fastbank.service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josemiguelhyb.fastbank.model.Account;
import com.josemiguelhyb.fastbank.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {
	
	private final AccountRepository accountRepository;
	
	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}	

	@Override
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}	
	
	@Override
	public Optional<Account> getAccountById(Long id) {
		return accountRepository.findById(id);
	}
	
	@Override
	@Transactional
	public Account createAccount(Account account) {
		// 1 - Validar si ya existe una cuenta con el mismo número
		
		Optional<Account> existingAccount = accountRepository.findByAccountNumber(account.getAccountNumber());
		if(existingAccount.isPresent()) {
            throw new IllegalArgumentException("⚠️ Ya existe una cuenta con el número: " + account.getAccountNumber());
		}
				
		// 2 - Guardar la nueva cuenta
		return accountRepository.save(account);
	}	

	@Override
	@Transactional
	public Account updateAccount(Long id, Account changes) {
		Account current = accountRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

		// accountNumber: si cambia, validar unicidad
		if (changes.getAccountNumber() != null && !changes.getAccountNumber().isBlank()) {
			String newNumber = changes.getAccountNumber();
			if (!newNumber.equals(current.getAccountNumber())) {
				accountRepository.findByAccountNumber(newNumber).ifPresent(a -> {
					throw new IllegalArgumentException("⚠️ Ya existe una cuenta con el número: " + newNumber);
				});
				current.setAccountNumber(newNumber);
			}
		}

		// balance: si se informa, validar no negativo
		BigDecimal newBalance = changes.getBalance();
		if (newBalance != null) {
			if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("El balance no puede ser negativo");
			}
			current.setBalance(newBalance);
		}

		// user: opcional (re-asignación)
		if (changes.getUser() != null) {
			current.setUser(changes.getUser());
		}

		return accountRepository.save(current);
	}

	@Override
	@Transactional
	public void deleteAccount(Long id) {
		if (!accountRepository.existsById(id)) {
			throw new IllegalArgumentException("Cuenta no encontrada");
		}
		accountRepository.deleteById(id);
	}
}
