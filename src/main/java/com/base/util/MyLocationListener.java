package com.base.util;

import android.location.Location;

public interface MyLocationListener {
	public void updateLocation(double latitude, double longitude);

	public void updateLocation(Location location);
}
