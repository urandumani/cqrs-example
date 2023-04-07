package com.example.cqrs.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

	private String id;
	private BigDecimal balance;
	private BigDecimal overdraftLimit;
	private List<Transaction> transactions = new ArrayList<>();
}
