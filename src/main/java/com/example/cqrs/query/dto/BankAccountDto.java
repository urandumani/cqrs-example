package com.example.cqrs.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDto {

	private String id;
	private BigDecimal balance;
	private BigDecimal overdraftLimit;
}
