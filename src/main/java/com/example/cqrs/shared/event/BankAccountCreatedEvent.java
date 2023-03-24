package com.example.cqrs.shared.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BankAccountCreatedEvent {
	private String id;
	private String customerId;
	private BigDecimal initialDeposit;
	private BigDecimal overdraftLimit;
}
