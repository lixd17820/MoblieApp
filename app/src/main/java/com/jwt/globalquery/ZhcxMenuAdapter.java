package com.jwt.globalquery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwt.update.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lixiaodong on 2017/8/27.
 */

public class ZhcxMenuAdapter extends RecyclerView.Adapter<ZhcxMenuAdapter.MenuViewHolder> {

    private List<String> menus;
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
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MenuViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        String menu = menus.get(position);
        holder.text.setText(menu);
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public ZhcxMenuAdapter(MenuClickListener clickListener) {
        this.clickListener = clickListener;
        this.menus = new ArrayList<String>();
        this.cols = cols;
    }

    public void setMenus(List<String> _menus) {
        menus = _menus;
        notifyDataSetChanged();
    }

    public String getMenu(int position) {
        return menus.get(position);
    }

}
