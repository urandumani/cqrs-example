package com.example.cqrs.command.api.dto;

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
	private BigDecimal initialDeposit;
	private BigDecimal overdraftLimit;
}
