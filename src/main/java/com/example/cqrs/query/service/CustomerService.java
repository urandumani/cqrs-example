package com.example.cqrs.query.service;

import com.example.cqrs.query.dto.BankAccountDto;
import com.example.cqrs.query.dto.CustomerDto;
import com.example.cqrs.query.repository.CustomerQueryRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

	private final CustomerQueryRepository repository;

	public CustomerService(CustomerQueryRepository repository) {

		this.repository = repository;
	}

	public CustomerDto findCustomer(String customerId) {

		return repository.findById(customerId)
				.map(entity -> CustomerDto.builder()
						.id(entity.getId())
						.name(entity.getName())
						.accounts(entity.getBankAccounts().values().stream()
								.map(account -> BankAccountDto.builder()
										.id(account.getId())
										.balance(account.getBalance())
										.overdraftLimit(account.getOverdraftLimit())
										.build())
								.collect(Collectors.toList()))
						.build())
				.orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	public List<CustomerDto> findCustomersWithNegativeBalances() {

		return repository.findAll().stream()
				.filter(entity -> entity.getBankAccounts().values().stream()
						.anyMatch(account -> account.getBalance().compareTo(BigDecimal.ZERO) < 0))
				.map(entity -> CustomerDto.builder()
						.id(entity.getId())
						.name(entity.getName())
						.accounts(entity.getBankAccounts().values().stream()
								.filter(account -> account.getBalance().compareTo(BigDecimal.ZERO) < 0)
								.map(account -> BankAccountDto.builder()
										.id(account.getId())
										.balance(account.getBalance())
										.overdraftLimit(account.getOverdraftLimit())
										.build())
								.collect(Collectors.toList()))
						.build())
				.collect(Collectors.toList());
	}
}
