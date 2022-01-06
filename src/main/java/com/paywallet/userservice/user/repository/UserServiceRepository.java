package com.paywallet.userservice.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.paywallet.userservice.user.entities.User;


@Repository
public interface UserServiceRepository extends MongoRepository<User, String> {

	Optional<User> findBySsn(String ssn);

	List<User> findByEmail(String email);
}
