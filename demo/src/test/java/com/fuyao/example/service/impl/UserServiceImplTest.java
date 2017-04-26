package com.fuyao.example.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fuyao.example.model.User;
import com.fuyao.example.service.UserService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-datasource.xml" })
public class UserServiceImplTest {
	
	//@Resource(name="userInfoService")
	@Resource
	UserService userService;
	@Test
	public void testSaveUser() {
		User u = new User();
		u.setName("test");
		userService.saveUser(u);
	}

	@Test
	public void testFind() {
		List<User> list = userService.find();
		for(User u : list){
			System.out.println(u.toString());
		}
	}

}
