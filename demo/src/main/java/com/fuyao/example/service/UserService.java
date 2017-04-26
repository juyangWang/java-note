package com.fuyao.example.service;

import java.util.List;

import com.fuyao.example.annotation.DataSource;
import com.fuyao.example.model.User;

public interface UserService {
	
	//@DataSource("master")
	Integer saveUser(User u);
	
	//@DataSource("slave")
	List<User> find();

}
