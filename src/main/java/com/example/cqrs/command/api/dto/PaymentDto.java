package com.example.cqrs.command.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.cqrs.shared.PaymentType;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

	private BigDecimal amount;
	private PaymentType paymentType;
}
