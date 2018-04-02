package com.jwt.main;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jwt.fragment.BjbdConfigFragement;
import com.jwt.fragment.JtssLightFragement;
import com.jwt.fragment.SysConfigFragement;
import com.jwt.utils.GlobalMethod;

public class ConfigParamSetting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String data = getIntent().getStringExtra("data");
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        if ("1".equals(data))
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SysConfigFragement()).commit();
        else if ("2".equals(data))
            getFragmentManager().beginTransaction().replace(android.R.id.content, new BjbdConfigFragement()).commit();
        else if("3".equals(data)){
        }
    }

    @Override
    protected void onDestroy() {
        //GlobalMethod.saveSysParamIntoSpf(this);
        super.onDestroy();
    }
}
