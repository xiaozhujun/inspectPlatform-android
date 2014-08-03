package org.whut.utils;

import com.baidu.location.LocationClientOption;

public class LocationInit {
	LocationClientOption option = new LocationClientOption();
	private boolean isOpenGPS = true;
	private String  addrType = "all";
	private String  coorType = "bd09ll";
	private boolean isDisableCache = true;
	private int     scanSpan = 30000;
	private int     poiNumber = 5;
	private int		poiDistance = 1000;
	private boolean	poiExtraInfo =true;
	
	public void initOption(){
		option.setOpenGps(isOpenGPS);
		option.setAddrType(addrType);
		option.setCoorType(coorType);
		option.setScanSpan(scanSpan);
		option.disableCache(isDisableCache);
		option.setPoiNumber(poiNumber);	
		option.setPoiDistance(poiDistance); 
		option.setPoiExtraInfo(poiExtraInfo); 
	}
	
	public LocationClientOption getOption() {
		initOption();
		return option;
	}

	public boolean isOpenGPS() {
		return isOpenGPS;
	}

	public void setOpenGPS(boolean isOpenGPS) {
		this.isOpenGPS = isOpenGPS;
	}

	public String getAddrType() {
		return addrType;
	}

	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}

	public String getCoorType() {
		return coorType;
	}

	public void setCoorType(String coorType) {
		this.coorType = coorType;
	}

	public boolean isDisableCache() {
		return isDisableCache;
	}

	public void setDisableCache(boolean isDisableCache) {
		this.isDisableCache = isDisableCache;
	}

	public int getScanSpan() {
		return scanSpan;
	}

	public void setScanSpan(int scanSpan) {
		this.scanSpan = scanSpan;
	}

	public int getPoiNumber() {
		return poiNumber;
	}

	public void setPoiNumber(int poiNumber) {
		this.poiNumber = poiNumber;
	}

	public int getPoiDistance() {
		return poiDistance;
	}

	public void setPoiDistance(int poiDistance) {
		this.poiDistance = poiDistance;
	}

	public boolean isPoiExtraInfo() {
		return poiExtraInfo;
	}

	public void setPoiExtraInfo(boolean poiExtraInfo) {
		this.poiExtraInfo = poiExtraInfo;
	}
	
}

