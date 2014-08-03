package org.whut.database.entity.service;

import java.util.HashMap;

public interface UserService {

	public void addUser(HashMap<String,Object> map);
	public boolean findUserById(int user_id);
	public int findUserByUserName(String username);
	public String getRoleById(int user_id);
}
