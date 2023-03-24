package com.example.cqrs.command.api;

import com.example.cqrs.command.api.dto.BankAccountDto;
import com.example.cqrs.command.api.dto.CustomerDto;
import com.example.cqrs.command.api.dto.PaymentDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerCommandController {

	private final CustomerCommandService customerCommandService;

	public CustomerCommandController(CustomerCommandService customerCommandService) {
		this.customerCommandService = customerCommandService;
	}

	@PostMapping
	public ResponseEntity<String> createRegistrationOrder(@RequestBody CustomerDto dto) {
		return customerCommandService.createCustomer(dto);
	}

	@PutMapping("/{aggregateId}")
	public ResponseEntity<String> updateRegistrationOrder(@PathVariable String aggregateId,
			@RequestBody CustomerDto dto) {
		return customerCommandService.updateCustomer(aggregateId, dto);
	}

	@PostMapping("/{aggregateId}/bankaccounts")
	public ResponseEntity<String> createBankAccount(@PathVariable String aggregateId, @RequestBody BankAccountDto dto) {
		return customerCommandService.createBankAccount(aggregateId, dto);
	}

	@PostMapping("/{aggregateId}/bankaccounts/{accountId}/payment")
	public ResponseEntity<String> createPayment(@PathVariable String aggregateId,
			@PathVariable String accountId,
			@RequestBody PaymentDto dto) {
		return customerCommandService.createPayment(aggregateId, accountId, dto);
	}
}
