package com.email.verification.user;

import java.util.List;
import java.util.Optional;

import com.email.verification.user.registration.RegistrationRequest;

public interface IUserService {
	
	//Get all users
	List<User> getUsers();
	
	//New user registration
	User registerUser(RegistrationRequest request);
	
	//Find users by email
	Optional<User> findByEmail(String Email);
	
	public void removeSessionMessage();

}
