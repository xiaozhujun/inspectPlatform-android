package org.whut.database.entity.service;

import java.util.List;

public interface UserRoleService {

	public void addUserRole(int user_id,String UserRole);

	List<String> getRoleById(int user_id);
}
