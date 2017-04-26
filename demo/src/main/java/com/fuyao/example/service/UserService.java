package com.fuyao.example.service;

import java.util.List;
import java.util.Map;

import com.fuyao.example.model.User;

public interface UserService {
	
	//@DataSource("master")
	Integer saveUser(User u);
	
	//@DataSource("slave")
	List<User> find();
	
	List<User> find(Map<String,Object> param);

}
