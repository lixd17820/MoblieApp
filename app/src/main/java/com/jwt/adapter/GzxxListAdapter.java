package com.jwt.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.bean.MenuOptionBean;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.update.R;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GzxxListAdapter extends RecyclerView.Adapter<GzxxListAdapter.ViewHolder> {

    private List<SelectObjectBean<ZapcGzxxBean>> list;
    private GzxxListAdapter.ClickListener clickListener;


    public GzxxListAdapter(List<SelectObjectBean<ZapcGzxxBean>> list, GzxxListAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUp, imgDown;
        public TextView tv1, tv2;

        public ViewHolder(View itemView, final ClickListener clickListener) {
            super(itemView);
            imgUp = (ImageView) itemView.findViewById(R.id.imageView1);
            imgDown = (ImageView) itemView.findViewById(R.id.imageView2);
            tv1 = (TextView) itemView.findViewById(R.id.textView1);
            tv2 = (TextView) itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onNoteClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface ClickListener {
        void onNoteClick(int position);
    }

    @Override
    public GzxxListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comm_two_line_select_item, parent, false);
        return new GzxxListAdapter.ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(GzxxListAdapter.ViewHolder holder, int position) {
        SelectObjectBean<ZapcGzxxBean> bean = list.get(position);
        ZapcGzxxBean b = bean.getBean();
        holder.tv1.setText(b.getUpText());
        holder.tv2.setText(b.getDownText());
        boolean noScbj = TextUtils.isEmpty(b.getCsbj()) || TextUtils.equals("0", b.getCsbj());
        holder.imgUp.setImageResource(!noScbj ? R.drawable.ic_ok_black_24dp
                : R.drawable.ic_help_outline_black_24dp);
        holder.imgDown.setImageResource(bean.isSel() ? R.drawable.ic_check_box_black_24dp
                : R.drawable.ic_check_blank_black_24dp);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(@NonNull List<SelectObjectBean<ZapcGzxxBean>> l) {
        list = l;
        notifyDataSetChanged();
    }

    public List<SelectObjectBean<ZapcGzxxBean>> getList() {
        return list;
    }

    public int getSelectIndex() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSel())
                return i;
        }
        return -1;
    }

    public ZapcGzxxBean getSelectItem() {
        int index = getSelectIndex();
        if (index == -1)
            return null;
        return list.get(index).getBean();
    }


}
