package com.example.cqrs.shared.event;

import lombok.Builder;
import lombok.Data;

import com.example.cqrs.shared.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PaymentEvent {

	private String accountId;
	private String customerId;
	private BigDecimal amount;
	private BigDecimal calculatedBalance;
	private PaymentType paymentType;
	private LocalDate date;
}
