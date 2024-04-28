package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer  >  {
	//this method is used for  our email/user for secure
	@Query("select u from User u where u.email=:email")
	public User getUserByEmail(@Param("email") String email);

}
