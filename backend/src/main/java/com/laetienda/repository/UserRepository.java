package com.laetienda.repository;

import java.util.List;

import com.laetienda.myldap.User;

public interface UserRepository {
	public List<User> findAll(String username, String password);
	public User findByUsername(String username, String password, String uid);
	public User add(String username, String password, String uid, String name, String lastname, String email, String pass1, String pass2);
	public boolean delete(String username, String password, User user);
}
