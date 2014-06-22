package com.kfodor.MySensors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ListAdapter;

import com.kfodor.MySensors.SparseArrayAdaptor;

public class SensorRateChangeDlg extends DialogFragment implements
		OnClickListener, OnCancelListener {

	private int delay_picked;

	// The activity that creates an instance of this dialog fragment must
	// implement this interface in order to receive event callbacks.
	// Each method passes the DialogFragment in case the host needs to
	// query it.
	public interface SensorRateChangeListener {
		public int getRate();

		public void onRateChange(int delay_picked);
	}

	// Use this instance of the interface to deliver action events
	SensorRateChangeListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		Dialog dialog;

		// Build this alert dialog box
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Set dialog box title
		builder.setTitle(getString(R.string.rate_change_choice_tag));

		// Retrieve the current rate setting.
		int delay = mListener.getRate();

		ListAdapter delay_choices = new SparseArrayAdaptor<String>(
				builder.getContext(), SensorInterface.delay);

		builder.setSingleChoiceItems(delay_choices, delay,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						delay_picked = item;
					}
				});

		builder.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onRateChange(delay_picked);
					}
				});

		// Sets the callback that will be called if the dialog is canceled.
		builder.setNegativeButton(getString(android.R.string.cancel), this);
		builder.setCancelable(true);
		builder.setOnCancelListener(this);

		// Get the AlertDialog from create()
		dialog = builder.create();
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
		return;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		dialog.dismiss();
		return;
	}

	// Override the Fragment.onAttach() method to instantiate the
	// SensorRateChangeListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the SensorRateChangeListener so we can send events to
			// the host
			mListener = (SensorRateChangeListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement SensorRateChangeListener");
		}
	}
}