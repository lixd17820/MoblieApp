package com.jwt.adapter;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwt.pojo.Bjbd;
import com.jwt.main.R;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/22.
 */

public class BjbdAdapter extends RecyclerView.Adapter<BjbdAdapter.BjbdViewHolder> {

    private BjbdClickListener clickListener;
    private List<Bjbd> dataset;

    public interface BjbdClickListener {
        void onBjbdItemClick(int position);
    }

    static class BjbdViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_left_mes)
        public TextView leftMes;
        @BindView(R.id.tv_left_send)
        public TextView leftSend;
        @BindView(R.id.tv_right_mes)
        public TextView rightMes;
        @BindView(R.id.tv_right_send)
        public TextView rightSend;
        @BindView(R.id.left_layout)
        public LinearLayout leftLayout;
        @BindView(R.id.right_layout)
        public LinearLayout rightLayout;

        public BjbdViewHolder(View itemView, final BjbdClickListener clickListener) {
            super(itemView);
            //text = (TextView) itemView.findViewById(R.id.textViewNoteText);
            //comment = (TextView) itemView.findViewById(R.id.textViewNoteComment);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onBjbdItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public BjbdAdapter(BjbdClickListener clickListener) {
        this.clickListener = clickListener;
        this.dataset = new ArrayList<Bjbd>();
    }

    public void setBjbds(@NonNull List<Bjbd> bjbds) {
        dataset = bjbds;
        notifyDataSetChanged();
    }

    public Bjbd getBjbd(int position) {
        return dataset.get(position);
    }

    @Override
    public BjbdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_two_bjbd, parent, false);
        return new BjbdViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(BjbdViewHolder viewHolder, int position) {
        Bjbd bjbd = dataset.get(position);
        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
        //Log.e("reciver", jybh + "/" + bjbd.getSender());
        boolean isSelf = TextUtils.equals(bjbd.getSender(), jybh);
        if (!isSelf) {
            //如果是收到的消息，则显示左边消息布局，将右边消息布局隐藏
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            String txt = bjbd.getBjyy();
            if ("1".equals(bjbd.getType())) {
                txt = bjbd.getDdsj() + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, bjbd.getHpzl())
                        + bjbd.getHphm() + "通过" + bjbd.getCbz() + "，布控原因：" + bjbd.getBjyy();
            }
            if(bjbd.getYdbj()!= 1)
                viewHolder.leftMes.setTypeface(null, Typeface.ITALIC);
            viewHolder.leftMes.setText(txt);
            viewHolder.leftSend.setText("系统");
        } else {
            //如果是发出去的消息，显示右边布局的消息布局，将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMes.setText(bjbd.getBjyy());
            viewHolder.rightSend.setText("我");
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
