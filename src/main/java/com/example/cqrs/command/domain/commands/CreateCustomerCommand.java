package com.example.cqrs.command.domain.commands;

import lombok.Builder;
import lombok.Data;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateCustomerCommand {

	@TargetAggregateIdentifier
	private String id;
	private String name;
}
