package com.kfodor.MySensors;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.kfodor.MySensors.MySensors;

public class SensorView extends FragmentActivity implements
		SensorEventListener, SensorRateChangeDlg.SensorRateChangeListener {

	private static final String TAG = "SensorView";
	private static final float NS2S = 1.0f / 1000000000.0f;

	private SensorListEntry se; // The sensor list entry
	private SensorInterface si; // The sensor interface to this sensor
	private SensorManager mgr; // Sensor Manager

	// Dynamic (real-time views)
	private TextView delay_view;
	private TextView event_count_view;
	private TextView timestamp_view;
	private TextView accuracy_view;
	private TextView num_values_reported_view;
	private TextView num_values_shown_view;

	// An array to hold text views with data entry values
	private ArrayList<TextView> data_value_text_views;

	// Sensor event counter
	private Integer event_counter = 0;

	// Sensor number of reported values
	private Integer num_reported_values = 0;

	// Flag to indicate if the user wants to see all reported values or
	// only the known (documented) ones.
	private Boolean show_all_reported_values = false;

	/** Called when the activity is first created. */
	// Called at the start of the full lifetime.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize activity.
		Log.d(TAG, "onCreate\n");

		// Inflate view(s)
		setContentView(R.layout.sensor_view);

		// Extract calling activity provided 'extras'
		// which will help us determine which sensor we
		// are going to use in this activity.
		prepareSensor();

		// Initialize basic (static) sensor view
		loadStaticView();

		// Initialize real-time sensor view
		loadRealTimeView();

		// Add a button listener to change the event update rate
		final Button rb = (Button) findViewById(R.id.RateButton);
		rb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks

				// Create an instance of the dialog fragment and show it.
				DialogFragment rate = new SensorRateChangeDlg();
				rate.show(getSupportFragmentManager(), "rate");
			}
		});

		// Fix issue #1 which will keep the screen from timing out
		// or going off while viewing sensor data.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Write some info to the log about this sensor
		String text = String.format(getString(R.string.sensor_log),
				SensorInterface.getType(si.getSensor().getType()), si
						.getSensor().getName(), si.getSensor().getVendor(), si
						.getSensor().getVersion());
		Log.d(TAG, text);
	}

	// Called after onCreate has finished, use to restore UI state
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		Log.d(TAG, "onRestoreInstanceState\n");
	}

	// Called before subsequent visible lifetimes
	// for an activity process.
	@Override
	public void onRestart() {
		super.onRestart();
		// Load changes knowing that the activity has already
		// been visible within this process.
		Log.d(TAG, "onRestart\n");
	}

	// Called at the start of the visible lifetime.
	@Override
	public void onStart() {
		super.onStart();
		// Apply any required UI change now that the Activity is visible.
		Log.d(TAG, "onStart\n");
	}

	// Called at the start of the active lifetime.
	@Override
	public void onResume() {
		super.onResume();
		// Resume any paused UI updates, threads, or processes required
		// by the activity but suspended when it was inactive.
		Log.d(TAG, "onResume\n");

		// Register sensor listener at previously set rate until changed
		registerSensorListener(se.getRate(), false);
	}

	// Called to save UI state changes at the
	// end of the active life cycle.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		super.onSaveInstanceState(savedInstanceState);
		Log.d(TAG, "onSaveInstanceState\n");
	}

	// Called at the end of the active lifetime.
	@Override
	public void onPause() {
		// Suspend UI updates, threads, or CPU intensive processes
		// that don’t need to be updated when the Activity isn’t
		// the active foreground activity.
		super.onPause();
		Log.d(TAG, "onPause\n");

		// Unregister ourself from sensor stream
		mgr.unregisterListener(this);
	}

	// Called at the end of the visible lifetime.
	@Override
	public void onStop() {
		// Suspend remaining UI updates, threads, or processing
		// that aren’t required when the Activity isn’t visible.
		// Persist all edits or state changes
		// as after this call the process is likely to be killed.
		super.onStop();
		Log.d(TAG, "onStop\n");
	}

	// Called at the end of the full lifetime.
	@Override
	public void onDestroy() {
		// Clean up any resources including ending threads,
		// closing database connections etc.
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// newConfig.orientation =
		// getResources().getConfiguration().orientation;
		super.onConfigurationChanged(newConfig);

		// Handle any configuration changes here...

	}

	// This hook is called whenever an item in your options menu is selected.
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		int id = item.getItemId();

		// Find which menu item has been selected
		// Check for each known menu item
		switch (id) {

		// Change Event Rate...
		case (R.id.change_event_rate): {
			// Create an instance of the dialog fragment and show it.
			DialogFragment rate = new SensorRateChangeDlg();
			rate.show(getSupportFragmentManager(), "rate");

			return true; // handled
		}
		// Show all reports...
		case (R.id.show_all_reports): {

			// Toggle checked state
			boolean isChecked = !item.isChecked();

			// Handle check/unchecked setting(toggled).
			item.setChecked(isChecked);
			show_all_reported_values = isChecked;

			// Based on the check-box state, make data values visible/gone
			int visibility = View.GONE;
			if (show_all_reported_values == true) {
				// Make all unknown data values visible. This means
				// all reported values will be shown.
				visibility = View.VISIBLE;
				num_values_shown_view.setText(num_reported_values.toString());
			} else {
				// Make all unknown data values gone
				visibility = View.GONE;
				Integer num_shown_values = si.getNumLabels();
				num_values_shown_view.setText(num_shown_values.toString());
			}

			// Reference the data values layout. This is a linear layout
			// with one or more additional linear layouts for each sensor value.
			LinearLayout ll = (LinearLayout) findViewById(R.id.data_values);

			// Set visibility for unknown data values (visible/gone)
			for (int i = si.getNumLabels(); i < num_reported_values; i++) {
				View v = ll.getChildAt(i);
				if (v != null) {
					v.setVisibility(visibility);
				}
			}

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
				accuracy);
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

			// Update the number of values reported and text view to show it.
			num_reported_values = event.values.length;
			num_values_reported_view.setText(num_reported_values.toString());

			// Loop for each reported sensor value...
			for (int i = 0; i < num_reported_values; i++) {

				TextView tv;

				// Check if we need a new data values text view for this value.
				// This would happen if the sensor reports more values
				// than what was documented or known previously.
				if (i >= data_value_text_views.size()) {
					int visibility = View.GONE;

					// Check if we want to see these
					if (show_all_reported_values == true) {
						visibility = View.VISIBLE;
					}

					// Add a new data value view (with unknown label)
					addDataValueView(i, visibility);
				}

				// Get text view for this sensor's data value
				tv = data_value_text_views.get(i);
				if (tv != null) {
					// Update the text value
					tv.setText(String.valueOf(event.values[i]));
				}

				// Write some info to the log about this sensor
				String text = String.format(
						getString(R.string.sensor_data_format), i);
				text += String.format(getString(R.string.sensor_value_format),
						si.getLabel(i), si.getUnits());
				text += String.valueOf(event.values[i]) + "\n";
				Log.d(TAG, text);
			}
		}
	}

	// Simple method to retrieve the sensor we are viewing
	private void prepareSensor() {

		// The parent activity passed to us the "sensor position"
		// within the sensor list which this activity will handle.
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			// Bail!
			finish();
		}

		// Get sensor position in list
		int position = extras.getInt("SensorPosition");

		// Acquire Sensor Manager
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Extract the sensor at the given position in the list
		se = MySensors.getSensorArray().get(position);

		// Now that we have the sensor, create a sensor interface object
		// and assign it to the interface.
		si = new SensorInterface(se.getSensor());

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
	private View addDataValueView(int index, int visibility) {
		TextView tv;

		// Reference the data values layout. This is a linear layout
		// with one or more additional linear layouts for each sensor value.
		LinearLayout ll = (LinearLayout) findViewById(R.id.data_values);

		// Get a layout inflater from the system
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Get the layout for sensor values
		View v = vi.inflate(R.layout.sensor_values, ll, false);

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

			// Add this view to the array of text views for data values
			data_value_text_views.add(tv);
		}

		// Set specified visibility
		v.setVisibility(visibility);

		// Add this view the parent layout
		ll.addView(v);

		return v;
	}

	// load and configure dynamic sensor information
	private void loadRealTimeView() {

		// Find real-time views and keep them for later reference
		delay_view = (TextView) findViewById(R.id.sensor_delay);
		event_count_view = (TextView) findViewById(R.id.sensor_events);
		timestamp_view = (TextView) findViewById(R.id.sensor_timestamp);
		accuracy_view = (TextView) findViewById(R.id.sensor_accuracy);
		num_values_reported_view = (TextView) findViewById(R.id.sensor_num_values_reported);
		num_values_shown_view = (TextView) findViewById(R.id.sensor_num_values_shown);

		// Data values are an array of text views (one for each value reported)
		// I have noticed that sometimes Android sensors will send (indicate)
		// that they have more values available than they have documented.
		// So one must be careful about sizes, lengths, etc.
		data_value_text_views = new ArrayList<TextView>();

		// For each of the sensor's advertised values, initialize the views
		// required to show each of the labels. At this point we only know
		// the documented number of data labels for the sensor type provided.
		Integer num_shown_values = si.getNumLabels();
		for (int i = 0; i < num_shown_values; i++) {
			addDataValueView(i, View.VISIBLE);
		}

		// Set the number of shown values (only known) by default
		num_values_shown_view.setText(num_shown_values.toString());

		return;
	}

	// Helper function to register and unregister a sensor listener
	private void registerSensorListener(int r, boolean notify) {

		// Check if we are changing the rate, or this is being
		// done for the first time (with no notification)
		if ((r != se.getRate()) || (notify == false)) {
			// Unregister anything which was previously registered
			mgr.unregisterListener(SensorView.this);

			// Register as a listener at the new set rate
			boolean result = mgr.registerListener(this, si.getSensor(), r);
			if (result == true) {
				se.setRate(r);
				delay_view.setText(SensorInterface.delayToString(r));
				if (notify == true) {
					// Construct notification next
					String notification_text = String.format(
							getString(R.string.sensor_rate_changed_tag),
							SensorInterface.delayToString(r));
					Toast.makeText(getApplicationContext(), notification_text,
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		return;
	}

	// Provide the current sensor's rate setting this method is
	// defined by the SensorRateChangeDlg.SensorRateChangeListener interface
	@Override
	public int getRate() {
		return se.getRate();
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the SensorRateChangeDlg.SensorRateChangeListener interface
	@Override
	public void onRateChange(int rate_picked) {
		// User touched the dialog's positive button
		registerSensorListener(rate_picked, true);
		return;
	}

}
