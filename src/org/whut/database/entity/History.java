package org.whut.database.entity;

public class History {

	//主键
	private int historyId;
	
	//通过userId来查询用户点检历史
	private int userId;
	
	private String userName;

	private String filePath;
	//点检表名称
	private String tableName;
	
	private String deviceName;
	
	//是否上传
	private int uploadFlag;
	
	
	private String inspectTime;

	
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getInspectTime() {
		return inspectTime;
	}

	public void setInspectTime(String inspectTime) {
		this.inspectTime = inspectTime;
	}

	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(int uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	
}
