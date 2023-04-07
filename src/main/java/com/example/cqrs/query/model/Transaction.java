package com.example.cqrs.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.cqrs.shared.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	private BigDecimal amount;
	private PaymentType paymentType;
	private LocalDate date;
}
