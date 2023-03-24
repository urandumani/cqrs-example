package com.example.cqrs.command.api;

import lombok.extern.slf4j.Slf4j;

import com.example.cqrs.command.api.dto.BankAccountDto;
import com.example.cqrs.command.api.dto.CustomerDto;
import com.example.cqrs.command.api.dto.PaymentDto;
import com.example.cqrs.command.domain.commands.CreateBankAccountCommand;
import com.example.cqrs.command.domain.commands.CreateCustomerCommand;
import com.example.cqrs.command.domain.commands.PaymentCommand;
import com.example.cqrs.command.domain.commands.UpdateCustomerCommand;
import com.example.cqrs.query.repository.CustomerQueryRepository;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CustomerCommandService {

	private final CommandGateway commandGateway;
	private final CustomerQueryRepository repository;

	public CustomerCommandService(CommandGateway commandGateway, EventGateway eventGateway,
			CustomerQueryRepository repository) {
		this.commandGateway = commandGateway;
		this.repository = repository;
	}

	public ResponseEntity<String> createCustomer(CustomerDto dto) {
		var result = commandGateway.sendAndWait(createCommandFrom(dto));
		return ResponseEntity.ok((String) result);
	}

	public ResponseEntity<String> updateCustomer(String aggregateId, CustomerDto dto) {
		repository.findById(aggregateId)
				.orElseThrow(() -> new AggregateNotFoundException(aggregateId, "Aggregate not found"));
		var result = commandGateway.sendAndWait(updateCommandFrom(dto, aggregateId));
		return ResponseEntity.ok((String) result);
	}

	public ResponseEntity<String> createBankAccount(String aggregateId, BankAccountDto dto) {
		repository.findById(aggregateId)
				.orElseThrow(() -> new AggregateNotFoundException("Aggregate with id {} not found", aggregateId));
		var result = commandGateway.sendAndWait(updateCommandFrom(dto, aggregateId));
		return ResponseEntity.ok((String) result);
	}

	public ResponseEntity<String> createPayment(String aggregateId, String accountId, PaymentDto dto) {
		repository.findById(aggregateId)
				.orElseThrow(() -> new AggregateNotFoundException("Aggregate with id {} not found", aggregateId));
		var result = commandGateway.sendAndWait(paymentCommandFrom(dto, aggregateId, accountId));
		return ResponseEntity.ok((String) result);
	}

	private static CreateCustomerCommand createCommandFrom(CustomerDto customerDto) {
		return CreateCustomerCommand.builder()
				.id(UUID.randomUUID().toString())
				.name(customerDto.getName())
				.build();
	}

	private static UpdateCustomerCommand updateCommandFrom(CustomerDto customerDto, String aggregateId) {
		return UpdateCustomerCommand.builder()
				.id(aggregateId)
				.name(customerDto.getName())
				.build();
	}

	private static CreateBankAccountCommand updateCommandFrom(BankAccountDto dto, String aggregateId) {
		return CreateBankAccountCommand.builder()
				.id(UUID.randomUUID().toString())
				.customerId(aggregateId)
				.deposit(dto.getInitialDeposit())
				.overdraftLimit(dto.getOverdraftLimit())
				.build();
	}

	private static PaymentCommand paymentCommandFrom(PaymentDto dto, String aggregateId, String accountId) {
		return PaymentCommand.builder()
				.accountId(accountId)
				.customerId(aggregateId)
				.amount(dto.getAmount())
				.paymentType(dto.getPaymentType())
				.build();
	}
}
