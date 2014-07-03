package com.kfodor.MySensors;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SparseArrayAdaptor<E> extends BaseAdapter {

	private SparseArray<E> array;
	private final LayoutInflater mInflater;

	public SparseArrayAdaptor(Context context, SparseArray<E> array) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.array = array;
	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public E getItem(int position) {
		return array.valueAt(position);
	}

	@Override
	public long getItemId(int position) {
		return array.keyAt(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView result = (TextView) convertView;
		if (result == null) {
			result = (TextView) mInflater.inflate(
					android.R.layout.select_dialog_singlechoice, null);
		}
		result.setText(getItem(position).toString());
		return result;
	}
}
