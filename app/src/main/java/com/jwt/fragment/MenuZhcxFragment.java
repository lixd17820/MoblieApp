package com.jwt.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jwt.globalquery.CxMenus;
import com.jwt.update.R;
import com.jwt.update.ZhcxConditionActivity;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.ParserJson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuZhcxFragment extends ListFragment {


    private ArrayList<String> strList;
    private CxMenus[] zhcxMenus;

    public MenuZhcxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        zhcxMenus = createZhcxMenus(inflater.getContext());
        strList = new ArrayList<String>();
        if (zhcxMenus != null && zhcxMenus.length > 0) {
            for (CxMenus m : zhcxMenus) {
                strList.add(m.getCxMenuName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                inflater.getContext(),
                android.R.layout.simple_list_item_1, strList);
        setListAdapter(adapter);
        Log.e("MenuZhcxFragment", "onCreateView " + strList.size());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CxMenus m = zhcxMenus[position];
        Intent intent = new Intent(getActivity(), ZhcxConditionActivity.class);
        intent.putExtra("zhcxItem", m);
        startActivity(intent);
    }

    private CxMenus[] createZhcxMenus(Context self) {
        SharedPreferences sp = self.getSharedPreferences(GlobalConstant.MJXX_INFO, Context.MODE_PRIVATE);
        String cx = sp.getString("cxMenus", "[]");
        try {
            return ParserJson.parseJsonToArray(cx, CxMenus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
