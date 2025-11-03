package com.josemiguelhyb.fastbank.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josemiguelhyb.fastbank.dto.AccountResponse;
import com.josemiguelhyb.fastbank.dto.CreateAccountRequest;
import com.josemiguelhyb.fastbank.mapper.AccountMapper;
import com.josemiguelhyb.fastbank.model.Account;
import com.josemiguelhyb.fastbank.model.User;
import com.josemiguelhyb.fastbank.service.AccountService;
import com.josemiguelhyb.fastbank.service.UserService;
import com.josemiguelhyb.fastbank.dto.UpdateAccountRequest;

import jakarta.validation.Valid;
import java.security.SecureRandom;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
	
	private final AccountService accountService;
	private final UserService userService;
	
	public AccountController(AccountService accountService, UserService userService) {
		this.accountService = accountService;
		this.userService = userService;
	}
		
	// Crear una cuenta nueva
	@PostMapping
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
		User user = userService.getUserById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Generar un número de cuenta corto y legible (ACC- + 6 dígitos)
		// Reintenta unas pocas veces si colisiona por unicidad
		Account newAccount = null;
		int attempts = 0;
		IllegalArgumentException lastDup = null;
		while (attempts < 5 && newAccount == null) {
			attempts++;
			try {
				String number = generateShortAccountNumber();
				Account account = new Account();
				account.setAccountNumber(number);
				account.setBalance(request.getInitialBalance());
				account.setUser(user);
				newAccount = accountService.createAccount(account);
			} catch (IllegalArgumentException iae) {
				lastDup = iae;
				// Si el mensaje indica número duplicado, volvemos a intentar
				if (iae.getMessage() != null && iae.getMessage().contains("Ya existe una cuenta")) {
					continue;
				}
				throw iae;
			}
		}
		if (newAccount == null && lastDup != null) throw lastDup;
		return ResponseEntity.ok(AccountMapper.toResponse(newAccount));
	}

	private static final SecureRandom RAND = new SecureRandom();
	private String generateShortAccountNumber() {
		int n = RAND.nextInt(1_000_000); // 0..999999
		String suffix = String.format("%06d", n);
		return "ACC- " + suffix;
	}
	
	
    // Obtener una cuenta por ID
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> ResponseEntity.ok(AccountMapper.toResponse(account)))
                .orElse(ResponseEntity.notFound().build());
    }
	
	
    // Obtener todas las cuentas
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts().stream()
                .map(AccountMapper::toResponse)
                .collect(Collectors.toList());
    }	

	// Actualizar una cuenta
	@PutMapping("/{id}")
	public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id, @RequestBody UpdateAccountRequest request) {
		Account changes = new Account();
		if (request.getAccountNumber() != null) changes.setAccountNumber(request.getAccountNumber());
		if (request.getBalance() != null) changes.setBalance(request.getBalance());
		if (request.getUserId() != null) {
			User user = userService.getUserById(request.getUserId())
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
			changes.setUser(user);
		}
		Account updated = accountService.updateAccount(id, changes);
		return ResponseEntity.ok(AccountMapper.toResponse(updated));
	}

	// Eliminar una cuenta
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
		accountService.deleteAccount(id);
		return ResponseEntity.noContent().build();
	}
}
