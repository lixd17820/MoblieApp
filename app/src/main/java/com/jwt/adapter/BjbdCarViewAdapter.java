package com.jwt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.pojo.Bjbd;
import com.jwt.main.R;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/22.
 */

public class BjbdCarViewAdapter extends RecyclerView.Adapter<BjbdCarViewAdapter.BjbdViewHolder> {

    private List<SelectObjectBean<Bjbd>> dataset;
    private boolean isShowSelect = false;

    public interface BjbdClickListener {
        void onBjbdItemClick(int position);
    }

    static class BjbdViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_item)
        public TextView text;
        @BindView(R.id.image_item)
        public ImageView img;
        @BindView(R.id.red_item)
        public ImageView readBj;

        public BjbdViewHolder(View itemView) {
            super(itemView);
            //text = (TextView) itemView.findViewById(R.id.textViewNoteText);
            //comment = (TextView) itemView.findViewById(R.id.textViewNoteComment);
            ButterKnife.bind(this, itemView);
        }
    }

    public BjbdCarViewAdapter(List<SelectObjectBean<Bjbd>> dataset) {
        this.dataset = dataset;
    }

    public void setBjbds(@NonNull List<SelectObjectBean<Bjbd>> bjbds) {
        dataset = bjbds;
        notifyDataSetChanged();
    }

    public SelectObjectBean<Bjbd> getBjbd(int position) {
        return dataset.get(position);
    }

    @Override
    public BjbdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_jbyw_bjbd_item, parent, false);
        return new BjbdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BjbdViewHolder viewHolder, int position) {
        SelectObjectBean<Bjbd> data = dataset.get(position);
        Bjbd bjbd = data.getBean();
        String txt = bjbd.getId() + ". " + bjbd.getDdsj() + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, bjbd.getHpzl())
                + bjbd.getHphm() + "通过" + bjbd.getCbz() + "，布控原因：" + bjbd.getBjyy()+", " + bjbd.getYdbj();
        viewHolder.text.setText(txt);
        if(bjbd.getYdbj() == 1){
            viewHolder.readBj.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.readBj.setVisibility(View.VISIBLE);
        }
        if (isShowSelect) {
            viewHolder.img.setImageResource(data.isSel() ? R.drawable.ic_check_box_black_24dp
                    : R.drawable.ic_check_blank_black_24dp);
        } else {
            viewHolder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
