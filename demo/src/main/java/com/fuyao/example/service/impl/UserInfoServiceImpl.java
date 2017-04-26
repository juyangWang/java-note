package com.fuyao.example.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fuyao.example.annotation.DataSource;
import com.fuyao.example.mapper.UserMapper;
import com.fuyao.example.model.User;
import com.fuyao.example.service.UserService;
@Service("userInfoService")
public class UserInfoServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	@DataSource("master")
	public Integer saveUser(User u) {
		System.out.println("------userInfoService-------");
		userMapper.add(u);
		return u.getId();
	}
	
	@DataSource("slave")
	public List<User> find() {
		System.out.println("------userInfoService-------");
		return userMapper.list();
	}

	@DataSource("slave")
	public List<User> find(Map<String, Object> param) {
		System.out.println("------userInfoService param-------");
		return userMapper.list();
	}

}
