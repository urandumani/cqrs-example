package com.example.cqrs.command.domain.commands;

import lombok.Builder;
import lombok.Data;

import com.example.cqrs.shared.PaymentType;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentCommand {

	private String accountId;
	@TargetAggregateIdentifier
	private String customerId;
	private BigDecimal amount;
	private PaymentType paymentType;
}
