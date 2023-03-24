package com.example.cqrs.shared.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerUpdatedEvent {

	private String id;
	private String name;
}
