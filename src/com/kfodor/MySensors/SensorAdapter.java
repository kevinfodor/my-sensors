package com.kfodor.MySensors;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorAdapter extends ArrayAdapter<SensorListEntry> {

	private ArrayList<SensorListEntry> items;
	private Context cntx;

	public SensorAdapter(Context context, int textViewResourceId,
			ArrayList<SensorListEntry> items) {
		super(context, textViewResourceId, items);

		// Retain information about the context and items in this array
		this.cntx = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			// Obtain a handle to the view
			LayoutInflater vi = (LayoutInflater) cntx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.sensor_item, parent, false);
		}

		// Determine what sensor we are trying to show
		Sensor sensor = items.get(position).getSensor();
		if (sensor != null) {

			// Populate sensor's icon
			ImageView iv = (ImageView) v.findViewById(R.id.sensor_icon);
			if (iv != null) {
				int sensor_type = sensor.getType();
				iv.setImageResource(SensorInterface.getIcon(sensor_type));
			}
			// Populate sensor's name
			TextView tv = (TextView) v.findViewById(R.id.sensor_name);
			if (tv != null) {
				tv.setText(sensor.getName());
			}
			// Populate sensor's vendor
			tv = (TextView) v.findViewById(R.id.sensor_vendor);
			if (tv != null) {
				tv.setText(sensor.getVendor());
			}
		}
		return v;
	}
}
