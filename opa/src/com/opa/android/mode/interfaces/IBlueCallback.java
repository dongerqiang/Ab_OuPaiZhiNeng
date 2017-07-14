package com.opa.android.mode.interfaces;

import com.opa.android.mode.activity.DeviceDB;

public abstract class IBlueCallback {
	public void discoverDevice(DeviceDB.Record record){}
	public void deviceUpdate(int speed,int battery){}
}
