package com.kfodor.MySensors;

import java.util.ArrayList;
import java.util.Locale;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class SensorView extends FragmentActivity implements
		SensorEventListener, SensorRateChangeDlg.SensorRateChangeListener {

	// Keys for extras put onto the intent launch
	public static final String SENSOR_INDEX_EXTRA = "SensorIndex";

	// Helpful sensor constants
	private static final String TAG = "SensorView";
	private static final float NS2S = 1.0f / 1000000000.0f;

	// Initialized with initSensor()
	private SensorManager mgr = null; // Sensor Manager
	private SensorInterface si = null; // The interface to this sensor

	// Dynamic (real-time views) references for quick/easy reference while
	// updating. Initialized with initRealTimeViews()
	private TextView delay_view = null;
	private TextView event_count_view = null;
	private TextView timestamp_view = null;
	private TextView accuracy_view = null;
	private LinearLayout known_ll = null;
	private LinearLayout unknown_ll = null;
	private TextView num_values_available_view = null;
	private TextView num_values_shown_view = null;
	private ArrayList<TextView> data_value_views = new ArrayList<TextView>();

	// Toggle button (start/stop)
	private ToggleButton start_stop_toggle_button = null;

	// Toggle button (start/stop)
	private ToggleButton start_stop_logging_button = null;

	// This sensor view settings (persistent)
	private SensorViewSettings settings = null;
	SharedPreferences preferences = null;
	private String settings_name = null;

	// Sensor event counter
	private Integer event_counter = 0;

	// Sensor number of available values
	private Integer num_available_values = 0;

	// Sensor Logger object
	private SensorLogger logger = null;

	/*
	 * Called when the activity is first created. This is where you should do
	 * all of your normal static set up: create views, bind data to lists, etc.
	 * This method also provides you with a Bundle containing the activity's
	 * previously frozen state, if there was one. Always followed by onStart().
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize activity.
		Log.d(TAG, "onCreate\n");

		// Inflate sensor view
		setContentView(R.layout.sensor_view);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// For the main activity, make sure the app icon in the action bar
			// does not behave as a button
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(false);
		}

		// Extract calling activity provided 'extras'
		// which will help us determine which sensor we
		// are going to use in this activity.
		int index;

		// Verify that we can retrieve the sensor entry passed to us
		// for this view.
		try {
			Bundle extras = getIntent().getExtras();
			// The parent activity passed to us the "sensor position"
			// within the sensor list which this activity will handle.
			index = extras.getInt(SensorView.SENSOR_INDEX_EXTRA, 0);
		} catch (NullPointerException e) {
			// The entry is not available, throw exception
			throw new NullPointerException(TAG + ": cannot get extras.");
		}

		// Construct a unique sensor view settings name. This name
		// is used to store this view's settings.
		settings_name = SensorView.getName(getApplicationContext(), index);

		// Restore settings
		preferences = getSharedPreferences(settings_name, MODE_PRIVATE);
		settings = new SensorViewSettings(preferences);

		// Initialize the sensor we are using
		initSensor(index);

		// Initialize basic (static) sensor view
		loadStaticView();

		// Initialize real-time (dynamic) sensor view references
		initRealTimeViews();

		// Initialize buttons
		loadButtonViews();

		// Fix issue #1 which will keep the screen from timing out
		// or going off while viewing sensor data.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Prevent rotation of the screen, due to sensor changes
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		// Create a file logger for this sensor
		if (logger == null) {
			String dir = MySensors.getStoragePath(this);
			String prefix = String.format(Locale.US, "MySensors_%d", index + 1);
			logger = new SensorLogger(dir, prefix,
					getString(R.string.sensor_log_file_ext), si);
		}

		// Apply settings
		applySettings();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		onCreate(savedInstanceState);
		return container;
	}

	/*
	 * Called after onCreate has finished, use to restore UI state. from the
	 * savedInstanceState. This bundle has also been passed to onCreate.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass first
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "onRestoreInstanceState\n");
	}

	/*
	 * Called at the start of the visible lifetime. Apply any required UI change
	 * now that the Activity is visible.
	 */
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart\n");
	}

	/*
	 * Called at the start of the active lifetime. Resume any paused UI updates,
	 * threads, or processes required. by the activity but suspended when it was
	 * inactive.
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume\n");

		// On resume, re-apply settings
		applySettings();
	}

	/*
	 * Called at the end of the active lifetime. Suspend UI updates, threads, or
	 * CPU intensive processes that don’t need to be updated when the Activity
	 * isn’t the active foreground activity.
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause\n");

		// Stop listening to sensor updates
		unregisterSensorListener();
	}

	/*
	 * Called at the end of the visible lifetime. Suspend remaining UI updates,
	 * threads, or processing that aren’t required when the Activity isn’t
	 * visible. Persist all edits or state changes as after this call the
	 * process is likely to be killed. (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop\n");

		// Save settings
		preferences = getSharedPreferences(settings_name, MODE_PRIVATE);
		settings.save(preferences);
	}

	/*
	 * Called before subsequent visible lifetimes for an activity process. Load
	 * changes knowing that the activity has already been visible within this
	 * process. Next call is onStart.
	 */
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart\n");
	}

	/*
	 * Called to save UI state changes at the end of the active life cycle. Save
	 * UI state changes to the savedInstanceState. (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
	 * .Bundle) This bundle will be passed to onCreate if the process is killed
	 * and restarted. This is called just before the activity is destroyed.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
		Log.d(TAG, "onSaveInstanceState\n");
	}

	/*
	 * Called at the end of the full lifetime. Clean up any resources including
	 * ending threads, closing database connections etc.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy\n");
	}

	// Callback for creating dialogs that are managed (saved and restored)
	// for you by the activity. This is only called once, the first time
	// the options menu is displayed.
	protected Dialog onCreateDialog(int id, Bundle savedInstanceState) {

		Dialog dialog = null;
		switch (id) {
		// Handle dialogs by id here...
		default:
		}
		return dialog;
	};

	// Provides opportunity to prepare your dialog before it is shown
	@Override
	protected void onPrepareDialog(int id, Dialog dialog,
			Bundle savedInstanceState) {
		switch (id) {
		// Prepare dialogs by id here...
		default:
		}
		return;
	}

	// Initialize the contents of the Activity's standard options menu.
	// This is only called once, the first time the options menu is
	// displayed.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Inflate menu view
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sensor_view, menu);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		Boolean checked;

		// Get state of menu options
		checked = settings.getShowAllValues();
		menu.findItem(R.id.show_all_values).setChecked(checked);

		checked = settings.getLogData();
		menu.findItem(R.id.log_data).setChecked(checked);

		return true;
	}

	// This hook is called whenever an item in your options menu is selected.
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		int id = item.getItemId();

		// Find which menu item has been selected
		// Check for each known menu item
		switch (id) {

		// Rotate Screen...
		case (R.id.rotate_screen): {

			// Get current orientation
			Integer orientation = settings.getOrientation();

			// Toggle orientation
			if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			} else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			}

			// Store new orientation
			settings.setOrientation(orientation);

			// Request new orientation
			setRequestedOrientation(orientation);

			return true; // handled
		}
		// Change Event Rate...
		case (R.id.change_event_rate): {
			// Create an instance of the dialog fragment and show it.
			DialogFragment rate = new SensorRateChangeDlg();
			rate.show(getSupportFragmentManager(), "rate");

			return true; // handled
		}
		// Show all values...
		case (R.id.show_all_values): {

			// Toggle current 'show' state
			Boolean show_all_values = settings.getShowAllValues();
			show_all_values = !show_all_values;
			settings.setShowAllValues(show_all_values);

			// Handle check/unchecked setting.
			item.setChecked(show_all_values);

			// Update number of shown values
			updateShownValues();

			return true; // handled
		}
		// Log data...
		case (R.id.log_data): {

			// Toggle current 'log' state
			Boolean log_data = settings.getLogData();
			log_data = !log_data;

			// Record new setting
			settings.setLogData(log_data);

			// Handle check/unchecked setting.
			item.setChecked(log_data);

			return true; // handled
		}
		// Reset view...
		case (R.id.reset): {

			settings.reset();

			// Reset event counter
			event_counter = 0;

			// Update event count text view with event data
			event_count_view.setText(event_counter.toString());

			applySettings();

			return true; // handled
		}
		// Search...
		case (R.id.web_search): {

			// Do a simple web search for the sensor
			String url = String.format(getString(R.string.sensor_search_url),
					si.getSensor().getVendor(), si.getSensor().getName(),
					si.getType());

			// Create a new intent to launch the web search
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));

			// Start the web search
			startActivity(i);

			return true;
		}
		// About...
		case (R.id.about): {
			// Create an instance of the dialog fragment and show it.
			DialogFragment about = new AboutDlg();
			about.show(getSupportFragmentManager(), "about");

			return true; // handled
		}
		}

		// Return false if you have not handled the menu item.
		return false;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not sure what exactly to do when the sensor
		// accuracy has changed, but perhaps some applications
		// need to use this as a way to impact how they use the sensors.
		// in any case we just log the event.
		String text = String.format(
				getString(R.string.sensor_accuracy_changed), sensor.getName(),
				SensorInterface.accuracyToString(accuracy));
		Log.d(TAG, text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	public void onSensorChanged(SensorEvent event) {

		// Sensor changed event fired, verify it is the type we are
		// handling currently.
		if (event.sensor.getType() == this.si.getSensor().getType()) {

			// Increment event counter
			event_counter++;

			// Update event count text view with event data
			event_count_view.setText(event_counter.toString());

			// Update accuracy text view with event data
			accuracy_view.setText(SensorInterface
					.accuracyToString((event.accuracy)));

			// Convert time stamp to seconds and update time stamp view
			Float ts = event.timestamp * NS2S;
			timestamp_view.setText(ts.toString());

			// Update the number of values available and text view to show it.
			num_available_values = event.values.length;
			num_values_available_view.setText(num_available_values.toString());

			// Loop for each available sensor value...
			for (int i = 0; i < num_available_values; i++) {

				if (i >= data_value_views.size()) {
					// Add a new data value child view (with unknown label)
					addDataValueChildView(i);
				}

				// Update number of shown values
				updateShownValues();

				// Get text view for this data value
				TextView tv = data_value_views.get(i);

				// Check for a valid view
				if (tv != null) {
					// Update the text view with the value
					tv.setText(String.valueOf(event.values[i]));
				}

				// write sensor info about this event's data
				writeSensorData(i, event);
			}

			// Log information about this sensor
			logger.write(event);
		}
	}

	// Helper method to construct a string which will uniquely identify
	// the settings for this sensor's view
	public final static String getName(Context context, int index) {
		// Construct a unique sensor view settings name. This name
		// is used to store this view's settings.
		final String name = context.getPackageName() + "."
				+ SensorViewSettings.TAG + "." + String.valueOf(index);
		return name;
	}

	// Helper method to update number of 'shown' values
	private void updateShownValues() {

		Boolean show_all_values = settings.getShowAllValues();
		Integer num_shown_values = si.getNumLabels();

		// Based on the 'show' state, make data values visible/gone
		if (show_all_values == true) {
			// Make all unknown data values visible. This means
			// all available values will be shown.
			unknown_ll.setVisibility(View.VISIBLE);

			// Add the number of unknown values to the number shown.
			num_shown_values += unknown_ll.getChildCount();
		} else {
			// Make all unknown data values gone
			unknown_ll.setVisibility(View.GONE);
		}

		// Set number of shown values in view
		num_values_shown_view.setText(num_shown_values.toString());
	}

	// Simple method to retrieve the sensor we are viewing
	private void initSensor(int index) {

		// Find this sensor by index
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = MySensors.findSensor(mgr, index);

		String text = String.format(Locale.US,
				"%s assigned, using sensor index: %d", sensor.getName(),
				index + 1);
		Log.d(TAG, text);

		// Now that we have the sensor, create a sensor interface object
		// and assign it to the interface.
		si = new SensorInterface(sensor);

		// Write some info to the log about this sensor
		writeSensorInfo(index + 1);

		return;
	}

	// method to load in the static sensor information
	private void loadStaticView() {

		ImageView iv;
		TextView tv;
		Sensor sensor = si.getSensor();

		// Load sensor icon
		iv = (ImageView) findViewById(R.id.sensor_icon);
		if (iv != null) {
			iv.setImageResource(si.getIcon());
		}
		// Load sensor name
		tv = (TextView) findViewById(R.id.sensor_name);
		if (tv != null) {
			tv.setText(sensor.getName());
		}
		// Load sensor vendor
		tv = (TextView) findViewById(R.id.sensor_vendor);
		if (tv != null) {
			tv.setText(sensor.getVendor());
		}
		// Load sensor version
		tv = (TextView) findViewById(R.id.sensor_version);
		if (tv != null) {
			tv.setText(((Integer) sensor.getVersion()).toString());
		}
		// Load sensor type
		tv = (TextView) findViewById(R.id.sensor_type);
		if (tv != null) {
			tv.setText(si.getType() + " ("
					+ ((Integer) sensor.getType()).toString() + ")");
		}
		// Load sensor power
		tv = (TextView) findViewById(R.id.sensor_power);
		if (tv != null) {
			tv.setText(((Float) sensor.getPower()).toString());
		}
		// Load sensor range
		tv = (TextView) findViewById(R.id.sensor_range);
		if (tv != null) {
			tv.setText(((Float) sensor.getMaximumRange()).toString());
		}
		// Load sensor range units
		tv = (TextView) findViewById(R.id.sensor_range_units);
		if (tv != null) {
			tv.setText(si.getUnits());
		}
		// Load sensor resolution
		tv = (TextView) findViewById(R.id.sensor_resolution);
		if (tv != null) {
			tv.setText(((Float) sensor.getResolution()).toString());
		}
		// Load sensor resolution units
		tv = (TextView) findViewById(R.id.sensor_resolution_units);
		if (tv != null) {
			tv.setText(si.getUnits());
		}
		// Load sensor number of known values
		tv = (TextView) findViewById(R.id.sensor_num_values);
		if (tv != null) {
			tv.setText(((Integer) si.getNumLabels()).toString());
		}

		return;
	}

	// Prepare a data value view
	private View addDataValueChildView(int index) {
		TextView tv;
		LinearLayout ll;

		// Get known/unknown LinearLayout based on index
		if (index < si.getNumLabels()) {
			ll = known_ll;
		} else {
			ll = unknown_ll;
		}

		// Get a layout inflater from the system
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Get the layout for sensor values
		View v = vi.inflate(R.layout.sensor_values, ll, false);

		// Add this view to the parent layout
		ll.addView(v);

		// Get a reference to the data array text view
		tv = (TextView) v.findViewById(R.id.data_array);
		if (tv != null) {
			// Set the data array text
			String text = String.format(getString(R.string.sensor_data_format),
					index);
			tv.setText(text);
		}

		// Get a reference to the data label text view
		tv = (TextView) v.findViewById(R.id.data_label);
		if (tv != null) {
			// Set the label/units text
			String text = String.format(
					getString(R.string.sensor_value_format),
					si.getLabel(index), si.getUnits());
			tv.setText(text);
		}

		// Get a reference to the data value text view
		tv = (TextView) v.findViewById(R.id.data_value);
		if (tv != null) {
			// Set initial value
			tv.setText(R.string.no_data_tag);

			// Add this text view to an array for easy access later
			data_value_views.add(tv);
		}

		return v;
	}

	// load dynamic sensor view information for quick reference in real-time
	private void initRealTimeViews() {

		// Find real-time views and keep them for later reference
		delay_view = (TextView) findViewById(R.id.sensor_delay);
		event_count_view = (TextView) findViewById(R.id.sensor_events);
		timestamp_view = (TextView) findViewById(R.id.sensor_timestamp);
		accuracy_view = (TextView) findViewById(R.id.sensor_accuracy);

		// Reference the data values layout. This is a linear layout
		// with one or more additional linear layouts for each sensor value.
		known_ll = (LinearLayout) findViewById(R.id.known_data_values);
		unknown_ll = (LinearLayout) findViewById(R.id.unknown_data_values);

		// Reference the number of values shown and available text views.
		num_values_shown_view = (TextView) findViewById(R.id.sensor_num_values_shown);
		num_values_available_view = (TextView) findViewById(R.id.sensor_num_values_available);

		/*
		 * For each of the sensor's advertised values, initialize the views
		 * required to show each of the labels. At this point we only know the
		 * documented number of data labels for the sensor type provided.
		 */
		Integer num_known_values = si.getNumLabels();
		for (int i = 0; i < num_known_values; i++) {
			addDataValueChildView(i);
		}

		return;
	}

	// load button view information for quick reference in real-time
	private void loadButtonViews() {
		// Add a button listener to toggle the start/stop state
		start_stop_toggle_button = (ToggleButton) findViewById(R.id.start_stop_events_button);
		start_stop_toggle_button
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// Just re-apply all current settings
						applySettings();
					}
				});

		// Add a button listener to toggle the start/stop logging
		start_stop_logging_button = (ToggleButton) findViewById(R.id.start_stop_logging_button);
		start_stop_logging_button
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// Just re-apply all current settings
						applySettings();
					}
				});

		// Add a button listener to reset event counter
		final Button reset_button = (Button) findViewById(R.id.reset_events_button);
		reset_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks, reset counter
				event_counter = 0;

				// Update event count text view with event data
				event_count_view.setText(event_counter.toString());
			}
		});
	}

	// Helper functions to register and unregister a sensor listener
	private boolean registerSensorListener(int r, boolean notify) {

		// Unregister anything which was previously registered
		mgr.unregisterListener(this);

		// Register as a listener at the new set rate
		boolean result = mgr.registerListener(this, si.getSensor(), r);
		if (result == true) {
			if (notify == true) {
				// Construct notification next
				String notification_text = String.format(
						getString(R.string.sensor_rate_changed_tag),
						SensorInterface.delayToString(r));
				// Post notification
				Toast.makeText(getApplicationContext(), notification_text,
						Toast.LENGTH_SHORT).show();
			}
		}

		return result;
	}

	private void unregisterSensorListener() {
		// Unregister ourself from sensor stream
		mgr.unregisterListener(this);
	}

	// Provide the current sensor's rate setting this method is
	// defined by the SensorRateChangeDlg.SensorRateChangeListener interface
	@Override
	public int getRate() {
		return settings.getRate();
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the SensorRateChangeDlg.SensorRateChangeListener interface
	@Override
	public void onRateChange(int rate_picked) {
		// User touched the dialog's positive button

		// Remember setting at chosen rate
		settings.setRate(rate_picked);
		applySettings();

		return;
	}

	// Simple write member function to dump information about this sensor
	private void writeSensorInfo(int index) {
		// Write some info to the log about this sensor
		String text = String.format(getString(R.string.sensor_log), index,
				SensorInterface.getType(si.getSensor().getType()), si
						.getSensor().hashCode(), si.getSensor().toString());
		Log.d(TAG, text);
	}

	// Simple write member function to dump sensor data
	private void writeSensorData(int i, SensorEvent event) {
		// Check if logging has been enabled
		if (settings.getLogData() == true) {
			// Write some info to the log about this sensor
			String text = String.format(getString(R.string.sensor_data_format),
					i);
			text += String.format(getString(R.string.sensor_value_format),
					si.getLabel(i), si.getUnits());
			text += String.valueOf(event.values[i]) + "\n";
			Log.d(TAG, text);
		}
	}

	// Helper function to apply current settings
	private void applySettings() {

		// Update number of shown values
		updateShownValues();

		// Request orientation based on settings
		int orientation = settings.getOrientation();
		setRequestedOrientation(orientation);

		// Update text for displayed rate(delay)
		int rate = settings.getRate();
		delay_view.setText(SensorInterface.delayToString(rate));

		// Check start/stop button state and take action
		if (start_stop_toggle_button.isChecked() == true) {
			// Register sensor listener based on settings
			registerSensorListener(rate, false);
		} else {
			// Stop listening to sensor updates
			unregisterSensorListener();
		}

		// Check start/stop logging button state and take action
		if (start_stop_logging_button.isChecked() == true) {
			// Start logging
			logger.enable(true);
		} else {
			// Stop logging
			logger.enable(false);
		}
	}
}
