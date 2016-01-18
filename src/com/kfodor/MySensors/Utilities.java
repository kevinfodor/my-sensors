package com.kfodor.MySensors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public final class Utilities {

	private static final String TAG = "Utilities";

	/*
	 * Retrieve a nicely formatted device name and version info
	 */
	static public String getDeviceInfo() {
		String deviceInfo;
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			deviceInfo = capitalize(model);
		} else {
			deviceInfo = capitalize(manufacturer) + " " + model;
		}
		return deviceInfo;
	}

	/*
	 * Capitalize a string
	 */
	static public String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	/*
	 * A method to get the storage path where we want to store our files
	 */
	static public String getStoragePath(Context ctx) {
		File f;
		String path = null;

		/*
		 * Android devices support a type of storage called external storage
		 * where apps can save files. It can be either removable like an SD card
		 * or non-removable in which case it is internal. Files in this storage
		 * are world readable which means other applications have access to them
		 * and the user can transfer them to their computer by connecting with a
		 * USB. Before writing to this volume we must check that it is available
		 * as it can become unavailable if the SD card is removed or mounted to
		 * the user’s computer.
		 */
		// Check if external storage is available
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// Make sure we're running on Froyo or higher to use this API
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				/*
				 * This returns a top level public external storage directory
				 * for shoving files of a particular type based on the argument
				 * passed. e.g. /storage/emulated/0/DCIM
				 */
				/*
				 * f = new File( Environment
				 * .getExternalStoragePublicDirectory(Environment
				 * .DIRECTORY_DCIM), getString(R.string.log_file_prefix));
				 */}
			/*
			 * Returns the absolute path to the directory on the primary
			 * shared/external storage device where the application can place
			 * persistent files it owns. e.g.
			 * /storage/emulated/0/Android/data/com.kfodor.MySensors/files
			 */
			// f = getExternalFilesDir(null);
			/*
			 * This returns the primary (top-level or root) external storage
			 * directory. e.g. /storage/emulated/0
			 */
			f = new File(Environment.getExternalStorageDirectory(),
					ctx.getString(R.string.log_file_prefix));
		} else {
			/*
			 * Returns the absolute path to the directory on the file system
			 * where files created with openFileOutput(String, int) are stored.
			 * e.g. /data/data/com.kfodor.MySensors/files
			 */
			f = ctx.getFilesDir();
		}

		// Create the directory
		if (f.mkdir() || f.isDirectory()) {

			// Retrieve path from the file
			path = f.getPath();

			// Write file path to the log
			Log.d(TAG, "Application Storage Path: " + path);
		} else {
			Log.e(TAG, "Failed to create application path at " + f.getPath());
		}

		return path;
	}

	/*
	 * A method to scan all the files in our directory
	 */
	static public void scanMedia(Context ctx, File f) {
		// Any file, that must be available in the media library
		// instantaneously, has to notify the media scanner.
		if (f != null) {
			String files[] = f.list(null);
			try {
				if (files == null) {
					// There is just one file
					files = new String[1];
					files[0] = f.getPath();
				} else {
					List<String> paths = new ArrayList<String>();

					// Prepend path to file names
					for (String file : files) {
						paths.add(String.format(
								"%1$s" + System.getProperty("file.separator")
										+ "%2$s", f.getAbsolutePath(), file));
					}

					// Convert files to an array of strings
					paths.toArray(files);
				}

				// Add to the media scanner
				MediaScannerConnection.scanFile(ctx, files, null, null);
			} catch (IllegalStateException e) {
				// Unable to scan files
				Log.e(TAG, "Unable to scan files on " + f.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	/*
	 * A method to scan all the files in our directory
	 */
	static public void scanMedia(Context ctx) {
		// Any file, that must be available in the media library
		// instantaneously, has to notify the media scanner.
		File f = new File(getStoragePath(ctx));
		scanMedia(ctx, f);
	}

	/*
	 * Method to remove a file
	 */
	static public void deleteFile(String path, String name) {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		File file = new File(path, name);
		if (file != null) {
			// Delete the file
			file.delete();
		}
	}

	/*
	 * Method to check if file exists
	 */
	static public boolean isFileExists(String path, String fname) {
		boolean exists = false;
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		File file = new File(path, fname);
		if (file != null) {
			exists = file.exists();
		}
		return exists;
	}
}
