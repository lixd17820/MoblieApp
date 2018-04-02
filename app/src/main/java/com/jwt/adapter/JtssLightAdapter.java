package com.jwt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.bean.LightInfoBean;
import com.jwt.main.R;
import com.jwt.pojo.Bjbd;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/22.
 */

public class JtssLightAdapter extends RecyclerView.Adapter<JtssLightAdapter.LightViewHolder> {

    private List<SelectObjectBean<LightInfoBean>> dataset;

    static class LightViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text1)
        public TextView text;
        @BindView(R.id.image1)
        public ImageView img;

        public LightViewHolder(View itemView) {
            super(itemView);
            //text = (TextView) itemView.findViewById(R.id.textViewNoteText);
            //comment = (TextView) itemView.findViewById(R.id.textViewNoteComment);
            ButterKnife.bind(this, itemView);
        }
    }

    public JtssLightAdapter(List<SelectObjectBean<LightInfoBean>> dataset) {
        this.dataset = dataset;
    }

    public void setLights(@NonNull List<SelectObjectBean<LightInfoBean>> dataset) {
        dataset = dataset;
        notifyDataSetChanged();
    }

    public SelectObjectBean<LightInfoBean> getBjbd(int position) {
        return dataset.get(position);
    }

    @Override
    public LightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_row_select_item, parent, false);
        return new LightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LightViewHolder viewHolder, int position) {
        SelectObjectBean<LightInfoBean> data = dataset.get(position);
        LightInfoBean l = data.getBean();
        JSONArray lpicArray = l.getLocalPics();
        int lpicNum = lpicArray == null || lpicArray.length() == 0 ? 0 : lpicArray.length();
        String txt = l.getCrossName() +
                "\n" + l.getLkfx() + "; " + l.getLight()
                + "; " + (l.getPicNum() > 0 ? "系统有" + l.getPicNum() + "张图" : "系统无图片");
        if (lpicNum > 0) {
            txt += "; 手机有" + lpicNum + "张图";
            int unsc = 0;
            for (int i = 0; i < lpicArray.length(); i++) {
                JSONObject obj = lpicArray.optJSONObject(i);
                unsc += obj.optInt("scbj", 0) > 0 ? 0 : 1;
            }
            txt += unsc+ "张未上传";
        }
        viewHolder.text.setText(txt);
        viewHolder.img.setImageResource(data.isSel() ? R.drawable.ic_check_box_black_24dp
                : R.drawable.ic_check_blank_black_24dp);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
