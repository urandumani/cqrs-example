package com.example.cqrs.command.domain.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.cqrs.command.domain.aggregate.member.BankAccounts;
import com.example.cqrs.command.domain.aggregate.member.model.BankAccount;
import com.example.cqrs.command.domain.commands.CreateBankAccountCommand;
import com.example.cqrs.command.domain.commands.CreateCustomerCommand;
import com.example.cqrs.command.domain.commands.PaymentCommand;
import com.example.cqrs.command.domain.commands.UpdateCustomerCommand;
import com.example.cqrs.shared.PaymentType;
import com.example.cqrs.shared.event.BankAccountCreatedEvent;
import com.example.cqrs.shared.event.CustomerCreatedEvent;
import com.example.cqrs.shared.event.CustomerUpdatedEvent;
import com.example.cqrs.shared.event.PaymentEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Aggregate
@NoArgsConstructor
public class CustomerAggregate {

	@AggregateIdentifier
	private String id;

	@AggregateMember
	private BankAccounts bankAccounts = BankAccounts.builder().build();

	private String name;

	@CommandHandler
	public CustomerAggregate(CreateCustomerCommand command) {
		apply(CustomerCreatedEvent.builder()
				.id(command.getId())
				.name(command.getName())
				.build());
	}

	@EventSourcingHandler
	public void on(CustomerCreatedEvent customerCreatedEvent) {
		log.info("Handling event {}", customerCreatedEvent);
		this.id = customerCreatedEvent.getId();
		this.name = customerCreatedEvent.getName();
	}

	@CommandHandler
	public void handle(UpdateCustomerCommand command) {
		apply(CustomerUpdatedEvent.builder()
				.name(command.getName())
				.id(command.getId())
				.build());
	}

	@EventSourcingHandler
	public void on(CustomerUpdatedEvent customerUpdatedEvent) {
		log.info("Handling event {}", customerUpdatedEvent);
		this.name = customerUpdatedEvent.getName();
	}

	@CommandHandler
	public void handle(CreateBankAccountCommand command) {
		log.info("handling CreateBankAccountCommand");
		apply(BankAccountCreatedEvent.builder()
				.id(command.getId())
				.customerId(command.getCustomerId())
				.initialDeposit(command.getDeposit())
				.overdraftLimit(command.getOverdraftLimit())
				.build());
	}

	@CommandHandler
	public void on(PaymentCommand command) {
		log.info("handling PaymentCommand");
		BankAccount account = findItem(command.getAccountId()).orElseThrow(
				() -> new AggregateNotFoundException(command.getAccountId(), "Cannot find BankAccount"));
		BigDecimal calculatedBalance = command.getPaymentType().equals(PaymentType.DEBIT) ?
				calculateBalance(account.getBalance(), command.getAmount(), account.getOverdraftLimit()) :
				calculateBalance(account.getBalance(), command.getAmount());
		apply(PaymentEvent.builder()
				.accountId(command.getAccountId())
				.customerId(command.getCustomerId())
				.amount(calculatedBalance)
				.paymentType(command.getPaymentType())
				.build());
	}

	@JsonIgnore
	public Optional<BankAccount> findItem(String id) {
		if (StringUtils.hasText(id)) {
			return bankAccounts.getItems().stream().filter(e -> e.getId().equals(id)).findFirst();
		}
		return Optional.empty();
	}

	private BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount, BigDecimal overdraftLimit) {
		var pendingBalance = balance.subtract(amount);
		if(pendingBalance.compareTo(BigDecimal.ZERO) < 0 && pendingBalance.compareTo(overdraftLimit.negate()) < 0) {
			throw new IllegalArgumentException("Requested amount is greater than the overdraft limit");
		}
		return pendingBalance;
	}

	private BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount) {
		return balance.add(amount);
	}
}
