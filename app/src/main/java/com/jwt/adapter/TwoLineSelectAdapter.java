package com.jwt.adapter;

import java.util.List;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TwoLineListItem;

import com.jwt.bean.TwoLineSelectBean;
import com.jwt.main.R;

public class TwoLineSelectAdapter extends ArrayAdapter<TwoLineSelectBean> {

    private int resourceId;
    private boolean isImage;
    private Context context;

    public TwoLineSelectAdapter(Context context, int textViewResourceId,
                                List<TwoLineSelectBean> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        isImage = true;
        this.context = context;
    }

    public TwoLineSelectAdapter(Context context, int textViewResourceId,
                                List<TwoLineSelectBean> objects, boolean isImage) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.isImage = isImage;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TwoLineListItem view;
        if (convertView == null) {
            view = (TwoLineListItem) inflater
                    .inflate(resourceId, parent, false);
        } else {
            view = (TwoLineListItem) convertView;
        }
        TwoLineSelectBean content = getItem(position);
        if (content != null) {
            if (view.getText1() != null) {
                view.getText1().setText(content.getText1());
            }
            if (view.getText2() != null) {
                view.getText2().setText(content.getText2());
            }

            ImageView img = (ImageView) view.findViewById(android.R.id.selectedIcon);
            //img.setColorFilter(context.getResources().getColor(android.R.color.primary_text_dark, context.getTheme()));
            if (isImage) {
                img.setImageResource(content.isSelect() ? R.drawable.ic_check_box_black_24dp : R.drawable.ic_check_blank_black_24dp);
            } else {
                img.setImageBitmap(null);
            }
        }
        return view;
    }

}
