package com.jwt.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.bean.MenuOptionBean;
import com.jwt.update.R;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/27.
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MenuViewHolder> {

    private List<MenuOptionBean> menus;
    private MenuClickListener clickListener;
    private int cols;

    public interface MenuClickListener {
        void onNoteClick(int position);
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_item)
        public TextView text;
        @BindView(R.id.image_item)
        public ImageView img;
        @BindView(R.id.red_item)
        public ImageView red;

        public MenuViewHolder(View itemView, final MenuClickListener clickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int ph = parent.getMeasuredHeight();
        int pw = parent.getMeasuredWidth();
        int margin = 24;
        if (ph > pw) {
            int row = (getItemCount() - 1) / cols + 1;
            lp.height = (parent.getMeasuredHeight() - (margin * 2 * row)) / row;
        } else {
            lp.height = (parent.getMeasuredHeight() - (margin * 2 * cols)) / cols;
        }
        lp.setMargins(0, margin, 0, margin);
        view.setLayoutParams(lp);
        return new MenuViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        MenuOptionBean menu = menus.get(position);
        holder.text.setText(menu.getMenuName());
        int imgResouse = GlobalMethod.getImageResouseByName(menu.getImg());
        if (imgResouse > 0)
            holder.img.setImageResource(imgResouse);
        else
            holder.img.setImageResource(R.drawable.ic_launcher);
        holder.red.setVisibility(menu.isBadge() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public MainMenuAdapter(MenuClickListener clickListener, int cols) {
        this.clickListener = clickListener;
        this.menus = new ArrayList<MenuOptionBean>();
        this.cols = cols;
    }

    public void setMenus(@NonNull List<MenuOptionBean> _menus) {
        menus = _menus;
        notifyDataSetChanged();
    }

    public MenuOptionBean getMenu(int position) {
        return menus.get(position);
    }

}
