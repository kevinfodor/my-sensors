package com.kfodor.MySensors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class AboutDlg extends DialogFragment implements OnClickListener,
		OnCancelListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		Dialog dialog;

		// Build this alert dialog box
		// Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Inflate the about layout and obtain a reference to the view.
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_dialog,
				(ViewGroup) getActivity().findViewById(R.id.layout_root));
		builder.setView(layout);

		// Add 'ok' button
		builder.setPositiveButton("Ok", this);

		// Sets the callback that will be called if the dialog is canceled.
		builder.setOnCancelListener(this);
		builder.setCancelable(true);

		// Get the AlertDialog from create()
		dialog = builder.create();
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		return;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		dialog.dismiss();
		return;
	}
}