package com.registerlogin.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.registerlogin.entity.User;
import com.registerlogin.repository.UserRepo;
import com.registerlogin.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserService userService;
	
	

	/*
	 * @GetMapping("/user/home") public String home() { return "home"; }
	 */
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	/*
	 * @GetMapping("/user/profile") public String profile(Principal p,Model m) {
	 * String email=p.getName(); User user = userRepo.findByEmail(email);
	 * m.addAttribute("user",user); return "profile"; }
	 */
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@Autowired
	private UserRepo userRepo;
	
	@ModelAttribute
	public void commonUser(Principal p,Model m) {
		if(p!=null) {
			String email=p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user",user);
		}	
		
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user,HttpSession session,Model m,HttpServletRequest request) {
		
//		System.out.println(user);
		String url = request.getRequestURL().toString();
//		System.out.println(url);
		url=url.replace(request.getServletPath(), "");
		
		
		//http://localhost:8089/verify?code=15b1a540-06ce-49ef-a58e-647585243ac1
		
		User u=userService.saveUser(user,url);
		if(u!=null) {
			//System.err.println("Save Success");
			
			session.setAttribute("msg","Register successfully");
		}else {
			//System.out.println("Error in Server");
			session.setAttribute("msg","Register not successfully");

		}
		
		return "redirect:/register";
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code,Model m) {
		
		boolean f = userService.verifyAccount(code);
		if(f) {
			m.addAttribute("msg","Sucessfully your account is verified");
		}else {
			m.addAttribute("msg","Your verification code is incorrect or already verified ");
		}
		
		return "message";
		
		
	}
}
