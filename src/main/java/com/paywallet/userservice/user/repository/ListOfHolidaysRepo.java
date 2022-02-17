package com.paywallet.userservice.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.paywallet.userservice.user.entities.HolidaysList;

public interface ListOfHolidaysRepo extends MongoRepository<HolidaysList, String> {

}
