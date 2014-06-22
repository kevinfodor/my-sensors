package com.kfodor.MySensors;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kfodor.MySensors.SparseArrayAdaptor;

/*
 * SensorInterface
 *
 * This is a simple class intended to make working with
 * Sensors a little easier.
 *
 */

@SuppressWarnings("unused")
public class SensorInterface {

	// Generic 'unknown' text
	private final static String unknown = "Unknown";

	// Sensor Type Text
	private final static String name_unknown = unknown;
	private final static String name_accelerometer = "Accelerometer";
	private final static String name_magnetic_field = "Magnetic Field";
	private final static String name_orientation = "Orientation";
	private final static String name_gyroscope = "Gyroscope";
	private final static String name_light = "Light";
	private final static String name_pressure = "Pressure";
	private final static String name_temperature = "Temperature";
	private final static String name_proximity = "Proximity";
	private final static String name_gravity = "Gravity";
	private final static String name_linear_acceleration = "Linear Acceleration";
	private final static String name_rotation_vector = "Rotation Vector";
	private final static String name_relative_humidity = "Relative Humidity";
	private final static String name_ambient_temperature = "Ambient Temperature";
	private final static String name_magnetic_field_uncalibrated = "Magnetic Field (Uncalibrated)";
	private final static String name_game_rotation_vector = "Game Rotation Vector";
	private final static String name_gyroscope_uncalibrated = "Gyroscope (Uncalibrated)";
	private final static String name_significant_motion = "Significant Motion";
	private final static String name_step_detector = "Step Detector";
	private final static String name_step_counter = "Step Counter";
	private final static String name_geometric_rotation_vector = "Geometric Rotation Vector";

	// Sensor Units Text
	private final static String units_unknown = "?";
	private final static String units_meters_per_second_squared = "m/s&#178;";
	private final static String units_micro_teslas = "&#181;T";
	private final static String units_degrees = "&#176;";
	private final static String units_radians_per_second = "rad/s";
	private final static String units_lux = "lx";
	private final static String units_hectopascals = "hPa";
	private final static String units_degrees_c = "&#176;C";
	private final static String units_centimeters = "cm";
	private final static String units_percent = "%";
	private final static String units_none = "";
	private final static String units_steps = "steps";
	private final static String units_event = "event";

	// Sensor label arrays
	private final static String[] unknown_labels = { unknown };

	private final static String[] gyro_labels = { "X-Axis [Azimuth]",
			"Y-Axis [Pitch]", "Z-Axis [Roll]" };

	private final static String[] rotation_labels = { "X-Axis", "Y-Axis",
			"Z-Axis", "Optional", "Estimated Heading Accuracy" };

	private final static String[] pressure_labels = { "Pressure" };

	private final static String[] distance_labels = { "Distance" };

	private final static String[] temperature_labels = { "Temperature" };

	private final static String[] humidity_labels = { "Humidity" };

	private final static String[] light_labels = { "Ambient" };

	private final static String[] acceleration_labels = { "Lateral",
			"Longitudinal", "Vertical" };

	private final static String[] magnetic_uncal_labels = { "X-Axis", "Y-Axis",
			"Z-Axis", "X-Axis Bias", "Y-Axis Bias", "Z-Axis Bias" };

	private final static String[] gyro_uncal_labels = { "X-Axis [Azimuth]",
			"Y-Axis [Pitch]", "Z-Axis [Roll]", "Estimated X-Axis [Azimuth]",
			"Estimated Y-Axis [Pitch]", "Estimated Z-Axis [Roll]" };

	// Sensor Type Class
	private static class SensorType {

		// Sensor Type element
		private String name_text;
		private String units_text;
		private int icon_id;
		private String[] labels;

		private SensorType(String name_text, String units_text, int icon_id,
				String[] labels) {
			this.name_text = name_text;
			this.units_text = units_text;
			this.icon_id = icon_id;
			this.labels = labels;
		}

	}

	// The Sensor Type Array (name, units, icon)
	private final static SparseArray<SensorType> types = createSensorTypeArray();

	// The Sensor delay text
	public final static SparseArray<String> delay = createSensorDelayArray();

	// The Sensor accuracy text
	private final static SparseArray<String> accuracy = createSensorAccuracyArray();

	// Sensor this object interfaces to.
	private Sensor sensor = null;

	// Sensor id this object interfaces to.
	private int sensor_type_id = 0;

	// This sensor's lookup table entry (default = 0)
	private SensorType sensor_type = types.get(0);

	SensorInterface(Sensor sensor) {
		// Initialize the object
		if (sensor == null) {
			throw new IllegalArgumentException("Invalid sensor provided!");
		} else {

			this.sensor = sensor; // assign sensor

			// Find this sensor's type in the array
			sensor_type_id = sensor.getType();
			sensor_type = types.get(sensor_type_id);
			if (sensor_type == null) {
				// Use the default
				sensor_type = types.get(0);
			}
		}
	}

	private static SparseArray<String> createSensorDelayArray() {
		SparseArray<String> array = new SparseArray<String>();

		array.put(SensorManager.SENSOR_DELAY_FASTEST, "Fastest");
		array.put(SensorManager.SENSOR_DELAY_GAME, "Game");
		array.put(SensorManager.SENSOR_DELAY_UI, "UI");
		array.put(SensorManager.SENSOR_DELAY_NORMAL, "Normal");

		return array;
	}

