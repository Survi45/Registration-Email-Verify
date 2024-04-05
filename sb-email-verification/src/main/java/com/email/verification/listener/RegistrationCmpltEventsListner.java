package com.email.verification.listener;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.email.verification.event.RegistrationCmpltEvents;
import com.email.verification.user.User;
import com.email.verification.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCmpltEventsListner implements ApplicationListener<RegistrationCmpltEvents>{
	
	private final UserService userService;
	private final JavaMailSender mailSender;
	private User registeredUser;

	@Override
	public void onApplicationEvent(RegistrationCmpltEvents event) {
		
	   // 1. Get the newly registered user
		
		registeredUser= event.getUser();
		
	   // 2. Create a verification token for the user
		
		String verificationToken = UUID.randomUUID().toString();
		
	   // 3. Save the verification token for the user
		
		userService.saveUserVerificationToken(registeredUser, verificationToken);
		
	   // 4. Build the verification url to be sent  to the user
		
		String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
		
	  //  5. Send the email
		
		try {
			sendVerificationEmail(url);
		} catch (UnsupportedEncodingException | MessagingException e) {
			e.printStackTrace();
		}
		
		log.info("Click the link to verify your email: {}", url);
		 
		
	}
	
	public void sendVerificationEmail(String url) throws UnsupportedEncodingException, MessagingException {
		
		String subject = "Email Verification";
		String senderName = "User Registration Portal Service";
		String mailContent = "<p> Hi, "+ registeredUser.getFirstName()+ ", </p>"+
		          "<p>Thank you for registering with us, "+""+
				  "Please, follow the link below to complete your registration.</p>"+
		          "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
				  "<p> Thank you <br> Users Registration Portal Service";
		MimeMessage message = mailSender.createMimeMessage();
		var messageHelper = new MimeMessageHelper(message);
		messageHelper.setFrom("sahunitu817@gmail.com", senderName);
		messageHelper.setTo(registeredUser.getEmail());
		messageHelper.setSubject(subject);
		messageHelper.setText(mailContent, true);
		mailSender.send(message);	
	}
	

}
