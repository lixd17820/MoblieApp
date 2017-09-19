package com.jwt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.update.R;

import java.util.ArrayList;
import java.util.List;

public class FxczfListAdapter extends RecyclerView.Adapter<FxczfListAdapter.ViewHolder> {

    private List<SelectObjectBean<VioFxczfBean>> list;
    private FxczfListAdapter.ClickListener clickListener;


    public FxczfListAdapter(List<SelectObjectBean<VioFxczfBean>> list, FxczfListAdapter.ClickListener clickListener) {
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
    public FxczfListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comm_two_line_select_item, parent, false);
        return new FxczfListAdapter.ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(FxczfListAdapter.ViewHolder holder, int position) {
        SelectObjectBean<VioFxczfBean> bean = list.get(position);
        VioFxczfBean b = bean.getBean();
        holder.tv1.setText(b.getWfsj() + "，号牌：" + b.getHphm() + "，" + b.getPhotos() + "张");
        holder.tv2.setText(b.getWfxw() + "地点：" + b.getWfdz());
        boolean noScbj = TextUtils.isEmpty(b.getScbj()) || TextUtils.equals("0", b.getScbj());
        holder.imgUp.setImageResource(!noScbj ? R.drawable.ic_ok_black_24dp
                : R.drawable.ic_help_outline_black_24dp);
        holder.imgDown.setImageResource(bean.isSel() ? R.drawable.ic_check_box_black_24dp
                : R.drawable.ic_check_blank_black_24dp);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(@NonNull List<SelectObjectBean<VioFxczfBean>> l) {
        list = l;
        notifyDataSetChanged();
    }

    public List<SelectObjectBean<VioFxczfBean>> getList() {
        return list;
    }

    public int getFirstSelectIndex() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSel())
                return i;
        }
        return -1;
    }

    public int[] getSelectIndexs() {
        List<Integer> res = new ArrayList<>();
        if (list == null || list.isEmpty())
            return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSel())
                res.add(i);
        }
        int[] its = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {
            its[i] = res.get(i);
        }
        return its;
    }

    public int getSelectCount(){
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            count += list.get(i).isSel()?1:0;
        }
        return count;
    }

    public List<VioFxczfBean> getSelectItems() {
        List<VioFxczfBean> res = new ArrayList<>();
        if (list == null || list.isEmpty())
            return res;
        for (SelectObjectBean<VioFxczfBean> fxc : list) {
            if (fxc.isSel())
                res.add(fxc.getBean());
        }
        return res;
    }

    public VioFxczfBean getFirstSelectItem() {
        int index = getFirstSelectIndex();
        if (index < 0)
            return null;
        return list.get(index).getBean();
    }


}
