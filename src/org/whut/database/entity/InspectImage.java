package org.whut.database.entity;

public class InspectImage {
	//主键
	private int id;
	//itemId
	private int itemId;
	//本地存储地址
	private String filePath;
	//所属表名
	private String inspectTableName;
	//点检项名
	private String itemName;
	//是否上传
	private int uploadFlag;
	//服务器端返回
	private int tableRecordId;
	//服务器端返回
	private int itemRecordId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getInspectTableName() {
		return inspectTableName;
	}

	public void setInspectTableName(String inspectTableName) {
		this.inspectTableName = inspectTableName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(int uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public int getTableRecordId() {
		return tableRecordId;
	}

	public void setTableRecordId(int tableRecordId) {
		this.tableRecordId = tableRecordId;
	}

	public int getItemRecordId() {
		return itemRecordId;
	}

	public void setItemRecordId(int itemRecordId) {
		this.itemRecordId = itemRecordId;
	}	
}

