package com.example.cqrs.unit;

import com.example.cqrs.command.domain.aggregate.CustomerAggregate;
import com.example.cqrs.command.domain.commands.CreateBankAccountCommand;
import com.example.cqrs.command.domain.commands.CreateCustomerCommand;
import com.example.cqrs.command.domain.commands.PaymentCommand;
import com.example.cqrs.command.domain.commands.UpdateCustomerCommand;
import com.example.cqrs.shared.PaymentType;
import com.example.cqrs.shared.event.BankAccountCreatedEvent;
import com.example.cqrs.shared.event.CustomerCreatedEvent;
import com.example.cqrs.shared.event.CustomerUpdatedEvent;
import com.example.cqrs.shared.event.PaymentEvent;

import org.axonframework.modelling.command.AggregateNotFoundException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerAggregateTest {

	private FixtureConfiguration<CustomerAggregate> fixture;

	@Before
	public void setUp() {
		fixture = new AggregateTestFixture<>(CustomerAggregate.class);
	}

	@Test
	public void whenCreateCustomerCommand_thenCustomerCreatedEvent() {
		String id = UUID.randomUUID().toString();
		fixture.given()
				.when(CreateCustomerCommand.builder()
						.id(id)
						.name("John Doe")
						.build())
				.expectSuccessfulHandlerExecution()
				.expectEvents(CustomerCreatedEvent.builder()
						.id(id)
						.name("John Doe")
						.build());
	}

	@Test
	public void whenUpdateCustomerCommand_thenCustomerUpdatedEvent() {
		String id = UUID.randomUUID().toString();
		fixture.given(CustomerCreatedEvent.builder()
						.id(id)
						.name("John Doe")
						.build())
				.when(UpdateCustomerCommand.builder()
						.id(id)
						.name("Updated Name")
						.build())
				.expectSuccessfulHandlerExecution()
				.expectEvents(CustomerUpdatedEvent.builder()
						.id(id)
						.name("Updated Name")
						.build());
	}

	@Test
	public void whenUpdateCustomerCommand_thenAggregateNotFoundException() {
		String id = UUID.randomUUID().toString();
		fixture.given()
				.when(UpdateCustomerCommand.builder()
						.id(id)
						.name("Updated Name")
						.build())
				.expectException(AggregateNotFoundException.class);
	}

	@Test
	public void whenCreateBankAccountCommand_thenAccountCreatedEvent() {
		String id = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		fixture.given(CustomerCreatedEvent.builder()
						.id(customerId)
						.name("John Doe")
						.build())
				.when(CreateBankAccountCommand.builder()
						.id(id)
						.customerId(customerId)
						.overdraftLimit(BigDecimal.TEN)
						.deposit(BigDecimal.TEN)
						.build())
				.expectSuccessfulHandlerExecution()
				.expectEvents(BankAccountCreatedEvent.builder()
						.id(id)
						.customerId(customerId)
						.overdraftLimit(BigDecimal.TEN)
						.initialDeposit(BigDecimal.TEN)
						.build());
	}

	@Test
	public void whenCreatePaymentCommand_thenPaymentEvent() {
		String accountId = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		fixture.given(CustomerCreatedEvent.builder()
						.id(customerId)
						.name("John Doe")
						.build())
				.andGiven(BankAccountCreatedEvent.builder()
						.id(accountId)
						.customerId(customerId)
						.initialDeposit(BigDecimal.TEN)
						.overdraftLimit(BigDecimal.TEN)
						.build())
				.when(PaymentCommand.builder()
						.accountId(accountId)
						.customerId(customerId)
						.paymentType(PaymentType.DEBIT)
						.amount(BigDecimal.TEN)
						.build())
				.expectSuccessfulHandlerExecution()
				.expectEvents(PaymentEvent.builder()
						.accountId(accountId)
						.customerId(customerId)
						.amount(BigDecimal.TEN)
						.calculatedBalance(BigDecimal.ZERO)
						.paymentType(PaymentType.DEBIT)
						.date(LocalDate.now())
						.build());
	}

	@Test
	public void whenCreatePaymentCommand_thenIllegalArgumentException() {
		String accountId = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		fixture.given(CustomerCreatedEvent.builder()
						.id(customerId)
						.name("John Doe")
						.build())
				.andGiven(BankAccountCreatedEvent.builder()
						.id(accountId)
						.customerId(customerId)
						.initialDeposit(BigDecimal.TEN)
						.overdraftLimit(BigDecimal.TEN)
						.build())
				.when(PaymentCommand.builder()
						.accountId(accountId)
						.customerId(customerId)
						.paymentType(PaymentType.DEBIT)
						.amount(BigDecimal.valueOf(2000))
						.build())
				.expectException(IllegalArgumentException.class);
	}

	@Test
	public void whenCreatePaymentCommand_thenAggregateNotFoundException() {
		String accountId = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		fixture.given(CustomerCreatedEvent.builder()
						.id(customerId)
						.name("John Doe")
						.build())
				.when(PaymentCommand.builder()
						.accountId(accountId)
						.customerId(customerId)
						.paymentType(PaymentType.DEBIT)
						.amount(BigDecimal.TEN)
						.build())
				.expectException(AggregateNotFoundException.class);
	}

}
