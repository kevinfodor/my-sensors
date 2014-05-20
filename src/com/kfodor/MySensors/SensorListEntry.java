package com.kfodor.MySensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

// This is just a simple container class to hold a sensor object
// and any other user specific attributes about the sensor.
public class SensorListEntry {

	private static final String TAG = "SensorListEntry";

	public static final int default_rate = SensorManager.SENSOR_DELAY_NORMAL;

	private Sensor sensor;

	// Use NORMAL as the default rate
	private int rate = default_rate;

	SensorListEntry(Sensor in_sensor) {
		sensor = in_sensor;
	}

	SensorListEntry(Sensor in_sensor, int in_rate) {
		sensor = in_sensor;
		rate = in_rate;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;

		// Write some info to the log about this change
		String text = String.format("%1$s sensor rate changed to %2$s\n",
				sensor.getName(), SensorInterface.delayToString(rate));
		Log.d(TAG, text);
	}
}
