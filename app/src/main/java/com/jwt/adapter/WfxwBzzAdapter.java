package com.jwt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.jwt.bean.WfxwBzz;
import com.jwt.main.R;
import com.jwt.utils.GlobalMethod;

import java.util.List;

public class WfxwBzzAdapter extends ArrayAdapter<WfxwBzz> {

    private int resourceId;
    private List<WfxwBzz> list;

    public WfxwBzzAdapter(Context context, int textViewResourceId,
                          List<WfxwBzz> _list) {
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
        WfxwBzz content = getItem(position);
        if (content != null) {
            EditText wfxw = (EditText) row.findViewById(R.id.edit_wfxw);
            wfxw.setText(GlobalMethod.ifNull(content.getWfxw()));
            EditText bzz = (EditText) row.findViewById(R.id.edit_bzz);
            bzz.setText(GlobalMethod.ifNull(content.getBzz()));
            EditText scz = (EditText) row.findViewById(R.id.edit_scz);
            scz.setText(GlobalMethod.ifNull(content.getScz()));
            ImageView img = (ImageView) row.findViewById(R.id.image1);
            img.setImageResource(content.getSelect()>0 ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_blank_black_24dp);
        }
        return row;
    }

    public List<WfxwBzz> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
