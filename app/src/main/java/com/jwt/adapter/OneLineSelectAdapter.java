package com.jwt.adapter;

import com.jwt.bean.TwoLineSelectBean;
import com.jwt.update.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OneLineSelectAdapter extends ArrayAdapter<TwoLineSelectBean> {

    private int resourceId;
    private List<TwoLineSelectBean> list;

    public OneLineSelectAdapter(Context context, int textViewResourceId,
                                List<TwoLineSelectBean> _list) {
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
        TwoLineSelectBean content = getItem(position);
        if (content != null) {
            TextView tv = (TextView) row.findViewById(R.id.text1);
            tv.setText(content.getText1());
            ImageView img = (ImageView) row.findViewById(R.id.image1);
            img.setImageResource(content.isSelect() ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_blank_black_24dp);
        }
        return row;
    }

    public List<TwoLineSelectBean> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
