package com.kfodor.MySensors;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class SensorView extends FragmentActivity implements
		SensorEventListener, SensorRateChangeDlg.SensorRateChangeListener {

	private static final String TAG = "SensorView";

	private SensorInterface si; // The sensor interface to this sensor
	private SensorManager mgr; // Sensor Manager

	// Dynamic (real-time views)
	TextView delay_view;
	TextView event_count_view;
	TextView timestamp_view;
	TextView accuracy_view;
	ArrayList<TextView> data_value_views;
	/*
	 * there may be one or more data values for each sensor, so here we declare
	 * an array list of text views to hold each data value and its label.
	 */

	private int event_counter = 0;
	private int rate = -1;

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
		getSensor();

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

		// Write some info to the log about this sensor
		String text = String.format(getString(R.string.sensor_log),
				SensorInterface.getType(si.sensor().getType()), si.sensor()
						.getName(), si.sensor().getVendor(), si.sensor()
						.getVersion())
				+ "\n";
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

		// Register sensor listener at normal rate until changed
		registerSensorListener(SensorManager.SENSOR_DELAY_NORMAL, false);
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

		// Handle any configuration changes

	}

	// This hook is called whenever an item in your options menu is selected.
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		int id = item.getItemId();

		// Find which menu item has been selected
		// Check for each known menu item
		switch (id) {

		// Search...
		case (R.id.web_search):

			// Do a simple web search for the sensor
			String url = "http://www.google.com/search?hl=en&q="
					+ si.sensor().getVendor() + "+" + si.sensor().getName()
					+ "+" + si.getType()
					+ "&btnG=Search&aq=f&aqi=&aql=&oq=&gs_rfai=";

			// Create a new intent to launch the web search
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));

			// Start the web search
			startActivity(i);

			return true;

			// About...
		case (R.id.about):
			// ... Perform menu handler actions ...

			// Create an instance of the dialog fragment and show it.
			DialogFragment about = new AboutDlg();
			about.show(getSupportFragmentManager(), "about");

			return true; // handled
		}

		// Return false if you have not handled the menu item.
		return false;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
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
		if (event.sensor.getType() == this.si.sensor().getType()) {

			// Increment event counter
			event_counter++;

			for (int i = 0; (i < event.values.length)
					&& (i < si.getNumValues()); i++) {

				// Update event count text view
				event_count_view.setText(((Integer) event_counter).toString());

				// Convert time stamp to seconds and update timestamp view
				Float ts = (float) event.timestamp / 1000000000;
				timestamp_view.setText(ts.toString());

				// Update accuracy text view
				accuracy_view.setText(SensorInterface
						.accuracyToString((event.accuracy)));

				// Update data values text view
				TextView tv = data_value_views.get(i);
				tv.setText(String.valueOf(event.values[i]));

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
	private void getSensor() {

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

		// Get the list of all sensors from the sensor manager
		List<Sensor> sensorList = mgr.getSensorList(Sensor.TYPE_ALL);

		// Extract the sensor at the given position in the list
		Sensor s = sensorList.get(position);

		// Now that we have the sensor, create a sensor interface object
		// and assign it to the interface.
		si = new SensorInterface(s);

		return;
	}

	// method to load in the static sensor information
	private void loadStaticView() {

		ImageView iv;
		TextView tv;

		// Load sensor icon
		iv = (ImageView) findViewById(R.id.sensor_icon);
		if (iv != null) {
			iv.setImageResource(si.getIcon());
		}
		// Load sensor name
		tv = (TextView) findViewById(R.id.sensor_name);
		if (tv != null) {
			tv.setText(si.sensor().getName());
		}
		// Load sensor vendor
		tv = (TextView) findViewById(R.id.sensor_vendor);
		if (tv != null) {
			tv.setText(si.sensor().getVendor());
		}
		// Load sensor version
		tv = (TextView) findViewById(R.id.sensor_version);
		if (tv != null) {
			tv.setText(((Integer) si.sensor().getVersion()).toString());
		}
		// Load sensor type
		tv = (TextView) findViewById(R.id.sensor_type);
		if (tv != null) {
			tv.setText(si.getType() + " ("
					+ ((Integer) si.sensor().getType()).toString() + ")");
		}
		// Load sensor power
		tv = (TextView) findViewById(R.id.sensor_power);
		if (tv != null) {
			tv.setText(((Float) si.sensor().getPower()).toString());
		}
		// Load sensor range
		tv = (TextView) findViewById(R.id.sensor_range);
		if (tv != null) {
			tv.setText(((Float) si.sensor().getMaximumRange()).toString());
		}
		// Load sensor range units
		tv = (TextView) findViewById(R.id.sensor_range_units);
		if (tv != null) {
			tv.setText(si.getUnits());
		}
		// Load sensor resolution
		tv = (TextView) findViewById(R.id.sensor_resolution);
		if (tv != null) {
			tv.setText(((Float) si.sensor().getResolution()).toString());
		}
		// Load sensor range units
		tv = (TextView) findViewById(R.id.sensor_resolution_units);
		if (tv != null) {
			tv.setText(si.getUnits());
		}

		return;
	}

	// load and configure dynamic sensor information
	private void loadRealTimeView() {

		// Find views and keep them for later reference
		delay_view = (TextView) findViewById(R.id.sensor_delay);
		event_count_view = (TextView) findViewById(R.id.sensor_events);
		timestamp_view = (TextView) findViewById(R.id.sensor_timestamp);
		accuracy_view = (TextView) findViewById(R.id.sensor_accuracy);
		accuracy_view.setText("Unknown");

		data_value_views = new ArrayList<TextView>();

		// reference the data values layout. This is a linear layout
		// with one or more additional linear layouts for each sensor value.
		LinearLayout ll = (LinearLayout) findViewById(R.id.data_values);

		// For each of the sensor's values, initialize the views
		// required to show each of them.
		for (int i = 0; i < si.getNumValues(); i++) {

			// Get a layout inflater from the system
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Get the layout for sensor values
			View v = vi.inflate(R.layout.sensor_values, ll, false);

			TextView tv;

			// Get a reference to the data array text view
			tv = (TextView) v.findViewById(R.id.data_array);
			if (tv != null) {
				// Set the data array text
				String text = String.format(
						getString(R.string.sensor_data_format), i);
				tv.setText(text);
				// tv.setText("d[" + i + "] = ");
			}
			// Get a reference to the data label text view
			tv = (TextView) v.findViewById(R.id.data_label);
			if (tv != null) {
				// Set the label/units text
				String text = String.format(
						getString(R.string.sensor_value_format),
						si.getLabel(i), si.getUnits());
				tv.setText(text);
				// tv.setText(si.getLabel(i) + " (" + si.getUnits() + ") = ");
			}
			// Get a reference to the data value text view
			tv = (TextView) v.findViewById(R.id.data_value);
			if (tv != null) {
				// Set initial value
				tv.setText("No data!");
				// Add this view to the array of text views for data values
				data_value_views.add(tv);
			}
			ll.addView(v); // Add this view the parent layout

		}

		return;
	}

	// Helper function to register and unregister a sensor listener
	private void registerSensorListener(int r, boolean notify) {

		// Check if we are changing the rate
		if (r != rate) {
			// Unregister anything which was previously registered
			mgr.unregisterListener(SensorView.this);

			// Register as a listener at set rate
			boolean result = mgr.registerListener(this, si.sensor(), r);
			if (result == true) {
				delay_view.setText(SensorInterface.delayToString(r));
				if (notify == true) {
					Toast.makeText(
							getApplicationContext(),
							"Changed event rate to "
									+ SensorInterface.delayToString(r),
							Toast.LENGTH_SHORT).show();
				}
				rate = r;
			}
		}

		return;
	}

	// Provide the current sensor's rate setting this method is
	// defined by the SensorRateChangeDlg.SensorRateChangeListener interface
	@Override
	public int getRate() {
		return rate;
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
