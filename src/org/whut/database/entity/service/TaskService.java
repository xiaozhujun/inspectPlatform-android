package org.whut.database.entity.service;

import org.whut.database.entity.Task;

public interface TaskService {

	public void addTask(Task task);
	public int findTask(int id);
	public void updateStatus(int pointer,int status);
	public void updateLocalStatus(int pointer);
}
