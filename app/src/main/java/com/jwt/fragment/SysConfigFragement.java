package com.jwt.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.update.R;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalSystemParam;

import java.util.Map;

/**
 * Created by lixiaodong on 2017/9/20.
 */

public class SysConfigFragement extends PreferenceFragment {

    private CheckBoxPreference mGpsUploadState, mNeedSfzhState, mPreviewPhoto, mSkipSpinner;
    private ListPreference mGpsUpFreq, mNetWorkState, mPicCompress;
    private ListPreference mDrvCheck, mVehCheck;
    private Activity self;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this.getActivity();
        sp = getPreferenceManager().getSharedPreferences();
        addPreferencesFromResource(R.xml.param_edit);
        mNetWorkState = (ListPreference) findPreference("network_state");
        mGpsUploadState = (CheckBoxPreference) findPreference("gps_upload");
        mNeedSfzhState = (CheckBoxPreference) findPreference("need_sfzh");
        mGpsUpFreq = (ListPreference) findPreference("gps_up_freq");
        mPicCompress = (ListPreference) findPreference("pic_compress");
        mDrvCheck = (ListPreference) findPreference("drv_check");
        mVehCheck = (ListPreference) findPreference("veh_check");
        mPreviewPhoto = (CheckBoxPreference) findPreference("preview_photo");
        mSkipSpinner = (CheckBoxPreference) findPreference("skip_spinner");
        changeNewWorkState();
        mNetWorkState.setEnabled(false);
        changeSummary(mGpsUpFreq);
        changeSummary(mPicCompress);
        changeSummary(mDrvCheck);
        changeSummary(mVehCheck);
        mGpsUpFreq.setOnPreferenceChangeListener(changeListener);
        mPicCompress.setOnPreferenceChangeListener(changeListener);
        mDrvCheck.setOnPreferenceChangeListener(changeListener);
        mVehCheck.setOnPreferenceChangeListener(changeListener);
    }

    private void changeSummary(ListPreference lp) {
        String value = lp.getValue();
        changeSummary(lp, value);
    }

    private void changeSummary(ListPreference lp, String value) {
        CharSequence[] names = lp.getEntries();
        CharSequence[] values = lp.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (TextUtils.equals(value, values[i].toString())) {
                lp.setSummary(names[i]);
            }
        }
    }

    private void changeNewWorkState() {
        CharSequence[] names = mNetWorkState.getEntries();
        CharSequence[] values = mNetWorkState.getEntryValues();
        for (int i = 0; i < values.length; i++) {
            if (TextUtils.equals(GlobalData.connCata.getIndex() + "", values[i].toString())) {
                mNetWorkState.setSummary(names[i]);
                mNetWorkState.setValue(values[i].toString());
            }
        }
    }

    Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String value = newValue.toString();
            changeSummary((ListPreference) preference, value);
            return true;
        }
    };

}
