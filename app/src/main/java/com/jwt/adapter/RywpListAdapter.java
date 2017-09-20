package com.jwt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.dao.ZaPcdjDao;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.pojo.ZapcRypcxxBean;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.pojo.Zapcxx;
import com.jwt.update.R;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.util.List;

public class RywpListAdapter extends RecyclerView.Adapter<RywpListAdapter.ViewHolder> {

    private List<SelectObjectBean<Zapcxx>> list;
    private RywpListAdapter.ClickListener clickListener;


    public RywpListAdapter(List<SelectObjectBean<Zapcxx>> list, RywpListAdapter.ClickListener clickListener) {
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
    public RywpListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comm_two_line_select_item, parent, false);
        return new RywpListAdapter.ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(RywpListAdapter.ViewHolder holder, int position) {
        SelectObjectBean<Zapcxx> bean = list.get(position);
        Zapcxx b = bean.getBean();
        String[] s = b.getXxms().split("\n");
        holder.tv1.setText(s[0]);
        holder.tv2.setText(s[1] + ("1".equals(b.getScbj()) ? "，已上传" : "，未上传"));
        holder.imgUp.setImageResource(b.getPcZl() == Zapcxx.PCRYXXZL ? R.drawable.ic_person_black_24dp :
                R.drawable.ic_drive_eta_black_24dp);
        holder.imgDown.setImageResource(bean.isSel() ? R.drawable.ic_check_box_black_24dp
                : R.drawable.ic_check_blank_black_24dp);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(@NonNull List<SelectObjectBean<Zapcxx>> l) {
        list = l;
        notifyDataSetChanged();
    }

    public List<SelectObjectBean<Zapcxx>> getList() {
        return list;
    }

    public int getSelectIndex() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSel())
                return i;
        }
        return -1;
    }

    public Zapcxx getSelectItem() {
        int index = getSelectIndex();
        if (index == -1)
            return null;
        return list.get(index).getBean();
    }


}
