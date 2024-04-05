package com.email.verification.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserRegSecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		
	}
	
//	public UserDetailsService getDetailsService() {
//		return new UserRegDetailsService();
//	}
//	
//	public DaoAuthenticationProvider getAuthenticationProvider() {
//		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//		daoAuthenticationProvider.setUserDetailsService(null);
//	}
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http.csrf(AbstractHttpConfigurer::disable) 
	        .authorizeHttpRequests((authorize) -> authorize
	            .requestMatchers("/register/**").permitAll() 
	            .requestMatchers("/users/**").hasAnyAuthority("USER", "ADMIN")
	            .anyRequest().authenticated()
	        )
	        		.formLogin(form -> form
	    			.loginPage("/signin")
	    			.loginProcessingUrl("/userLogin")
	    			.defaultSuccessUrl("/users").permitAll()
	    		).build();
	}


	

}
