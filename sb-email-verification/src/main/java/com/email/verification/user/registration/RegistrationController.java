package com.email.verification.user.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.email.verification.event.RegistrationCmpltEvents;
import com.email.verification.user.User;
import com.email.verification.user.UserService;
import com.email.verification.user.registration.token.VerificationToken;
import com.email.verification.user.registration.token.VerificationTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/register")
public class RegistrationController {
	
	private final UserService userService;
	private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;
    
    
	@GetMapping("/signup")
	public String register() {
		return "register";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@PostMapping("/saveUser")
	@ResponseBody
	public String registerUser(@Valid @ModelAttribute RegistrationRequest registrationRequest, final HttpServletRequest request, HttpSession session) throws Exception {
		
		User user= userService.registerUser(registrationRequest);
		
		if(user!=null) {
//			System.out.println("save success");
			session.setAttribute("msg", "Register Successfully");
		}else {
//			throw new Exception("User is null");
			session.setAttribute("msg", "Something went wrong, Please try again!");
		}
		
		//publish registration event
		
		publisher.publishEvent(new RegistrationCmpltEvents(user, applicationUrl(request)));
		
		return "Success! Please, check your email to verify your email";
		
	}

	@RequestMapping( method = RequestMethod.GET, path = "/verifyEmail")
	public String verifyEmail(@RequestParam("token") String token) {
		VerificationToken verifiedToken = verificationTokenRepository.findByToken(token);
		if(verifiedToken.getUser().isEnabled()) {
			return "This account has already been verified, please login!";
		}
		String verificationResult = userService.validateToken(token);
		if(verificationResult.equalsIgnoreCase("valid")) {
			return "Email verified successfully. Now you can login to your account";
		}else {
			return "Invalid verification token";	
		}
			
	}
	
	public String applicationUrl(HttpServletRequest request) {
		
		return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
	}
	
}
