package com.fuyao.example.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fuyao.example.annotation.DataSource;
import com.fuyao.example.model.User;

@Repository("userMapper")
public interface UserMapper {
    @DataSource("master")
    public void add(User user);

    @DataSource("master")
    public void update(User user);

    @DataSource("master")
    public void delete(int id);

    @DataSource("slave")
    public User loadbyid(Integer id);

    @DataSource("master")
    public User loadbyname(String name);
    
    @DataSource("slave")
    public List<User> list();
}
