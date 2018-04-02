package com.jwt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jwt.bean.TwoLineSelectBean;
import com.jwt.main.R;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/27.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    private List<TwoLineSelectBean> imageList;
    private ImageClickListener clickListener;

    public interface ImageClickListener {
        void onClick(int position);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image1)
        public ImageView img1;
        @BindView(R.id.image2)
        public ImageView img2;

        public ImageViewHolder(View itemView, final ImageClickListener clickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
//        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        int ph = parent.getMeasuredHeight();
//        int pw = parent.getMeasuredWidth();
//        int margin = 24;
//        if (ph > pw) {
//            int row = (getItemCount() - 1) / cols + 1;
//            lp.height = (parent.getMeasuredHeight() - (margin * 2 * row)) / row;
//        } else {
//            lp.height = (parent.getMeasuredHeight() - (margin * 2 * cols)) / cols;
//        }
//        lp.setMargins(0, margin, 0, margin);
//        Log.e("height", lp.height + "");
//        view.setLayoutParams(lp);
        return new ImageViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        TwoLineSelectBean img = imageList.get(position);
        holder.img1.setImageBitmap(GlobalMethod.getImageFromFile(img.getText1()));
        holder.img2.setImageResource(img.isSelect() ? R.drawable.ic_check_box_black_24dp :
                R.drawable.ic_check_blank_black_24dp);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public List<String> getList(){
        List<String> l = new ArrayList<>();
        for(TwoLineSelectBean t: imageList){
            l.add(t.getText1());
        }
        return l;
    }

    public ImageListAdapter(ImageClickListener clickListener, List<String> list) {
        this.clickListener = clickListener;
        List<TwoLineSelectBean> sbs = new ArrayList<>();
        if (list != null) {
            for (String s : list)
                sbs.add(new TwoLineSelectBean(s, "", false));
        }
        this.imageList = sbs;
    }

    public void setImageList(@NonNull List<String> list) {
        List<TwoLineSelectBean> sbs = new ArrayList<>();
        if (list != null) {
            for (String s : list)
                sbs.add(new TwoLineSelectBean(s, "", false));
        }
        this.imageList = sbs;
        notifyDataSetChanged();
    }

    public String getImg(int position) {
        return imageList.get(position).getText1();
    }

    public void setSelectIndex(int pos) {
        boolean isSel = imageList.get(pos).isSelect();
        for (int i = 0; i < imageList.size(); i++) {
            imageList.get(i).setSelect(false);
        }
        imageList.get(pos).setSelect(!isSel);
    }

    public int getSelectIndex() {
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).isSelect())
                return i;
        }
        return -1;
    }

}
