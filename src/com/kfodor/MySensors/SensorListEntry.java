package com.kfodor.MySensors;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

// This is just a simple container class to hold a sensor object
// and any other user specific attributes about the sensor.
public class SensorListEntry {

	private static final String TAG = "SensorListEntry";

	// The following are persistent settings for each sensor

	// Keep and instance of this specific sensor
	private Sensor sensor;

	// Use NORMAL as the default rate
	private int rate = SensorManager.SENSOR_DELAY_NORMAL;

	// Flag to indicate if the user wants to see all available values or
	// only the known (documented) ones.
	private Boolean show_all_values = false;

	// Current screen orientation
	private Integer orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

	SensorListEntry(Sensor in_sensor) {
		sensor = in_sensor;
	}

	SensorListEntry(Sensor in_sensor, int in_rate) {
		sensor = in_sensor;
		rate = in_rate;
	}

	public void reset() {
		// Reset to defaults
		rate = SensorManager.SENSOR_DELAY_NORMAL;
		show_all_values = false;
		orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
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

	/**
	 * @return the show_all_values
	 */
	public Boolean getShowAllValues() {
		return show_all_values;
	}

	/**
	 * @param show_all_values
	 *            the show_all_values to set
	 */
	public void setShowAllValues(Boolean show_all_values) {
		this.show_all_values = show_all_values;
	}

	/**
	 * @return the orientation
	 */
	public Integer getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 *            the orientation to set
	 */
	public void setOrientation(Integer orientation) {
		this.orientation = orientation;
	}
}
