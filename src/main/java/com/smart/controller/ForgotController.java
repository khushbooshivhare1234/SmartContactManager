package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
	@RequestMapping("/forgot_password")
	public String openEmailForm() {
		return "forgot_password";
	}
	@PostMapping("/send_otp")
	public String sendOtp(@RequestParam("email") String email) {
		System.out.println(email);
		//generate 4 digit otp
		 Random random = new Random();
		 int randomNumber = 1000 + random.nextInt(9000);
		 System.out.println(randomNumber);
		 //code for send to email

		return "verify_otp";
	}

}
