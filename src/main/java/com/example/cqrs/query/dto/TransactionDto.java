package com.example.cqrs.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.cqrs.shared.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

	private BigDecimal amount;
	private PaymentType paymentType;
	private LocalDate date;
}
