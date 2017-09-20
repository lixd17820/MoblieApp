package com.jwt.update;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jwt.bean.MenuGridBean;
import com.jwt.bean.MenuOptionBean;
import com.jwt.utils.MenuParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigMainActivity extends AppCompatActivity {

    private Context self;
    private List<String> strList;
    @BindView(android.R.id.list)
    ListView listView;
    private ArrayList<MenuOptionBean> menus;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_no_button_list);
        self = this;
        ButterKnife.bind(this);
        setTitle("系统配置");
        strList = new ArrayList<String>();
        List<MenuGridBean> list = MenuParser.parseMenuXml(self);
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
        adapter = new ArrayAdapter<String>(self,
                android.R.layout.simple_list_item_1, strList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long arg3) {
                if (position < 0 && menus != null && !menus.isEmpty())
                    return;
                MenuOptionBean m = menus.get(position);
                Log.e("MainConfig", m.getPck() + "/" + m.getClassName());
//                Intent intent = new Intent(self, ConfigMjgrxxActivity.class);
//                startActivity(intent);
                Intent intent = null;
                if (!TextUtils.isEmpty(m.getPck())
                        && !TextUtils.isEmpty(m.getClassName())) {
                    intent = new Intent();
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
        });
    }
}
