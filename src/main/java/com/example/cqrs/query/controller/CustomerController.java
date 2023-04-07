package com.example.cqrs.query.controller;

import com.example.cqrs.query.dto.CustomerDto;
import com.example.cqrs.query.dto.TransactionDto;
import com.example.cqrs.query.service.CustomerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("{customerId}")
	public CustomerDto findCustomer(@PathVariable String customerId) {
		return customerService.findCustomer(customerId);
	}

	@GetMapping("/negative")
	public List<CustomerDto> findCustomersWithNegativeBalances() {
		return customerService.findCustomersWithNegativeBalances();
	}

	@GetMapping("{customerId}/bankaccounts/{bankAccountId}/transactions")
	public List<TransactionDto> findTransactionsByDate(
			@PathVariable String customerId,
			@PathVariable String bankAccountId,
			@RequestParam LocalDate from) {
		return customerService.findTransactionsByDate(customerId, bankAccountId, from);
	}
}
