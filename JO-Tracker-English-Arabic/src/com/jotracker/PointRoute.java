package com.jotracker;

public class PointRoute {
    String mName;
    String mDescription;
    String mIconUrl;
    private double mLatitude;
    private double mLongitude;
	double getmLatitude() {
		return mLatitude;
	}
	void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}
	double getmLongitude() {
		return mLongitude;
	}
	void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
}
