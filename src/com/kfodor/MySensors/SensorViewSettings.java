package com.kfodor.MySensors;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.util.Log;

// This is just a simple container class to hold a SensorView's settings
// and any other user specific attributes about the view.
public class SensorViewSettings {

	public static final String TAG = "SensorViewSettings";

	// The following are persistent settings for each sensor.
	// Each of them is initialized with default values.

	// Use NORMAL as the default rate
	private int rate = SensorManager.SENSOR_DELAY_NORMAL;
	private static final String SENSOR_RATE_TAG = "rate";

	// Flag to indicate if the user wants to see all available values or
	// only the known (documented) ones. Default is false
	private boolean show_all_values = false;
	private static final String SENSOR_SHOW_ALL_VALUES_TAG = "show_all_values";

	// Current screen orientation. Default is portrait
	private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	private static final String SENSOR_ORIENTATION_TAG = "orientation";

	// Create with explicit values
	SensorViewSettings(int rate, boolean show_all_values, int orientation) {
		this.rate = rate;
		this.show_all_values = show_all_values;
		this.orientation = orientation;
	}

	// Reset all settings to defaults
	public void reset() {
		// Reset settings to defaults
		rate = SensorManager.SENSOR_DELAY_NORMAL;
		show_all_values = false;
		orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	}

	public int getRate() {
		return rate;
	}

	public boolean getShowAllValues() {
		return show_all_values;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setRate(int rate) {
		this.rate = rate;

		// Write some info to the log about this change
		String text = String.format("Setting 'rate' changed to %s",
				SensorInterface.delayToString(rate));
		Log.d(TAG, text);
	}

	public void setShowAllValues(boolean show_all_values) {
		this.show_all_values = show_all_values;

		// Write some info to the log about this change
		String text = String.format("Setting 'show-all' changed to %B",
				this.show_all_values);
		Log.d(TAG, text);
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;

		// Write some info to the log about this change
		String text = String.format("Setting 'orientation' changed to %d",
				this.orientation);
		Log.d(TAG, text);
	}

	public static SensorViewSettings restore(SharedPreferences preferences) {

		// Note this is where default settings are also established
		int rate = preferences.getInt(SENSOR_RATE_TAG,
				SensorManager.SENSOR_DELAY_NORMAL);
		boolean show_all_values = preferences.getBoolean(
				SENSOR_SHOW_ALL_VALUES_TAG, false);
		int orientation = preferences.getInt(SENSOR_ORIENTATION_TAG,
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SensorViewSettings obj = new SensorViewSettings(rate, show_all_values,
				orientation);

		// Write some info to the log about this restore
		String text = String
				.format("Restored settings; rate=%s, show_all_values=%B, orientation=%d",
						SensorInterface.delayToString(rate), show_all_values,
						orientation);
		Log.d(TAG, text);

		return obj;

	}

	public void save(SharedPreferences preferences) {

		// We need an Editor object to make preference changes.
		SharedPreferences.Editor editor = preferences.edit();

		// Store values between instances here
		editor.putInt(SENSOR_RATE_TAG, rate);
		editor.putBoolean(SENSOR_SHOW_ALL_VALUES_TAG, show_all_values);
		editor.putInt(SENSOR_ORIENTATION_TAG, orientation);

		// Write some info to the log about this save
		String text = String.format(
				"Saved settings; rate=%s, show_all_values=%B, orientation=%d",
				SensorInterface.delayToString(rate), show_all_values,
				orientation);
		Log.d(TAG, text);

		// Commit to storage
		editor.commit();

	}

}