	// Initialize sensor accuracy text and their mapping
	// See:
	// http://developer.android.com/reference/android/hardware/SensorManager.html
	private static SparseArray<String> createSensorAccuracyArray() {
		SparseArray<String> array = new SparseArray<String>();

		array.put(SensorManager.SENSOR_STATUS_UNRELIABLE, "Unreliable");
		array.put(SensorManager.SENSOR_STATUS_ACCURACY_LOW, "Low");
		array.put(SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM, "Medium");
		array.put(SensorManager.SENSOR_STATUS_ACCURACY_HIGH, "High");

		return array;
	}

	// Initialize the sensor types we support and their mapping
	// See:
	// http://developer.android.com/guide/topics/sensors/sensors_overview.html
	// This populates the static array of sensor info based on type.
	// The sensor info is name, units, and icon
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static SparseArray<SensorType> createSensorTypeArray() {
		SparseArray<SensorType> array = new SparseArray<SensorType>();

		array.put(0, new SensorType(name_unknown, units_unknown,
				R.drawable.unknown_sensor, unknown_labels));

		array.put(Sensor.TYPE_ACCELEROMETER, new SensorType(name_accelerometer,
				units_meters_per_second_squared,
				R.drawable.accelerometer_sensor, acceleration_labels));

		array.put(Sensor.TYPE_MAGNETIC_FIELD, new SensorType(
				name_magnetic_field, units_micro_teslas,
				R.drawable.magnetic_sensor, acceleration_labels));

		array.put(Sensor.TYPE_ORIENTATION, new SensorType(name_orientation,
				units_degrees, R.drawable.orientation_sensor, gyro_labels));

		array.put(Sensor.TYPE_GYROSCOPE, new SensorType(name_gyroscope,
				units_radians_per_second, R.drawable.gyroscope_sensor,
				gyro_labels));

		array.put(Sensor.TYPE_LIGHT, new SensorType(name_light, units_lux,
				R.drawable.light_sensor, light_labels));

		array.put(Sensor.TYPE_PRESSURE,
				new SensorType(name_pressure, units_hectopascals,
						R.drawable.pressure_sensor, pressure_labels));

		array.put(Sensor.TYPE_TEMPERATURE, new SensorType(name_temperature,
				units_degrees_c, R.drawable.temp_sensor, temperature_labels));

		array.put(Sensor.TYPE_PROXIMITY,
				new SensorType(name_proximity, units_centimeters,
						R.drawable.proximity_sensor, distance_labels));

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			array.put(Sensor.TYPE_GRAVITY, new SensorType(name_gravity,
					units_meters_per_second_squared, R.drawable.gravity_sensor,
					acceleration_labels));

			array.put(Sensor.TYPE_LINEAR_ACCELERATION, new SensorType(
					name_linear_acceleration, units_meters_per_second_squared,
					R.drawable.linear_acceleration_sensor, acceleration_labels));

			array.put(Sensor.TYPE_ROTATION_VECTOR, new SensorType(
					name_rotation_vector, units_none,
					R.drawable.rotation_vector_sensor, rotation_labels));
		}
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			array.put(Sensor.TYPE_RELATIVE_HUMIDITY, new SensorType(
					name_relative_humidity, units_percent,
					R.drawable.relative_humidity_sensor, humidity_labels));

			array.put(Sensor.TYPE_AMBIENT_TEMPERATURE, new SensorType(
					name_ambient_temperature, units_degrees_c,
					R.drawable.temp_sensor, temperature_labels));
		}
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			array.put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, new SensorType(
					name_magnetic_field_uncalibrated, units_micro_teslas,
					R.drawable.magnetic_sensor, magnetic_uncal_labels));

			array.put(Sensor.TYPE_GAME_ROTATION_VECTOR, new SensorType(
					name_game_rotation_vector, units_none,
					R.drawable.rotation_vector_sensor, rotation_labels));

			array.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, new SensorType(
					name_gyroscope_uncalibrated, units_radians_per_second,
					R.drawable.gyroscope_sensor, gyro_uncal_labels));

			array.put(Sensor.TYPE_SIGNIFICANT_MOTION, new SensorType(
					name_significant_motion, units_event,
					R.drawable.unknown_sensor, unknown_labels));
		}
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			array.put(Sensor.TYPE_STEP_DETECTOR, new SensorType(
					name_step_detector, units_steps, R.drawable.unknown_sensor,
					unknown_labels));

			array.put(Sensor.TYPE_STEP_COUNTER, new SensorType(
					name_step_counter, units_event, R.drawable.unknown_sensor,
					unknown_labels));

			array.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, new SensorType(
					name_geometric_rotation_vector, units_none,
					R.drawable.rotation_vector_sensor, rotation_labels));
		}
		return array;
	}

	public final Sensor getSensor() {
		return sensor;
	}

	public final String getType() {
		return sensor_type.name_text;
	}

	public final Spanned getUnits() {
		return Html.fromHtml(sensor_type.units_text);
	}

	public final int getIcon() {
		return sensor_type.icon_id;
	}

	public final int getNumLabels() {
		return sensor_type.labels.length;
	}

	public final static String getType(int sensor_type) {
		SensorType type = types.get(sensor_type, types.get(0));
		return type.name_text;
	}

	public final static int getIcon(int sensor_type) {
		SensorType type = types.get(sensor_type, types.get(0));
		return type.icon_id;
	}

	public final String getLabel(int index) {
		String label = unknown;
		// Make sure request is within bounds
		if ((index >= 0) && (index < sensor_type.labels.length)) {
			label = sensor_type.labels[index];
		}
		return label;
	}

	public final static String accuracyToString(int a) {
		String accuracy_text = accuracy.get(a, unknown);
		return accuracy_text;
	}

	public final static String delayToString(int d) {
		String delay_text = delay.get(d, unknown);
		return delay_text;
	}

}
