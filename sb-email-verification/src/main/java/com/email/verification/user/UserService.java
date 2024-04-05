package com.email.verification.user;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.email.verification.exception.UserAlreadyExistsException;
import com.email.verification.user.registration.RegistrationRequest;
import com.email.verification.user.registration.token.VerificationToken;
import com.email.verification.user.registration.token.VerificationTokenRepository;

import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationTokenRepository verificationTokenRepository;

	@Override
	public List<User> getUsers() {
		
		return userRepository.findAll();
	}

	@Override
	public User registerUser(RegistrationRequest request) {

		Optional<User> user = this.findByEmail(request.email());
		if (user.isPresent()) {
			throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
		}
		var newUser = new User();
		newUser.setFirstName(request.firstName());
		newUser.setLastName(request.lastName());
		newUser.setEmail(request.email());
		newUser.setPassword(passwordEncoder.encode(request.password()));
		newUser.setRole("USER");

		return userRepository.save(newUser);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	public void saveUserVerificationToken(User registeredUser, String verificationToken) {
		
		VerificationToken userVerificationToken = new VerificationToken(verificationToken, registeredUser);
		
		verificationTokenRepository.save(userVerificationToken);
		
	}

	public String validateToken(String token) {
		VerificationToken tokenVerification = verificationTokenRepository.findByToken(token);
		if(tokenVerification == null) {
			return "Invalid verification token";
		}
		User user = tokenVerification.getUser();
		Calendar calendar = Calendar.getInstance();
		if((tokenVerification.getExpirationTime().getTime()- calendar.getTime().getTime()) <=0) {
			verificationTokenRepository.delete(tokenVerification);
			return "Token already expired";
		}
		
		user.setEnabled(true);
		userRepository.save(user);
		return "Valid";
	}

	@Override
	public void removeSessionMessage() {
		HttpSession session =((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		session.removeAttribute("msg");
	}
	
	

}
