package org.whut.entity;

import java.util.List;

import android.os.Parcelable;


public interface Listable extends Parcelable{
	public List<String> getParams();
	public void setByList(List<String> params);
	public int getPropertyCount();

}
