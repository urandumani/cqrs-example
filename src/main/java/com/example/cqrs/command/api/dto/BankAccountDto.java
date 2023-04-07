package com.example.cqrs.command.api.dto;

import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "Initial deposit cannot be null")
	private BigDecimal initialDeposit;
	@NotNull(message = "Overdraft limit cannot be null")
	private BigDecimal overdraftLimit;
}
