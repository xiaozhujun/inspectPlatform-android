package org.whut.database.entity.service;

import java.util.Map;

public interface InspectImageService {

	public void addInspectImage(int itemId,String filePath,String inspectTableName,String itemName,int uploadFlag);
	public void updateInspectImage(String inspectTableName,String filePath,int itemId,int tableRecordId,int itemRecordId);
	public void deleteInspectImages(String inspectTableName);
	public Map<String, String> getImageInfo(String filePath);
}
