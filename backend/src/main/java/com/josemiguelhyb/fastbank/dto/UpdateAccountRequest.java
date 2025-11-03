package com.josemiguelhyb.fastbank.dto;

import java.math.BigDecimal;

public class UpdateAccountRequest {
    private String accountNumber; // opcional
    private BigDecimal balance;    // opcional (>= 0)
    private Long userId;           // opcional (re-asignar)

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
