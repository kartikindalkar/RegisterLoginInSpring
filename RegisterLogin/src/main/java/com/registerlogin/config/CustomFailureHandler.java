package com.registerlogin.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.registerlogin.entity.User;
import com.registerlogin.repository.UserRepo;
import com.registerlogin.services.UserService;
import com.registerlogin.services.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepo userRepo;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String email = request.getParameter("username");
		User user = userRepo.findByEmail(email);
		 
		if(user != null) {			
			if(user.isEnable()) {
				if(user.isAccountNonLocked()) {
					if(user.getFailedAttempt()<UserServiceImpl.ATTEMTP_TIME-1) {
						userService.increaseFailedAttempt(user);
					}else {
						userService.lock(user);
						exception=new LockedException("Account is Locked.Failed to Login");
					}
				}else if(!user.isAccountNonLocked()) {
					if(userService.unlockAccountTimeExpired(user)) {
						exception=new LockedException("Account is unlocked. Please try to Login");
					}else {
						exception=new LockedException("Account is locked. Please try After sometime");
					}
				}
			}else {
				exception = new LockedException("Account is Inactive..Please Verify Account");
			}
		}
		
		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}
	
}
