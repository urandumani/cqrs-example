package com.example.cqrs.query.application;

import lombok.extern.slf4j.Slf4j;

import com.example.cqrs.query.model.BankAccount;
import com.example.cqrs.query.model.Customer;
import com.example.cqrs.query.repository.CustomerQueryRepository;
import com.example.cqrs.shared.event.BankAccountCreatedEvent;
import com.example.cqrs.shared.event.CustomerCreatedEvent;
import com.example.cqrs.shared.event.CustomerUpdatedEvent;
import com.example.cqrs.shared.event.PaymentEvent;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerEventHandler {

	private final CustomerQueryRepository customerQueryRepository;

	public CustomerEventHandler(CustomerQueryRepository customerQueryRepository) {
		this.customerQueryRepository = customerQueryRepository;
	}

	@EventHandler
	public void on(CustomerCreatedEvent event) {
		customerQueryRepository.save(Customer.builder()
				.id(event.getId())
				.name(event.getName())
				.build());
	}

	@EventHandler
	public void on(CustomerUpdatedEvent event) {
		customerQueryRepository.save(Customer.builder()
				.id(event.getId())
				.name(event.getName())
				.build());
	}

	@EventHandler
	public void on(BankAccountCreatedEvent event) {
		customerQueryRepository.findById(event.getCustomerId())
				.ifPresentOrElse(order -> {
					order.getBankAccounts().putIfAbsent(event.getId(), BankAccount.builder()
							.balance(event.getInitialDeposit())
							.overdraftLimit(event.getOverdraftLimit())
							.build());
					customerQueryRepository.save(order);
					},
					() -> log.warn("Won't create BankAccount. Customer is not present: {}", event.getCustomerId()));
	}

	@EventHandler
	public void on(PaymentEvent event) {
		customerQueryRepository.findById(event.getCustomerId())
				.ifPresentOrElse(order -> {
							var bankAccount = order.getBankAccounts().get(event.getAccountId());
							bankAccount.setBalance(event.getAmount());
							customerQueryRepository.save(order);
						},
						() -> log.warn("Won't create BankAccount. Customer is not present: {}", event.getCustomerId()));
	}
}