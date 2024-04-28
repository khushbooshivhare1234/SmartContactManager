package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart contact manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart contact manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - Smart contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	//handler for register user
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1, @RequestParam(value="checkme",defaultValue = "false")boolean checkme,Model model,HttpSession session  ) {
		try {
			if(!checkme) {
				System.out.println("not agree term and condition");
				throw new Exception("not agree term and condition");
			}
			
			if(result1.hasErrors()) {
				System.out.println("error"+result1.toString());
				model.addAttribute("user",user);
				return "signup"
						;
			}
			
			user.setRole("USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(checkme);
			System.out.println(user);
			
			User result = this.userRepository.save(user);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("successful register","alert-success" ));
			return "login";
		
			
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("server error"+ e.getMessage(),"alert-danger" ));
			return "signup";
		}
		
	}
	
	
	
	
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title","Login - Smart contact manager");
		return "login";
	}
	
	
	   

}
