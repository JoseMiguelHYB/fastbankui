package com.josemiguelhyb.fastbank.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.josemiguelhyb.fastbank.dto.CreateTransactionRequest;
import com.josemiguelhyb.fastbank.dto.TransactionResponse;
import com.josemiguelhyb.fastbank.mapper.TransactionMapper;
import com.josemiguelhyb.fastbank.model.Account;
import com.josemiguelhyb.fastbank.model.Transaction;
import com.josemiguelhyb.fastbank.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	// POST /api/transactions/deposit (INGRESAR DINERO)
	@PostMapping("/deposit")
	public ResponseEntity<TransactionResponse> deposit(@RequestBody CreateTransactionRequest request) {
		Transaction transaction = transactionService.deposit(request.getToAccountId(), request.getAmount(), request.getDescription());
		return ResponseEntity.ok(TransactionMapper.toResponse(transaction));		
	}
	
	// POST /api/transactions/withedraw (RETIRAR DINERO)
	@PostMapping("/withdraw")
	public ResponseEntity<TransactionResponse> withdraw(@RequestBody CreateTransactionRequest request) {
		Transaction transaction = transactionService.withdraw(request.getFromAccountId(), request.getAmount(), request.getDescription());
		return ResponseEntity.ok(TransactionMapper.toResponse(transaction));		
	}
	
	// POST /api/transactions/transfer (TRANSFERIR ENTRE CUENTAS)
	@PostMapping("/transfer")
	public ResponseEntity<TransactionResponse> transfer(@RequestBody CreateTransactionRequest request) {
		Transaction transaction = transactionService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount(), request.getDescription());
		return ResponseEntity.ok(TransactionMapper.toResponse(transaction));		
	}

	// GET /api/transactions (LISTAR TODAS LAS TRANSACCIONES)
	@GetMapping
	public List<TransactionResponse> getAllTransactions(@org.springframework.web.bind.annotation.RequestParam(name = "order", defaultValue = "desc") String order) {
		return transactionService.getAllTransactions(order)
				.stream()
				.map(TransactionMapper::toResponse)
				.collect(Collectors.toList());
	}
		
	// POST /api/transactions/account/{accountId} (LISTAR TRANSACCIONES DE UNA CUENTA)
	@GetMapping("/account/{accountId}")
	public List<TransactionResponse> getTransactionsByAccount(@PathVariable Long accountId) {
		Account account = new Account();
		account.setId(accountId);
		return transactionService.getTransactionsByAccount(account)
				.stream()
				.map(TransactionMapper::toResponse)
				.collect(Collectors.toList());		
	}
	
	@PostMapping("/test/concurrent-deposits")
	public ResponseEntity<String> testConcurrentDeposits(@RequestBody CreateTransactionRequest request) {
	int numThreads = 10;
	BigDecimal amountPerDeposit = request.getAmount();
	Long accountId = request.getToAccountId();
	String description = request.getDescription();

		java.util.concurrent.atomic.AtomicReference<Integer> statusCode = new java.util.concurrent.atomic.AtomicReference<>(200);
		java.util.concurrent.atomic.AtomicReference<String> errorMessage = new java.util.concurrent.atomic.AtomicReference<>(null);

		List<Thread> threads = new java.util.ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread(() -> {
				try {
					transactionService.deposit(accountId, amountPerDeposit, description);
				} catch (org.springframework.web.server.ResponseStatusException ex) {
					// Guardar el primer error significativo
					if (statusCode.get() == 200) {
						statusCode.set(ex.getStatusCode().value());
						if (ex.getStatusCode().value() == 404) {
							errorMessage.set("ERROR: La cuenta no existe.");
						} else if (ex.getStatusCode().value() == 400) {
							errorMessage.set("ERROR: Solicitud inválida.");
						} else {
							errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
						}
					}
				} catch (RuntimeException ex) {
					if (statusCode.get() == 200) {
						statusCode.set(500);
						errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
					}
				}
			});
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		if (statusCode.get() != 200) {
			return ResponseEntity.status(statusCode.get()).body(errorMessage.get());
		}
		return ResponseEntity.ok("Concurrent deposits completed (" + numThreads + " threads)");
	} 
	
	@PostMapping("/test/concurrent-withdrawals")
	public ResponseEntity<String> testConcurrentWithdrawals(@RequestBody CreateTransactionRequest request) {
	int numThreads = 10;
	BigDecimal amountPerWithdraw = request.getAmount();
	Long accountId = request.getFromAccountId();
	String description = request.getDescription();

		java.util.concurrent.atomic.AtomicReference<Integer> statusCode = new java.util.concurrent.atomic.AtomicReference<>(200);
		java.util.concurrent.atomic.AtomicReference<String> errorMessage = new java.util.concurrent.atomic.AtomicReference<>(null);

		List<Thread> threads = new java.util.ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread(() -> {
				try {
					transactionService.withdraw(accountId, amountPerWithdraw, description);
				} catch (org.springframework.web.server.ResponseStatusException ex) {
					if (statusCode.get() == 200) {
						statusCode.set(ex.getStatusCode().value());
						if (ex.getStatusCode().value() == 404) {
							errorMessage.set("ERROR: La cuenta no existe.");
						} else if (ex.getStatusCode().value() == 400) {
							errorMessage.set("ERROR: Saldo insuficiente.");
						} else {
							errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
						}
					}
				} catch (RuntimeException ex) {
					if (statusCode.get() == 200) {
						statusCode.set(500);
						errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
					}
				}
			});
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		if (statusCode.get() != 200) {
			return ResponseEntity.status(statusCode.get()).body(errorMessage.get());
		}
		return ResponseEntity.ok("Concurrent withdrawals completed (" + numThreads + " threads)");
	}

	@PostMapping("/test/concurrent-transfers")
	public ResponseEntity<String> testConcurrentTransfers(@RequestBody CreateTransactionRequest request) {
	int numThreads = 10;
	BigDecimal amountPerTransfer = request.getAmount();
	Long fromAccountId = request.getFromAccountId();
	Long toAccountId = request.getToAccountId();
	String description = request.getDescription();

		java.util.concurrent.atomic.AtomicReference<Integer> statusCode = new java.util.concurrent.atomic.AtomicReference<>(200);
		java.util.concurrent.atomic.AtomicReference<String> errorMessage = new java.util.concurrent.atomic.AtomicReference<>(null);

		List<Thread> threads = new java.util.ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread(() -> {
				try {
					transactionService.transfer(fromAccountId, toAccountId, amountPerTransfer, description);
				} catch (org.springframework.web.server.ResponseStatusException ex) {
					if (statusCode.get() == 200) {
						statusCode.set(ex.getStatusCode().value());
						if (ex.getStatusCode().value() == 404) {
							errorMessage.set("ERROR: Cuenta origen o destino no existe.");
						} else if (ex.getStatusCode().value() == 400) {
							errorMessage.set("ERROR: Saldo insuficiente.");
						} else {
							errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
						}
					}
				} catch (RuntimeException ex) {
					if (statusCode.get() == 200) {
						statusCode.set(500);
						errorMessage.set("ERROR: Problema interno del servidor. Inténtalo de nuevo.");
					}
				}
			});
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		if (statusCode.get() != 200) {
			return ResponseEntity.status(statusCode.get()).body(errorMessage.get());
		}
		return ResponseEntity.ok("Concurrent transfers completed (" + numThreads + " threads)");
	} 
}
