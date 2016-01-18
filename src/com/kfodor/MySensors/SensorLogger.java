package com.kfodor.MySensors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

import android.hardware.SensorEvent;
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
	private String dir = "";

	// Log file prefix
	private String prefix = "";

	// Log file extension
	private String ext = "";

	/*
	 * Constructor
	 */
	public SensorLogger(String dir, String prefix, String ext,
			SensorInterface sensor) {
		si = sensor; // Grab sensor interface
		this.dir = dir;
		this.prefix = prefix;
		this.ext = ext;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		// Stop logging (if we are logging)
		enable(false);
	}

	/*
	 * Return the current log file handle
	 */
	public File getFile() {
		return lf;
	}

	/*
	 * Create a unique file name based on date
	 */
	private String fname() {
		Calendar c = Calendar.getInstance();

		// Create a unique filename
		String fname = String.format(Locale.US,
				"%s_%s_%04d%02d%02d_%02d%02d%02d%s", prefix, si.getType()
						.replaceAll(" ", "_"), c.get(Calendar.YEAR), c
						.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c
						.get(Calendar.SECOND), ext);
		return fname;
	}

	/*
	 * Log file write sensor event header
	 */
	private void writeHdr(int num_values) {
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Log file write sensor event method
	 */
	void writeEvent(SensorEvent event) {
		if ((lfosw != null) && (enabled == true)) {
			writeHdr(event.values.length);
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Interface method to enable/disable logging
	 */
	void enable(boolean ctl) {
		if ((enabled == false) && (ctl == true)) {
			openSensorLogFile();
		} else if ((enabled == true) && (ctl == false)) {
			closeSensorLogFile();
		}
		enabled = ctl;
	}

	/*
	 * This method is used to open a sensor log file.
	 */
	private void openSensorLogFile() {

		// Create a file where we will place our private
		// file in storage.
		lf = new File(dir, fname());

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
			// Unable to create file, likely because chosen storage
			// path is not currently mounted.
			Log.e(TAG, "Error writing " + lf, e);
			e.printStackTrace();
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
}
