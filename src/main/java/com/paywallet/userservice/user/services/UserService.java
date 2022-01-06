package com.paywallet.userservice.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.paywallet.userservice.user.entities.CreateUserRequest;
import com.paywallet.userservice.user.entities.UpdateUserRequest;
import com.paywallet.userservice.user.entities.User;
import com.paywallet.userservice.user.exception.CustomerNotFoundException;
import com.paywallet.userservice.user.exception.UserAlreadyPresentException;
import com.paywallet.userservice.user.repository.UserServiceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	UserServiceRepository userServiceRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Environment environment;

	@Autowired
	MongoOperations mongoOperation ;

	public Object createUser(CreateUserRequest user) {
		User createdUser = new User();
		Optional<User> existingUserData = userServiceRepository.findBySsn(user.getSsn());
		try {
			if (!existingUserData.isPresent()) {
				log.info("creating new user");
				createdUser = userServiceRepository.insert(createUserRequest(user));
			} else {
				log.error("User already present");
				throw new UserAlreadyPresentException("User already present.. please update the existing user");
			}
		} catch (Exception e) {
			log.error("Exception occured for insert user" + e.getMessage());
		}
		return createdUser;
	}

	public User createUserRequest(CreateUserRequest user) {
		User userDetails = new User();
		List<String> employerList = new ArrayList<>();
		employerList.add(user.getEmployers());
		userDetails.setAddress(user.getAddress());
		userDetails.setCommunicationMode(user.getCommunicationMode());
		userDetails.setEmail(user.getEmail());
		userDetails.setEmployers(employerList);
		userDetails.setFirstName(user.getFirstName());
		userDetails.setLastName(user.getLastName());
		userDetails.setMiddleName(user.getMiddleName());
		userDetails.setPhoneNumber(user.getPhoneNumber());
		userDetails.setSsn(user.getSsn());
		userDetails.setCreatedAt(new Date().toInstant().toString());
		userDetails.setUpdateAt(new Date().toInstant().toString());
		userDetails.setPaswword(user.getPaswword());
		userDetails.setRole(user.getRole());
		return userDetails;
	}

	public User findBySsn(String ssn) {
		Optional<User> userDetails = userServiceRepository.findBySsn(ssn);
		if (userDetails.isPresent() && ssn.equals(userDetails.get().getSsn())) {
			log.info("user with given ssn available" + ssn);
			return userDetails.get();
		} else
			throw new CustomerNotFoundException("No user found with given Ssn");
	}

	public User findById(String id) {
		Optional<User> userDetails = userServiceRepository.findById(id);
		if (userDetails.isPresent() && id.equals(userDetails.get().getId())) {
			log.info("User eists");
			return userDetails.get();
		} else
			throw new CustomerNotFoundException("No user found with given id");
	}

	public void deleteUser(String userId) {
		log.info("deleting user" + userId);
		Optional<User> userDetails = userServiceRepository.findById(userId);
		if (userDetails.isPresent())
			userServiceRepository.deleteById(userDetails.get().getId());
		else
			throw new CustomerNotFoundException("No user found with given ID");
	}

	public Object updateUserDetails(UpdateUserRequest usrRequest) {
		Optional<User> userDetails = userServiceRepository.findById(usrRequest.getId());
		return userServiceRepository.save(updateUserDetails(userDetails, usrRequest));
	}

	private User updateUserDetails(Optional<User> userDetails, UpdateUserRequest usrRequest) {
		User user = userDetails.get();
		List<String> employerList = new ArrayList<>();
		employerList.add(usrRequest.getEmployers());
		user.setAddress(usrRequest.getAddress());
		user.setCommunicationMode(usrRequest.getCommunicationMode());
		user.setEmail(usrRequest.getEmail());
		user.setFirstName(usrRequest.getFirstName());
		user.setLastName(usrRequest.getLastName());
		user.setMiddleName(usrRequest.getMiddleName());
		user.setEmployers(employerList);
		user.setSsn(usrRequest.getSsn());
		user.setRole(usrRequest.getRole());
		user.setPhoneNumber(usrRequest.getPhoneNumber());
		user.setPaswword(usrRequest.getPaswword());
		user.setUpdateAt(new Date().toInstant().toString());
		return user;
	}

	public List<User> getUsers() {
		return userServiceRepository.findAll();
	}

	public List<User> findByEmail(String email) {
		List<User> userDetails=userServiceRepository.findByEmail(email);
		log.info("user deatils are:" + userDetails);
		if (!userDetails.isEmpty())
			return userDetails;
		else
			throw new CustomerNotFoundException("No user found with given email");
	}

}
