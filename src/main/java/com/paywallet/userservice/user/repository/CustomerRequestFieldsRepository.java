package com.paywallet.userservice.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.paywallet.userservice.user.model.CustomerRequestFields;

@Repository
public interface CustomerRequestFieldsRepository extends MongoRepository<CustomerRequestFields, String> {

	Optional<CustomerRequestFields> findByEmployer(String employer);
	
}
