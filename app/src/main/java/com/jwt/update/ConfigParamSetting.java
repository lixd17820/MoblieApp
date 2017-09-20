package com.jwt.update;

import java.util.Map;
import java.util.Map.Entry;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jwt.bean.LoginResultBean;
import com.jwt.fragment.BjbdConfigFragement;
import com.jwt.fragment.SysConfigFragement;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

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
    }

    @Override
    protected void onDestroy() {
        GlobalMethod.saveParam(this);
        super.onDestroy();
    }
}
