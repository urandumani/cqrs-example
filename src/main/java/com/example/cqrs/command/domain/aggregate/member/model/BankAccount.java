package com.example.cqrs.command.domain.aggregate.member.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BankAccount {

	private String id;
	private BigDecimal balance;
	private BigDecimal overdraftLimit;
}
