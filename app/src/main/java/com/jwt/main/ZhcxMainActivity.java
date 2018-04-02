package com.jwt.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jwt.globalquery.CxMenus;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZhcxMainActivity extends AppCompatActivity {
    private Context self;
    private CxMenus[] zhcxMenus;
    private ArrayAdapter<String> adapter;
    private List<String> strList;
    @BindView(android.R.id.list)
    ListView listView;

    // private Map<String, Integer> imgs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_no_button_list);
        self = this;
        ButterKnife.bind(this);
        setTitle("综合查询");
        createZhcxMenus();
        strList = new ArrayList<String>();
        if (zhcxMenus != null && zhcxMenus.length > 0) {
            for (CxMenus m : zhcxMenus) {
                strList.add(m.getCxMenuName());
            }
        }
        adapter = new ArrayAdapter<String>(self,
                android.R.layout.simple_list_item_1, strList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view,
                                    int position, long arg3) {
                if (position < 0)
                    return;
                CxMenus m = zhcxMenus[position];
                Intent intent = new Intent(self, ZhcxConditionActivity.class);
                intent.putExtra("zhcxItem", m);
                startActivity(intent);
            }
        });
    }


    private void createZhcxMenus() {
        String cx = GlobalSystemParam.getParam(self,GlobalConstant.SP_CX_MENUS,"{}");
        try {
            zhcxMenus = ParserJson.parseJsonToArray(cx, CxMenus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
