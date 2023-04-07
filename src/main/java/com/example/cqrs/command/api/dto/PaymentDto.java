package com.example.cqrs.command.api.dto;

import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "Amount cannot be null")
	private BigDecimal amount;
	@NotNull(message = "Payment type cannot be null")
	private PaymentType paymentType;
}
