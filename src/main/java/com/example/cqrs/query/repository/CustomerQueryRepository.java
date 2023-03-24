package com.example.cqrs.query.repository;

import com.example.cqrs.query.model.Customer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerQueryRepository extends MongoRepository<Customer, String> {

}
