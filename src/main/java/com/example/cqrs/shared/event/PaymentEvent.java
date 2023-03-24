package com.example.cqrs.shared.event;

import lombok.Builder;
import lombok.Data;

import com.example.cqrs.shared.PaymentType;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentEvent {

	private String accountId;
	private String customerId;
	private BigDecimal amount;
	private PaymentType paymentType;
}
