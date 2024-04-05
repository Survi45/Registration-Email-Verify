package com.email.verification.event;

import org.springframework.context.ApplicationEvent;

import com.email.verification.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationCmpltEvents extends ApplicationEvent {


	private User user;
	private String applicationUrl;

	public RegistrationCmpltEvents(User user, String applicationUrl) {
		super(user);
		this.user = user;
		this.applicationUrl = applicationUrl;
	}
}
