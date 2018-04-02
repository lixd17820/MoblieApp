package com.jwt.globalquery;

import java.util.List;


import com.jwt.main.R;



import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ZhcxOneRecordListAdapter extends ArrayAdapter<ZhcxQueryResultBean> {

	Activity context;

	public ZhcxOneRecordListAdapter(Activity _context, List<ZhcxQueryResultBean> objects) {
		super(_context, R.layout.two_col_list, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.two_col_list, null);
		}
		ZhcxQueryResultBean kv = getItem(position);
		TextView tv = (TextView) row.findViewById(R.id.TextView_left);
		TextView tv2 = (TextView) row.findViewById(R.id.TextView_right);
		tv.setText(kv.getComment());
		tv2.setText(kv.getValue());
		return row;

	}
}
