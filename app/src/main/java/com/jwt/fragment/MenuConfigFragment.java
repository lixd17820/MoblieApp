package com.jwt.fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jwt.bean.MenuGridBean;
import com.jwt.bean.MenuOptionBean;
import com.jwt.utils.MenuParser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuConfigFragment extends ListFragment {
    private List<String> strList;
    private ArrayList<MenuOptionBean> menus;

    public MenuConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        strList = new ArrayList<String>();
        List<MenuGridBean> list = MenuParser.parseMenuXml(inflater.getContext());
        if (list != null && !list.isEmpty()) {
            MenuGridBean mg = list.get(2);
            if (mg != null && mg.getOptions() != null
                    && !mg.getOptions().isEmpty()) {
                menus = mg.getOptions();
                for (MenuOptionBean m : menus) {
                    strList.add(m.getMenuName());
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),
                android.R.layout.simple_list_item_1, strList);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position < 0 && menus != null && !menus.isEmpty())
            return;
        MenuOptionBean m = menus.get(position);
        Log.e("MainConfig", m.getPck() + "/" + m.getClassName());
//                Intent intent = new Intent(self, ConfigMjgrxxActivity.class);
//                startActivity(intent);
        if (!TextUtils.isEmpty(m.getPck())
                && !TextUtils.isEmpty(m.getClassName())) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(m.getPck(), m
                    .getClassName()));
            if (!TextUtils.isEmpty(m.getDataName())
                    && !TextUtils.isEmpty(m.getData())) {
                intent.putExtra(m.getDataName(), m.getData());
            }
            intent.putExtra("title", m.getMenuName());
            if (!TextUtils.isEmpty(m.getData()))
                intent.putExtra("data", m.getData());
            startActivity(intent);
        }
    }
}
