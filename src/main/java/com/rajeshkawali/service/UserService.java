package com.rajeshkawali.service;

import java.util.List;

import javax.validation.Valid;

import com.rajeshkawali.dto.User;


/**
 * @author Rajesh_Kawali
 *
 */
public interface UserService {

	public List<User> getAllUsers();

	public User addUser(@Valid User user);

	public User userById(Long id);

	public List<User> getUserByName(String name);

	public User updateUser(Long id, User user);

	public String deleteUser(Long id);
	
	public User getUserByIdWithRetry(Long userId);
	
	public User getUserByIdWithCustomErrorHandling(Long userId);
	
	public User addNewUserWithCustomErrorHandling(User user);
	
	public String errorEndpoint();
	
	public String errorEndpointFixedRetry();
}
