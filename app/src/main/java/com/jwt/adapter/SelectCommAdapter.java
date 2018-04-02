package com.jwt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.bean.TwoLineSelectBean;
import com.jwt.main.R;

import java.util.Collections;
import java.util.List;

public class SelectCommAdapter extends ArrayAdapter<SelectObjectBean> {

    private int resourceId;
    private List<SelectObjectBean> list;

    public SelectCommAdapter(Context context, int textViewResourceId,
                             List<SelectObjectBean> _list) {
        super(context, textViewResourceId, _list);
        resourceId = textViewResourceId;
        this.list = _list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(resourceId, parent, false);
        }
        SelectObjectBean content = getItem(position);
        if (content != null) {
            TextView tv = (TextView) row.findViewById(R.id.text1);
            tv.setText(content.getText());
            ImageView img = (ImageView) row.findViewById(R.id.image1);
            img.setImageResource(content.isSel() ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_blank_black_24dp);
        }
        return row;
    }

    public List<SelectObjectBean> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
