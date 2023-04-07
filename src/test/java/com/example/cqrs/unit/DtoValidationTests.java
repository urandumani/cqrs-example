package com.example.cqrs.unit;

import static org.assertj.core.api.Assertions.assertThat;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import com.example.cqrs.command.api.dto.BankAccountDto;
import com.example.cqrs.command.api.dto.CustomerDto;
import com.example.cqrs.command.api.dto.PaymentDto;
import com.example.cqrs.shared.PaymentType;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

public class DtoValidationTests {

	private static Validator validator;

	@Before
	public void getExecutableValidator() {
		validator = Validation.buildDefaultValidatorFactory()
				.getValidator();
	}

	@Test
	public void whenCustomerEmptyName_thenConstraintViolation() {
		CustomerDto customer = CustomerDto.builder()
				.name("")
				.build();

		Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customer);

		assertThat(violations.size()).isEqualTo(1);
	}

	@Test
	public void whenCustomerNotEmptyName_thenNoConstraintViolations() {
		CustomerDto customer = CustomerDto.builder()
				.name("John Doe")
				.build();

		Set<ConstraintViolation<CustomerDto>> violations = validator.validate(customer);

		assertThat(violations.size()).isEqualTo(0);
	}

	@Test
	public void whenBankAccountNullFields_thenConstraintViolations() {
		BankAccountDto bankAccountDto = BankAccountDto.builder()
				.build();

		Set<ConstraintViolation<BankAccountDto>> violations = validator.validate(bankAccountDto);

		assertThat(violations.size()).isEqualTo(2);
	}

	@Test
	public void whenBankAccountNotNullFields_thenNoConstraintViolations() {
		BankAccountDto bankAccountDto = BankAccountDto.builder()
				.initialDeposit(BigDecimal.ZERO)
				.overdraftLimit(BigDecimal.TEN)
				.build();

		Set<ConstraintViolation<BankAccountDto>> violations = validator.validate(bankAccountDto);

		assertThat(violations.size()).isEqualTo(0);
	}

	@Test
	public void whenPaymentNullFields_thenConstraintViolations() {
		PaymentDto paymentDto = PaymentDto.builder()
				.build();

		Set<ConstraintViolation<PaymentDto>> violations = validator.validate(paymentDto);

		assertThat(violations.size()).isEqualTo(2);
	}

	@Test
	public void whenPaymentNotNullFields_thenNoConstraintViolations() {
		PaymentDto paymentDto = PaymentDto.builder()
				.paymentType(PaymentType.DEBIT)
				.amount(BigDecimal.TEN)
				.build();

		Set<ConstraintViolation<PaymentDto>> violations = validator.validate(paymentDto);

		assertThat(violations.size()).isEqualTo(0);
	}

}
