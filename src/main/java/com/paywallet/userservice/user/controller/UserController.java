package com.paywallet.userservice.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.paywallet.userservice.user.constant.AppConstants.BASE_PATH;
import static com.paywallet.userservice.user.constant.AppConstants.CREATE_USER;

import java.util.List;

import com.paywallet.userservice.user.entities.CreateUserRequest;
import com.paywallet.userservice.user.entities.UpdateUserRequest;
import com.paywallet.userservice.user.entities.User;
import com.paywallet.userservice.user.exception.MissingParametersException;
import com.paywallet.userservice.user.services.UserService;
import com.paywallet.userservice.user.util.UserValidatiorUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(BASE_PATH)
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping(CREATE_USER)
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
		log.info("User details are" + user);
		if (!UserValidatiorUtil.validateUserRequest(user))
			return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
		else
			throw new MissingParametersException("Requried parameters are missing");
	}

	@GetMapping("/ssn/{ssn}")
	public ResponseEntity<?> findUserBySsn(@PathVariable("ssn") String ssn) {
		log.info("find user by ssn");
		return ResponseEntity.status(HttpStatus.OK).body(userService.findBySsn(ssn));
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getUserDetails(@PathVariable("id") String id) {
		log.info("find user by Id");
		return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findByEmail(email));
	}

	@GetMapping()
	public List<User> getUsers() {
		log.info("getting all user");
		return userService.getUsers();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserDetails(@RequestBody UpdateUserRequest usrRequest) {
		log.info("update user details..");
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.updateUserDetails(usrRequest));
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable String id) {

		log.info("delete the user :" + id);
		userService.deleteUser(id);
	}
}
