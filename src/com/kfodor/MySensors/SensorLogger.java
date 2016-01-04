package com.kfodor.MySensors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import android.hardware.SensorEvent;
import android.os.Environment;
import android.util.Log;

public class SensorLogger {

	// Helpful sensor constants
	private static final String TAG = "SensorLogger";

	// Sensor Interface
	private SensorInterface si = null;

	// Logging enabled flag
	private boolean enabled = false;

	// Log file
	private File lf = null;

	// Log file output stream
	private OutputStream lfos = null;

	// Log file output stream writer
	private OutputStreamWriter lfosw = null;

	// Wrote header
	private boolean wrote_header = false;

	// Log file directory
	private File directory = null;

	// Log file extension
	private String ext = "";

	/*
	 * Constructor
	 */
	public SensorLogger(File dir, String ext, SensorInterface sensor) {
		si = sensor; // Grab sensor interface
		directory = dir;
		this.ext = ext;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		closeSensorLogFile();
	}

	/*
	 *  Create a unique file name based on date
	 */
	private String fname() {
		Calendar c = Calendar.getInstance();

		// Create a unique filename
		String fname = String.format(
				"MySensors_%s_%04d%02d%02d_%02d%02d%02d.%s", si.getType()
						.replaceAll(" ", "_"), c.get(Calendar.YEAR), c
						.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c
						.get(Calendar.SECOND), ext);
		return fname;
	}

	/*
	 *  Log file write sensor event header
	 */
	private void hdr(int num_values) {
		if (wrote_header == false) {
			try {
				String ln = "";
				ln += "timestamp,";
				ln += "accuracy,";
				// Loop for each available sensor value...
				for (int i = 0; i < num_values; i++) {
					ln += String.format("d%d", i);
					if (i < (num_values - 1)) {
						ln += ",";
					}
				}
				ln += System.getProperty("line.separator");
				lfosw.write(ln);
				wrote_header = true;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 *  Log file write sensor event method
	 */
	void write(SensorEvent event) {
		if ((lfosw != null) && (enabled == true)) {
			hdr(event.values.length);
			try {
				String ln = "";
				ln += event.timestamp + ",";
				ln += event.accuracy + ",";
				// Loop for each available sensor value...
				for (int i = 0; i < event.values.length; i++) {
					ln += event.values[i];
					if (i < (event.values.length - 1)) {
						ln += ",";
					}
				}
				ln += System.getProperty("line.separator");
				lfosw.write(ln);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Interface method to enable/disable logging
	 */
	void enable(boolean ctl) {
		if ((enabled == false) && (ctl == true)) {
			createSensorLogFile();
		} else if ((enabled == true) && (ctl == false)) {
			closeSensorLogFile();
		}
		enabled = ctl;
	}

	/*
	 * This is a simple method which creates a sensor list file on the device
	 * that contains a complete list of all sensors along with the device name
	 * and date the list was created.
	 */
	private void createSensorLogFile() {

		// Check if external storage is available
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// Create a path where we will place our private file on external
			// storage.
			lf = new File(directory, fname());

			try {
				lfos = new FileOutputStream(lf);
				if (lfos != null) {
					lfosw = new OutputStreamWriter(lfos);
					if (lfosw == null) {
						// Close output stream
						lfos.close();
						lfos = null;
					}
				}
			} catch (IOException e) {
				// Unable to create file, likely because external storage is
				// not currently mounted.
				Log.e(TAG, "Error writing " + lf, e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Method used to close all open files
	 */
	private void closeSensorLogFile() {
		if (lfosw != null) {
			try {
				lfosw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lfosw = null;
		}
		if (lfos != null) {
			try {
				lfos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lfos = null;
		}
		if (lf != null) {
			if (lf.length() == 0) {
				lf.delete();
			}
		}

		wrote_header = false;
	}

	/*
	 * Public method to allow deleting all files with a particular extension
	 */
	public static void deleteAllLogFiles(String pathname, String ext) {
		// Check if external storage is available
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// Create a path where we placed our private files on external
			// storage.
			File file = new File(pathname);
			File log_files[] = file.listFiles();
			for (int i = 0; i < log_files.length; i++) {
				if (log_files[i].isFile()
						&& log_files[i].getName().toLowerCase().endsWith(ext)) {
					log_files[i].delete();
				}
			}
		}
	}
}
