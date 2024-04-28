package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;






@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class MyConfig {
    @Bean
    UserDetailsService getUserDetailsService() {
	
	    return new CustomUserDetailsService();
		
		
		
	}


    @Bean
    BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		}

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider =new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
		}
	
	//config method
	
 
    	            
    	            
    
    
    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
	            authorizationManagerRequestMatcherRegistry
	                .requestMatchers("/admin/**").hasRole("ADMIN")
	                .requestMatchers("/user/**").hasRole("USER")
	                .requestMatchers("/**").permitAll())
	                .formLogin(form->
                     form
                .loginPage("/signin").loginProcessingUrl("/login").defaultSuccessUrl("/normal/").permitAll() // Specify custom login page
                 // Allow access to login page
        ).authorizeHttpRequests(auth->auth.requestMatchers("/img/**").permitAll().anyRequest().authenticated()); // Allow access to login page by anyone
	  
	    http.authenticationProvider(authenticationProvider());
	    return http.build();
		/*return http
				
				.formLogin(form->form.loginPage("/login").permitAll())
				.authorizeHttpRequests(auth->auth.anyRequest().authenticated())
				
			    .authenticationProvider(authenticationProvider())
				.build();*/
	}

	
	
	


}
