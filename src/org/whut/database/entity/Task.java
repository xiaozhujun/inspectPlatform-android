package org.whut.database.entity;

import java.io.Serializable;

public class Task implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/*
	{"message":"操作成功！","data":
	[{"id":89,"inspectPlanId":12,"inspectTableId":2,
	"inspectTableRecordId":0,"userId":6,"deviceId":13,
	"faultCount":0,"inspectTime":null,"createtime":"2014-07-27 01:00:00",
	"status":0,"taskDate":"2014-07-27 00:00:00","timeStart":8,"timeEnd":12,
	"appId":1,"tableName":"门机司机日常点检表","planName":"司机每日点检",
	"deviceName":"门座式起重机#01","userName":"赵伟","startDay":null,"endDay":null}
	],"code":200}
*/
	
	
	
	//主键
	private int taskId;
	
	//服务器端传过来的任务id，用来查询任务
	private int id;
	
	private int inspectPlanId;
	private int inspectTableId;
	private int inspectTableRecordId;
	private int userId;
	private int deviceId;
	private int faultCount;
	private String inspectTime;
	private String createtime;
	private int status;
	private String taskDate;
	private int timeStart;
	private int timeEnd;
	private int appId;
	private String tableName;
	private String planName;
	private String deviceName;
	private String userName;
	private String startDay;
	private String endDay;
	
	/*
	 * 记录本地状态：
	 * 0 	未完成
	 * 1	已完成
	 * */
	
	private int localStatus;


	

	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getInspectPlanId() {
		return inspectPlanId;
	}
	public void setInspectPlanId(int inspectPlanId) {
		this.inspectPlanId = inspectPlanId;
	}
	public int getInspectTableId() {
		return inspectTableId;
	}
	public void setInspectTableId(int inspectTableId) {
		this.inspectTableId = inspectTableId;
	}
	public int getInspectTableRecordId() {
		return inspectTableRecordId;
	}
	public void setInspectTableRecordId(int inspectTableRecord) {
		this.inspectTableRecordId = inspectTableRecord;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public int getFaultCount() {
		return faultCount;
	}
	public void setFaultCount(int faultCount) {
		this.faultCount = faultCount;
	}
	public String getInspectTime() {
		return inspectTime;
	}
	public void setInspectTime(String inspectTime) {
		this.inspectTime = inspectTime;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTaskDate() {
		return taskDate;
	}
	public void setTaskDate(String taskDate) {
		this.taskDate = taskDate;
	}
	public int getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(int timeStart) {
		this.timeStart = timeStart;
	}
	public int getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(int timeEnd) {
		this.timeEnd = timeEnd;
	}
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStartDay() {
		return startDay;
	}
	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}
	public String getEndDay() {
		return endDay;
	}
	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}
	public int getLocalStatus() {
		return localStatus;
	}
	public void setLocalStatus(int localStatus) {
		this.localStatus = localStatus;
	}
	
	
}
