package com.example.cqrs.command.domain.commands;

import lombok.Builder;
import lombok.Data;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
public class CreateBankAccountCommand {

	private String id;
	@TargetAggregateIdentifier
	private String customerId;
	private BigDecimal deposit;
	private BigDecimal overdraftLimit;
}
