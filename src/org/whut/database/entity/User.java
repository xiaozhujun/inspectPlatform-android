package org.whut.database.entity;


public class User{

/*
 * {"message":"操作成功！","data":
 * {"number":6,"role":"机修人员","roleNum":1,
 * "name":"赵伟","userName":"zhaowei","id":6,
 * "image":"/inspectManagementResource/userImage/1/zhaowei.jpg",
 * "sex":null,"userRole":"ROLE_ADMIN"},"code":200}
 * 
 **/
	
	//用户角色
	private String role;
	//角色编号
	private String roleNum;
	//点检人员姓名
	private String name;
	//点检人员登录时的用户名
	private String userName;
	//ID
	private int id;
	//点检人员照片地址
	private String image;
	//点检人员性别
	private String sex;
	//用户权限
	private String userRole;

	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRoleNum() {
		return roleNum;
	}
	public void setRoleNum(String roleNum) {
		this.roleNum = roleNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
}
