package com.jwt.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.bean.TwoLineSelectBean;
import com.jwt.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaodong on 2017/9/9.
 */

public class MyListAdapter {

    public interface ClickListener {
        void onNoteClick(int position);
    }

    private Activity context;
    private ViewGroup parent;
    private ClickListener clickListener;
    private List<TwoLineSelectBean> list;
    private List<View> viewList;

    public MyListAdapter(Activity context, int source, List<TwoLineSelectBean> list, ClickListener clickListener) {
        this.context = context;
        this.parent = (ViewGroup) context.findViewById(source);
        this.clickListener = clickListener;
        this.list = list;
        viewList = new ArrayList<>();
        if (list != null && list.size() > 0)
            initView();
    }

    private void initView() {
        for (int i = 0; i < list.size(); i++) {
            View v = onCreateView(i);
            viewList.add(v);
            parent.addView(v);
        }
    }

    private View onCreateView(final int position) {
        View view = context.getLayoutInflater().inflate(R.layout.one_row_select_item, parent, false);
        drawView(position, view);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.onNoteClick(position);
            }
        });
        return view;
    }

    public void drawView(final int position, View view) {
        TwoLineSelectBean b = list.get(position);
        TextView tv = (TextView) view.findViewById(R.id.text1);
        tv.setText(b.getText2());
        ImageView img = (ImageView) view.findViewById(R.id.image1);
        img.setImageResource(b.isSelect() ? R.drawable.ic_check_box_black_24dp
                : R.drawable.ic_check_blank_black_24dp);
    }

    public View getViewByPos(int postion) {
        return viewList.get(postion);
    }

    public int[] getSelectPos() {
        if (list == null)
            return null;
        List<Integer> sel = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelect())
                sel.add(i);
        }
        if (sel.isEmpty())
            return null;
        int[] re = new int[sel.size()];
        for (int i = 0; i < sel.size(); i++) {
            re[i] = sel.get(i);
        }
        return re;
    }

    public List<TwoLineSelectBean> getData() {
        if (list == null)
            list = new ArrayList<TwoLineSelectBean>();
        return list;
    }

    public void noteDataChange() {
        for (View view : viewList) {
            parent.removeView(view);
        }
        viewList.clear();
        for (int i = 0; i < list.size(); i++) {
            View v = onCreateView(i);
            viewList.add(v);
            parent.addView(v);
        }
    }

}
