package com.example.cqrs.command.domain.aggregate.member;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.example.cqrs.command.domain.aggregate.member.model.BankAccount;
import com.example.cqrs.shared.event.BankAccountCreatedEvent;
import com.example.cqrs.shared.event.PaymentEvent;

import org.axonframework.eventsourcing.EventSourcingHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
public class BankAccounts {

	private final List<BankAccount> items = new ArrayList<>();

	@EventSourcingHandler
	public void on(BankAccountCreatedEvent bankAccountCreatedEvent) {
		log.info("Handling event {}", bankAccountCreatedEvent);
		items.add(BankAccount.builder()
				.id(bankAccountCreatedEvent.getId())
				.balance(bankAccountCreatedEvent.getInitialDeposit())
				.overdraftLimit(bankAccountCreatedEvent.getOverdraftLimit())
				.build());
	}

	@EventSourcingHandler
	public void on(PaymentEvent paymentEvent) {
		log.info("Handling event {}", paymentEvent);
		items.stream()
				.filter(e -> e.getId().equals(paymentEvent.getAccountId()))
				.findFirst()
				.ifPresent(item -> item.setBalance(paymentEvent.getCalculatedBalance()));
	}
}