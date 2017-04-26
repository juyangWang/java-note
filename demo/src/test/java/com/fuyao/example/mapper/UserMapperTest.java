package com.fuyao.example.mapper;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fuyao.example.model.User;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-datasource.xml" })
public class UserMapperTest extends TestCase {
	
	@Autowired
	private UserMapper userMapper;

	@Test
	public void testAdd() {
		//
		User user = new User();
		//user.setId(1);
		user.setName("test");
		userMapper.add(user);
	}
	@Test
	public void testUpdate() {
		
		User user = new User();
		user.setId(1);
		user.setName("test1");
		userMapper.update(user);
	}
	@Test
	public void testDelete() {
		
	}
	@Test
	public void testLoadbyid() {
		User u = userMapper.loadbyid(1);
		System.out.println(u.toString());
	}
	@Test
	public void testLoadbyname() {
		
		User user = userMapper.loadbyname("test1");
		Assert.assertNotNull(user);
		System.out.println(user.toString());
	}
	@Test
	public void testList() {
		
		List<User> userlist = userMapper.list();
		for(User u : userlist){
			System.out.println(u.toString());
		}
	}

}
