package org.whut.entity;

import java.io.Serializable;

public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * {"address":"北京市朝阳区和平西街5号院","userId":14,
	 * "image":"/inspectManagementResource/userImage/1/xiaozhujun.JPG",
	 * "userName":"xiaozhujun","lng":"116.421217",
	 * "devName":"门座式起重机#01","inspectTableName":"机修人员点检表",
	 * "lat":"39.975097"}
	 * */
	
	private String address;
	private int userId;
	private String image;
	private String userName;
	private String lng;
	private String devName;
	private String inspectTableName;
	private String lat;
	
	
	
	public Location() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Location(String address,int userId,String lng,String lat){
		this.address = address;
		this.userId = userId;
		this.lng = lng;
		this.lat = lat;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getInspectTableName() {
		return inspectTableName;
	}
	public void setInspectTableName(String inspectTableName) {
		this.inspectTableName = inspectTableName;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	
	
}
