package com.example.cqrs.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.testcontainers.shaded.org.awaitility.Durations.FIVE_SECONDS;
import static org.testcontainers.shaded.org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;

import com.example.cqrs.command.api.dto.BankAccountDto;
import com.example.cqrs.command.api.dto.PaymentDto;
import com.example.cqrs.query.dto.CustomerDto;
import com.example.cqrs.query.model.Customer;
import com.example.cqrs.query.repository.CustomerQueryRepository;
import com.example.cqrs.shared.PaymentType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CustomerIntegrationTest extends IntegrationTestBase {

	private static final String GET_CUSTOMER_URL = "http://localhost:%s/api/customers/%s";
	private static final String POST_CUSTOMER_URL = "http://localhost:%s/api/customers";
	private static final String POST_BANK_ACCOUNT_URL = "http://localhost:%s/api/customers/%s/bankaccounts";
	private static final String GET_NEGATIVE_BANK_ACCOUNT_URL = "http://localhost:%s/api/customers/negative";
	private static final String POST_PAYMENT_URL = "http://localhost:%s/api/customers/%s/bankaccounts/%s/payment";

	@Autowired
	private CustomerQueryRepository queryRepository;

	@Test
	public void shouldSaveAndRetrieveCustomer() {
		String id;
		ResponseEntity<String> result = testRestTemplate.postForEntity(
				String.format(POST_CUSTOMER_URL, randomServerPort),
				CustomerDto.builder().name("John Doe").build(),
				String.class
		);

		id = result.getBody();

		await()
				.atMost(FIVE_SECONDS)
				.with()
				.pollInterval(ONE_HUNDRED_MILLISECONDS)
				.until(() -> queryRepository.findById(id).isPresent());

		ResponseEntity<CustomerDto> response = testRestTemplate
				.getForEntity(
						String.format(GET_CUSTOMER_URL, randomServerPort, id),
						CustomerDto.class);

		CustomerDto responseDto = response.getBody();

		assertEquals(response.getStatusCode().is2xxSuccessful(), true);
		assertNotNull(responseDto);
		assertEquals(responseDto.getName(), "John Doe");
		assertEquals(responseDto.getId(), id);
		assertEquals(responseDto.getAccounts().size(), 0);
	}

	@Test
	public void shouldSaveAndRetrieveBankAcount() {
		String id;
		ResponseEntity<String> result = testRestTemplate.postForEntity(
				String.format(POST_CUSTOMER_URL, randomServerPort),
				CustomerDto.builder().name("John Doe").build(),
				String.class
		);

		id = result.getBody();

		await()
				.atMost(FIVE_SECONDS)
				.with()
				.pollInterval(ONE_HUNDRED_MILLISECONDS)
				.until(() -> queryRepository.findById(id).isPresent());

		testRestTemplate.postForEntity(
				String.format(POST_BANK_ACCOUNT_URL, randomServerPort, id),
				BankAccountDto.builder()
						.overdraftLimit(BigDecimal.TEN)
						.initialDeposit(BigDecimal.TEN)
						.build(),
				String.class
		);

		ResponseEntity<CustomerDto> response = testRestTemplate
				.getForEntity(
						String.format(GET_CUSTOMER_URL, randomServerPort, id),
						CustomerDto.class);

		CustomerDto responseDto = response.getBody();

		assertEquals(response.getStatusCode().is2xxSuccessful(), true);
		assertNotNull(responseDto);
		assertEquals(responseDto.getName(), "John Doe");
		assertEquals(responseDto.getId(), id);
		assertEquals(responseDto.getAccounts().size(), 1);
		assertEquals(responseDto.getAccounts().get(0).getBalance(), BigDecimal.TEN);
	}

	@Test
	public void shouldSaveUpdateAndRetrieveNegativeBankAcount() {
		String id;
		ResponseEntity<String> result = testRestTemplate.postForEntity(
				String.format(POST_CUSTOMER_URL, randomServerPort),
				CustomerDto.builder().name("John Doe").build(),
				String.class
		);

		id = result.getBody();

		await()
				.atMost(FIVE_SECONDS)
				.with()
				.pollInterval(ONE_HUNDRED_MILLISECONDS)
				.until(() -> queryRepository.findById(id).isPresent());

		testRestTemplate.postForEntity(
				String.format(POST_BANK_ACCOUNT_URL, randomServerPort, id),
				BankAccountDto.builder()
						.overdraftLimit(BigDecimal.TEN)
						.initialDeposit(BigDecimal.TEN)
						.build(),
				String.class
		);

		ResponseEntity<CustomerDto> response = testRestTemplate
				.getForEntity(
						String.format(GET_CUSTOMER_URL, randomServerPort, id),
						CustomerDto.class);

		CustomerDto responseDto = response.getBody();

		assertEquals(response.getStatusCode().is2xxSuccessful(), true);
		assertNotNull(responseDto);
		assertEquals(responseDto.getName(), "John Doe");
		assertEquals(responseDto.getId(), id);
		assertEquals(responseDto.getAccounts().size(), 1);
		assertEquals(responseDto.getAccounts().get(0).getBalance(), BigDecimal.TEN);


		testRestTemplate.postForEntity(
				String.format(POST_PAYMENT_URL, randomServerPort, id, responseDto.getAccounts().get(0).getId()),
				PaymentDto.builder()
						.amount(BigDecimal.valueOf(15))
						.paymentType(PaymentType.DEBIT)
						.build(),
				String.class
		);

		ResponseEntity<CustomerDto[]> updatedNegativeResponse = testRestTemplate
				.getForEntity(
						String.format(GET_NEGATIVE_BANK_ACCOUNT_URL, randomServerPort),
						CustomerDto[].class);

		CustomerDto negativeResult = Arrays.stream(updatedNegativeResponse.getBody()).findFirst()
				.orElseThrow(() -> new RuntimeException("Failed to update"));

		assertEquals(updatedNegativeResponse.getStatusCode().is2xxSuccessful(), true);
		assertNotNull(negativeResult);
		assertEquals(negativeResult.getName(), "John Doe");
		assertEquals(negativeResult.getId(), id);
		assertEquals(negativeResult.getAccounts().size(), 1);
		assertEquals(negativeResult.getAccounts().get(0).getBalance(), BigDecimal.valueOf(-5));
	}
}
