package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/normal")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository ;
	@Autowired
	private ContactRepository contactRepository;
	@ModelAttribute
	//method for common data
	public void addCommonData(Model model,Principal principal) {
		String username = principal.getName();
		System.out.println(username);
		//get the email using username(email)
		User user =userRepository.getUserByEmail(username);
		
		System.out.println(user);
		model.addAttribute("user",user);
		
		
	}
	//dashboard
	
	@RequestMapping("/")
	public String dashboard(Model model,Principal principal ) {
		model.addAttribute("title","User Dashboard");
		return "normal/dashboard";
	}
	//open add form handler
	@GetMapping("/add_contact")
	public String openAddContact(Model model,Principal principal ) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact";
	}
	//process add contact
	@RequestMapping("/process_contact")
	public String processContact(@ModelAttribute("contact") @Valid Contact contact, BindingResult result,@RequestParam("image")MultipartFile file, Principal principal,HttpSession session) {
		
		      try { String name = principal.getName();
		    	User user =this.userRepository.getUserByEmail(name);
		    	if(file.isEmpty()) {
		    		System.out.println("file is empty");
		    		contact.setImage("contact.png");
		    	}
		    	else {
		    		contact.setImage(file.getOriginalFilename());
		    	    File saveFile =	new ClassPathResource("static/img").getFile();
		            Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		    	    System.out.println("image save");	
		    	}
		        
		    	
		    	
		    	contact.setUser(user);
		    	user.getContacts().add(contact);
		    	this.userRepository.save(user);
		    	System.out.println(contact);
		    	System.out.println("added to database");
		    	//msg to success
		    	session.setAttribute("message", new Message("your contact is added","alert-success"));
		    	
		    	
		    	
		      }
		       
		      catch (Exception e) {
				System.out.println("error"+e.getMessage());
				e.printStackTrace();
				//error msg
				session.setAttribute("message", new Message("something went wrong !! try again", "alert-danger"));
			}
		      
		      return "normal/add_contact";
		    
		
	}
	
	//show all contact
	//per page =5[n]
	//current page=0[page]
	@GetMapping("/show_contacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","show Contacts");
		//contact list 
		String userName =principal.getName();
		User user =this.userRepository.getUserByEmail(userName);
		
		Pageable pageable=PageRequest.of(page, 7);
		Page<Contact> contacts =this.contactRepository.findContactByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		/*String username = principal.getName();
		User user = this.userRepository.getUserByEmail(username);
		List<Contact> contacts =user.getContacts();*/
		
		
		return "normal/show_contacts";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetais(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		System.out.println(cId);
	Optional<Contact> contactOptional=	this.contactRepository.findById(cId);
	 Contact contact=contactOptional.get(); 
	 
	String username= principal.getName();
	User user = this.userRepository.getUserByEmail(username);
	
	if(user.getId()==contact.getUser().getId()) {
		 model.addAttribute("contact", contact);
		
	}
	 
	
		return "normal/contact_details";
	}
	
	//delete handler
	@GetMapping("/delete/{cId}")
	@Transactional
	public String deleteContact(@PathVariable("cId") Integer cId,Model model,Principal principal) {
	Contact	contact=this.contactRepository.findById(cId).get();
		//Contact contact = contactOptional.get();
	
		String username= principal.getName();
		User user = this.userRepository.getUserByEmail(username);
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		if(user.getId()==contact.getUser().getId()) {
			 model.addAttribute("contact", contact);
			
		}
		//remove photo
		
		
		return "redirect:/normal/show_contacts/0";
	}
	//open update from handler
	@PostMapping("/update_contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		model.addAttribute("title", "update_form");
	Contact contact =	this.contactRepository.findById(cId).get();
	model.addAttribute("contact", contact);
		return "normal/update_contact";
	}
	//update handler
	@PostMapping("/process_update")
	public String updateHandler(@ModelAttribute("contact") @Valid Contact contact,BindingResult result,Model model,@RequestParam("image")MultipartFile file,Principal principal) {
		try {
			Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();
			//img new or not
			if(!file.isEmpty()) {
				File deleteFile =	new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile,oldContactDetails.getImage());
				file1.delete();
		         
				
				//delete old img 
				//update img
				  File saveFile =	new ClassPathResource("static/img").getFile();
		         Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		    	    contact.setImage(file.getOriginalFilename());
				
				
				
			}
			else {
				
				contact.setImage(oldContactDetails.getImage());
			}
			
			User user = this.userRepository.getUserByEmail(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(contact);
		return "redirect:/normal/"+contact.getcId()+"/contact";
	}
	
	
	// your profile
	@GetMapping("/your_profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "profile page");
		return "normal/your_profile";
	}
	//setting 
	@GetMapping("/settings")
	public String openSetting(Model model) {
		return "normal/settings";
	}
	
	@PostMapping("/change_password")
	public String passwordChange(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal) {
		System.out.println(oldPassword+newPassword);
		
		String userName =principal.getName();
	User currentUser   = this.userRepository.getUserByEmail(userName);
	System.out.println(currentUser.getPassword());
	
	if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
		currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(currentUser);
	}
	else {
		return "normal/settings";
		
	}
		return "normal/dashboard";
	}
	
	
	
	
	
}
	
	  
	



