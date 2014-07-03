package com.kfodor.MySensors;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class MySensors extends FragmentActivity {

	private static final String TAG = "MySensors";

	// Sensor Manager
	private SensorManager mgr;

	// Create an array list of sensors
	private static final ArrayList<SensorListEntry> sensorArray = new ArrayList<SensorListEntry>();

	/** Called when the activity is first created. */
	// Called at the start of the full lifetime.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize activity.
		Log.d(TAG, "onCreate\n");

		// Inflate view
		setContentView(R.layout.main);

		// Acquire sensor manager
		mgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Load all available sensors
		loadSensors();
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

		Dialog dialog;

		// Find which dialog has been selected
		switch (id) {
		// Add dialogs via id here...
		default:
			dialog = null;
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

		// Show the dialog
		dialog.show();

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
		inflater.inflate(R.menu.my_sensors, menu);

		return true;
	}

	// This hook is called whenever an item in your options menu is selected.
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		int id = item.getItemId();

		// Find which menu item has been selected
		// Check for each known menu item
		switch (id) {

		// Reset rates...
		case (R.id.reset):
			// For each sensor, reset update rates to default
			for (SensorListEntry se : sensorArray) {

				// Reset sensor views to defaults
				se.reset();
			}

			// Construct notification next
			String text = String
					.format(getString(R.string.reset_all_sensor_views_to_defaults_tag));
			// Show notification
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();

			return true; // Handled menu item

			// About...
		case (R.id.about):
			// ... Perform menu handler actions ...

			// Create an instance of the dialog fragment and show it.
			DialogFragment about = new AboutDlg();
			about.show(getSupportFragmentManager(), "about");

			return true; // Handled menu item
		}

		// Return false if you have not handled the menu item.
		return false;
	}

	private boolean sensorExists(final Sensor sensor) {
		boolean exists = false;
		for (SensorListEntry sensorItem : sensorArray) {
			exists = sensor == sensorItem.getSensor();
			if (exists == true)
				break;
		}
		return exists;
	}

	private void loadSensors() {

		// Get references to UI widget (ListView) for sensors
		ListView sensorListView = (ListView) findViewById(R.id.sensors);

		// Create a Sensor adapter to bind the array to the list view
		final SensorAdapter sa;
		sa = new SensorAdapter(this, R.layout.sensor_item, sensorArray);

		// Bind array adaptor to the ListView
		sensorListView.setAdapter(sa);

		// Load array with each sensor available
		for (Sensor sensor : mgr.getSensorList(Sensor.TYPE_ALL)) {

			boolean exists = false;

			// Does this sensor exist in our list?
			exists = sensorExists(sensor);

			// Sensor does not exist, add it
			if (exists == false) {
				SensorListEntry sensorItem = new SensorListEntry(sensor);

				// Add this sensor to our list of sensors
				sensorArray.add(sensorItem);

				// Write some info to the log about this sensor
				String text = String.format(getString(R.string.sensor_log),
						SensorInterface.getType(sensor.getType()),
						sensor.getName(), sensor.getVendor(),
						sensor.getVersion());
				Log.d(TAG, text);
			}
		}

		// Load number of sensors
		TextView tv = (TextView) findViewById(R.id.number_of_sensors_found_value);
		if (tv != null) {
			Integer numSensors = sensorArray.size();
			tv.setText(numSensors.toString());
		}

		// Notifies the attached View that the underlying data has been
		// changed and it should refresh itself.
		sa.notifyDataSetChanged();

		// Set a listener to respond to list item clicks
		sensorListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Launch a new Activity to display the selected sensor
				Intent intent = new Intent(MySensors.this, SensorView.class);
				// Push the sensor position to the new Activity
				intent.putExtra("SensorPosition", position);
				// Start the activity
				startActivity(intent);
			}
		});

		return;
	}

	/**
	 * @return the sensorArray
	 */
	public static ArrayList<SensorListEntry> getSensorArray() {
		return sensorArray;
	}
}