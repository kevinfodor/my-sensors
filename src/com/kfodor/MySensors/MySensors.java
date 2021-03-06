package com.kfodor.MySensors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class MySensors extends FragmentActivity {

	private static final String TAG = "MySensors";

	// Create an array list of sensors. This array is used to display
	// an annotated list of all available sensors on the device.
	private static ArrayList<SensorListEntry> sensorArray = new ArrayList<SensorListEntry>();

	// A place to store application files (sensor list and logs)
	private String app_directory = null;

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

		// Inflate view
		setContentView(R.layout.main);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// For the main activity, make sure the app icon in the action bar
			// does not behave as a button
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(false);
		}

		// Load all available sensors
		loadSensors();

		// Initialize directory to use
		app_directory = Utilities.getStoragePath(this);

		// Write text file with sensor list if not already present
		if (Utilities.isFileExists(app_directory,
				getString(R.string.sensor_list_fname)) == false) {

			// Create the list file
			createSensorListFile();
		}
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

		// Apply any required UI change now that the Activity is visible.

		// Get references to UI widget (ListView) for sensors
		ListView sensorListView = (ListView) findViewById(R.id.sensors);

		// Create a Sensor adapter to bind the array to the list view
		final SensorAdapter sa;
		sa = new SensorAdapter(this, R.layout.sensor_item, sensorArray);

		// Bind array adaptor to the ListView
		sensorListView.setAdapter(sa);

		// Load number of sensors into view so it can be shown
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

				// Push the sensor index to the new Activity
				intent.putExtra(SensorView.SENSOR_INDEX_EXTRA, position);

				// Start the activity
				startActivity(intent);
			}
		});
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
	}

	/*
	 * Called at the end of the active lifetime. Suspend UI updates, threads, or
	 * CPU intensive processes that don�t need to be updated when the Activity
	 * isn�t the active foreground activity.
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause\n");
	}

	/*
	 * Called at the end of the visible lifetime. Suspend remaining UI updates,
	 * threads, or processing that aren�t required when the Activity isn�t
	 * visible. Persist all edits or state changes as after this call the
	 * process is likely to be killed. (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop\n");
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
		case (R.id.reset): {
			// For each sensor, reset settings to default
			for (SensorListEntry se : sensorArray) {

				// Create a unique name for these setting
				String name = SensorView.getName(getApplicationContext(),
						se.getIndex());
				SharedPreferences preferences = getSharedPreferences(name,
						MODE_PRIVATE);

				// Retrieve existing set of settings
				SensorViewSettings settings = new SensorViewSettings(
						preferences);

				// Reset setting
				settings.reset();

				// Save settings
				settings.save(preferences);
			}

			// Construct notification next
			String text = String
					.format(getString(R.string.reset_all_sensor_views_to_defaults_tag));
			// Show notification
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();

			return true; // Handled menu item
		}

		// Rewrite Sensor List
		case (R.id.rewrite): {
			// Delete the existing file (if any)
			Utilities.deleteFile(app_directory,
					getString(R.string.sensor_list_fname));

			// Create a new file
			createSensorListFile();

			return true;
		}

		// Delete all Sensor Logs
		case (R.id.delete_all_sensor_log_files): {

			File f = null;
			File[] paths;

			// Delete all log files
			try {
				f = new File(app_directory);

				// create new filename filter
				FilenameFilter fileNameFilter = new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (name.lastIndexOf('.') > 0) {
							// get last index for '.' char
							int lastIndex = name.lastIndexOf('.');

							// get extension
							String str = name.substring(lastIndex);

							// match path name extension
							if (str.equals(getString(R.string.sensor_log_file_ext))) {
								return true;
							}
						}
						return false;
					}
				};

				// returns pathnames for files and directory
				paths = f.listFiles(fileNameFilter);

				// for each pathname in pathname array
				for (File path : paths) {
					// Delete the file
					path.delete();
					// Update media scanner
					Utilities.scanMedia(this, path);
				}
			} catch (Exception e) {
				// if any error occurs
				e.printStackTrace();
			}

			// Construct notification next
			String text = String
					.format(getString(R.string.delete_all_sensor_logs_defaults_tag));
			// Show notification
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();

			return true;
		}

		// Show all Sensor Logs
		case (R.id.show_log_files): {

			// Create a uri for the storage directory
			Uri uri = Uri.parse(app_directory);

			// Create a new intent to launch the folder browser
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(uri, "resource/folder");

			if (i.resolveActivityInfo(getPackageManager(), 0) != null) {
				startActivity(i);
			} else {
				// if you reach this place, it means there is no any file
				// explorer app installed on your device

				String msg = new String(String.format(
						getString(R.string.no_folder_browser_tag),
						getString(R.string.es_file_explorer_url),
						getString(R.string.astro_explorer_url)));

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(Html.fromHtml(msg)).setCancelable(false)
						.setPositiveButton(android.R.string.ok, null)
						.setIcon(android.R.drawable.ic_dialog_info);
				AlertDialog alert = builder.create();

				alert.show();

				((TextView) alert.findViewById(android.R.id.message))
						.setMovementMethod(LinkMovementMethod.getInstance());

			}

			return true;
		}

		// Rescan sensor list and all logs
		case (R.id.rescan): {

			// Force a media rescan to make any new files visible as media
			Utilities.scanMedia(this);

			/*
			 * MediaScannerConnection.scanFile(getApplicationContext(), new
			 * String[] { app_directory }, null, null);
			 */

			// Show notification
			String text = String.format(getString(R.string.rescan_tag));
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();

			return true;
		}

		// About...
		case (R.id.about): {
			// ... Perform menu handler actions ...

			// Create an instance of the dialog fragment and show it.
			DialogFragment about = new AboutDlg();
			about.show(getSupportFragmentManager(), "about");

			return true; // Handled menu item
		}

		}
		// Return false if you have not handled the menu item.
		return false;
	}

	// A helper function to find a sensor using the manager and an index
	public final static Sensor findSensor(SensorManager mgr, int index) {
		Sensor sensor;

		// Find a sensor at this index...
		try {
			sensor = mgr.getSensorList(Sensor.TYPE_ALL).get(index);
		} catch (IndexOutOfBoundsException e) {
			// The entry is not available, throw exception
			throw new IndexOutOfBoundsException(TAG
					+ ": cannot get sensor at index." + String.valueOf(index));
		}

		return sensor;
	}

	// A helper function to determine if a sensor is in our list
	private boolean sensorExists(final Sensor sensor) {
		boolean exists = false;
		for (SensorListEntry sensorItem : sensorArray) {
			exists = (sensor == sensorItem.getSensor());
			if (exists == true) {
				break;
			}
		}

		return exists;
	}

	// A helper function to load each sensor into the list
	private void loadSensors() {

		// Acquire sensor manager
		final SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Iterate over available sensors...
		int sensor_index = 0;

		// Load array with each sensor available
		for (Sensor sensor : mgr.getSensorList(Sensor.TYPE_ALL)) {

			boolean exists;

			// Create some textual info for the log about this sensor
			String text = String.format(getString(R.string.sensor_log),
					sensor_index, SensorInterface.getType(sensor.getType()),
					sensor.hashCode(), sensor.toString());

			// Does this sensor exist in our list?
			exists = sensorExists(sensor);

			// Sensor does not exist, add it
			if (exists == false) {

				// Create a new sensor list element
				SensorListEntry sensor_entry = new SensorListEntry(mgr,
						sensor_index);

				// Add this sensor to our list of sensors
				sensorArray.add(sensor_entry);

				text += "Added";

			} else {

				text += "Exists(Skipped)";
			}

			// Write sensor text to the log
			Log.d(TAG, text);

			// Increment sensor index
			sensor_index++;
		}

		return;
	}

	/*
	 * This is a simple method which creates a sensor list file on the device
	 * that contains a complete list of all sensors along with the device name
	 * and date the list was created.
	 */
	private void createSensorListFile() {

		// Create a path where we will place our sensor list file.
		File file = new File(app_directory,
				getString(R.string.sensor_list_fname));

		try {
			OutputStream os = new FileOutputStream(file);
			if (os != null) {
				OutputStreamWriter osw = new OutputStreamWriter(os);

				if (os != null) {
					// Write app info
					osw.write(getString(R.string.app_name) + " "
							+ getString(R.string.app_version));
					osw.write(System.getProperty("line.separator"));
					// Write device model and date
					osw.write("Device: " + Utilities.getDeviceInfo());
					osw.write(System.getProperty("line.separator"));
					String version = System.getProperty("os.version") + "("
							+ android.os.Build.VERSION.INCREMENTAL + ")";
					osw.write("Version: " + version);
					osw.write(System.getProperty("line.separator"));
					java.text.DateFormat df = SimpleDateFormat
							.getDateTimeInstance();
					String date = df.format(Calendar.getInstance().getTime());
					osw.write("Date: " + date);
					osw.write(System.getProperty("line.separator"));
					// Write sensor list
					for (SensorListEntry sensorItem : sensorArray) {
						Sensor sensor = sensorItem.getSensor();
						String text = String.format(
								getString(R.string.sensor_log),
								sensorItem.getIndex() + 1,
								SensorInterface.getType(sensor.getType()),
								sensor.hashCode(), sensor.toString());
						osw.write(text);
					}
					// Close formatted writer
					osw.close();
				}
				// Close output stream
				os.close();
			}

			// Add to media connection (scan)
			Utilities.scanMedia(this, file);

			// Construct notification next
			String text = String.format(
					getString(R.string.sensor_list_created),
					file.getAbsolutePath());
			// Show notification
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
					.show();

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.e(TAG, "Error writing " + file, e);
			e.printStackTrace();
		}
	}

}
