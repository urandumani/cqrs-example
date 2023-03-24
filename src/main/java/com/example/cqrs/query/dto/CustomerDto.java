package com.example.cqrs.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

	private String id;
	private String name;
	private List<BankAccountDto> accounts = new ArrayList<>();
}
