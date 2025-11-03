package com.josemiguelhyb.fastbank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
	private Long id;
	private String type;
	private BigDecimal amount;
	private Long accountId; // Solo una cuenta asociada
	private LocalDateTime createdAt;
	private String description; // Motivo opcional
	
	// Getters y setters (NOSE SI SOLO GETTERS)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
