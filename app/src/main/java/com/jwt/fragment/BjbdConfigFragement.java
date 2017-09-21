package com.jwt.fragment;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;

import com.jwt.bean.KeyValueBean;
import com.jwt.event.MqttEvent;
import com.jwt.update.MainReferService;
import com.jwt.update.R;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by lixiaodong on 2017/9/20.
 */

public class BjbdConfigFragement extends PreferenceFragment {

    //private CheckBoxPreference mRecBjbd, mRecText;
    private SwitchPreference mOpenBjbd;
    private MultiSelectListPreference mBjbdCatalog, mBjbdFw;
    //private Activity self;
    //private SharedPreferences sp;
    private String[] bjzlValues, bjzlNames, glbmValues, glbmNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //self = this.getActivity();
        //sp = getPreferenceManager().getSharedPreferences();
        addPreferencesFromResource(R.xml.param_bjbd);
        //mRecBjbd = (CheckBoxPreference) findPreference("is_rec_bj");
        //mRecText = (CheckBoxPreference) findPreference("is_rec_text");
        mOpenBjbd = (SwitchPreference)findPreference("is_conn_bjbd");
        mBjbdCatalog = (MultiSelectListPreference) findPreference("bjbd_catalog");
        mBjbdFw = (MultiSelectListPreference) findPreference("bjbd_fw");
        setBjbdCatalog(mBjbdCatalog);
        setBjbdFw(mBjbdFw);
        mBjbdCatalog.setOnPreferenceChangeListener(changeListener);
        mBjbdFw.setOnPreferenceChangeListener(changeListener);
        mOpenBjbd.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                GlobalSystemParam.isConnBjbd = (boolean)newValue;
                MqttEvent event = new MqttEvent();
                event.setCatalog(MainReferService.MANAGE_CONN);
                EventBus.getDefault().post(event);
                return true;
            }
        });
    }

    private void setBjbdCatalog(MultiSelectListPreference zl) {
        bjzlValues = new String[GlobalData.bjzlList.size()];
        bjzlNames = new String[GlobalData.bjzlList.size()];
        for (int i = 0; i < GlobalData.bjzlList.size(); i++) {
            KeyValueBean kv = GlobalData.bjzlList.get(i);
            bjzlValues[i] = kv.getKey();
            bjzlNames[i] = kv.getValue();
        }
        zl.setEntryValues(bjzlValues);
        zl.setEntries(bjzlNames);
        changeSummary(zl);
    }

    private void setBjbdFw(MultiSelectListPreference fw) {
        glbmValues = new String[GlobalData.glbmList.size()];
        glbmNames = new String[GlobalData.glbmList.size()];
        for (int i = 0; i < glbmNames.length; i++) {
            glbmNames[i] = GlobalData.glbmList.get(i).getValue();
            glbmValues[i] = GlobalData.glbmList.get(i).getKey();
        }
        fw.setEntryValues(glbmValues);
        fw.setEntries(glbmNames);
        changeSummary(fw);
    }

    private void changeSummary(MultiSelectListPreference lp) {
        Set<String> set = lp.getValues();
        changeSummary(lp, set);
    }

    private void changeSummary(MultiSelectListPreference lp, Set<String> set) {
        CharSequence[] names = lp.getEntries();
        CharSequence[] values = lp.getEntryValues();
        if (set == null || set.isEmpty()) {
            lp.setSummary("未设定");
            return;
        }
        List<String> ns = new ArrayList<>();
        for (String s : set) {
            int index = GlobalMethod.indexOf(values, s);
            String name = names[index].toString();
            ns.add(name);
        }
        lp.setSummary(GlobalMethod.join(ns, ","));
    }


    Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Set<String> value = (Set<String>) newValue;
            changeSummary((MultiSelectListPreference) preference, value);
            if(preference == mBjbdFw){
                GlobalSystemParam.recBjbdFW = value;
                MqttEvent event = new MqttEvent();
                event.setCatalog(MainReferService.MANAGE_TOPIC);
                EventBus.getDefault().post(event);
            }else if(preference == mBjbdCatalog){
                GlobalSystemParam.recBjbdZl = value;
                GlobalSystemParam.syncBjzl();
            }
            return true;
        }
    };

}
