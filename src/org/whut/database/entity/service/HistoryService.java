package org.whut.database.entity.service;

import java.util.List;
import java.util.Map;

import org.whut.database.entity.History;

public interface HistoryService {

	public void addHistory(History history);
	public int findHistory(int userId);
	public List<Map<String,String>> queryHistory(int userId);
	public void deleteHistory(String filePath);
	public void updateUploadFlag(String filePath);
	public int isUploaded(String filePath);
}
