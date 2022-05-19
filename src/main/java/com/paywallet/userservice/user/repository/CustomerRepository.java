package com.paywallet.userservice.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.paywallet.userservice.user.entities.CustomerDetails;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerDetails, String> {

    Optional<CustomerDetails> findByCustomerId(String customerId);
    Optional<CustomerDetails> findByPersonalProfileCellPhone(String cellPhone);
    Optional<CustomerDetails> findByPersonalProfileEmailId(String emailId);
    Optional<CustomerDetails> findByExternalAccountAndExternalAccountABAAndPersonalProfileCellPhone(String externalAccount,String externalAccountABA,String cellPhone);
}
