package com.kfodor.MySensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;

// This is just a simple container class to hold a sensor object
// and any other user specific attributes about the sensor.
public class SensorListEntry {

	public static final String TAG = "SensorListEntry";

	// Keep and instance of this specific sensor object
	private Sensor sensor = null;

	// Index of this sensor
	private int index = -1;

	// Create from sensor manager
	SensorListEntry(SensorManager mgr) {
		this.sensor = MySensors.findSensor(mgr, index);
	}

	// Create from sensor manager and sensor index
	SensorListEntry(SensorManager mgr, int index) {
		this.sensor = MySensors.findSensor(mgr, index);
		this.index = index;
	}

	// Create from sensor and sensor index
	SensorListEntry(Sensor sensor, int index) {
		this.sensor = sensor;
		this.index = index;
	}

	// Get sensor
	public Sensor getSensor() {
		return sensor;
	}

	// Get index
	public int getIndex() {
		return index;
	}

	// Assign sensor
	public void assignSensor(SensorManager mgr) {
		this.sensor = MySensors.findSensor(mgr, index);
	}
}
