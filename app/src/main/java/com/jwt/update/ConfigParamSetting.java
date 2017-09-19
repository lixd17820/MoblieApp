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
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jwt.bean.LoginResultBean;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

public class ConfigParamSetting extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {
	static final String TAG = "ConfigParamSetting";

	private CheckBoxPreference mGpsUploadState, mNeedSfzhState, mPreviewPhoto,mSkipSpinner;
	private ListPreference mGpsUpFreq, mNetWorkState, mPicCompress;
	private ListPreference mDrvCheck, mVehCheck;
	private Resources mRes;
	private Context self;
	private LocationManager locm;

	private ProgressDialog progressDialog;

	private final int REQ_START_GPS = 0;

	private String[] netValues = new String[] {"0","1"};

	// private String[] netStrs = new String[] { "中国移动连接", "中国电信连接", "公安部三所",
	// "离线模式" };

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mRes = getResources();
		self = this;
		SharedPreferences sp = getPreferenceManager().getSharedPreferences();
		printPer(sp, "before");
		cleanPerferenceValue(sp);
		printPer(sp, "after");
		addPreferencesFromResource(R.xml.param_edit);
		mNetWorkState = (ListPreference) findPreference("network_state");
		mGpsUploadState = (CheckBoxPreference) findPreference("gps_upload");
		mNeedSfzhState = (CheckBoxPreference) findPreference("need_sfzh");
		mGpsUpFreq = (ListPreference) findPreference("gps_up_freq");
		mPicCompress = (ListPreference) findPreference("pic_compress");
		mDrvCheck = (ListPreference) findPreference("drv_check");
		mVehCheck = (ListPreference) findPreference("veh_check");
		mPreviewPhoto = (CheckBoxPreference) findPreference("preview_photo");
        mSkipSpinner  = (CheckBoxPreference) findPreference("skip_spinner");
		mNetWorkState.setEntries(getConnStrs());
		mNetWorkState.setEntryValues(netValues);
		mNetWorkState.setEnabled(false);
		changePreviewPhoto();
		changeNewWorkState();
		changeGpsIsUpload();
		changeNeedSfzh();
		changeGpsUpFreq();
		changePicCompress();
		changeDrvCheck();
		changeVehCheck();
        changeSkipSpinner();
		mNetWorkState.setOnPreferenceChangeListener(this);
		mGpsUploadState.setOnPreferenceChangeListener(this);
		mNeedSfzhState.setOnPreferenceChangeListener(this);
		mGpsUpFreq.setOnPreferenceChangeListener(this);
		mPicCompress.setOnPreferenceChangeListener(this);
		mDrvCheck.setOnPreferenceChangeListener(this);
		mVehCheck.setOnPreferenceChangeListener(this);
		mPreviewPhoto.setOnPreferenceChangeListener(this);
        mSkipSpinner.setOnPreferenceChangeListener(this);
		String serverName = Context.LOCATION_SERVICE;
		locm = (LocationManager) getSystemService(serverName);
	}



    private String[] getConnStrs() {
		ConnCata[] v = ConnCata.values();
		String[] s = new String[v.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = v[i].getName();
		}
		return s;
	}

	private void cleanPerferenceValue(SharedPreferences sp) {
		Editor editer = sp.edit();
		editer.clear();
		editer.commit();
	}

	private void printPer(SharedPreferences sp, String time) {
		Map<String, ?> map = sp.getAll();
		for (Entry<String, ?> entry : map.entrySet()) {
			Log.e(TAG, time + " " + entry.getKey() + "/"
					+ entry.getValue().getClass().getName());
		}
	}

	private void changeVehCheck() {
		String[] values = mRes.getStringArray(R.array.veh_check_values);
		String[] entries = mRes.getStringArray(R.array.veh_check_entries);
		int index = -1;
		for (int i = 0; i < values.length; i++) {
			if (GlobalSystemParam.vehCheckFs == Integer.parseInt(entries[i])) {
				index = i;
				break;
			}
		}
		mVehCheck.setSummary(index > -1 ? values[index] : "未设定");
		mVehCheck.setValueIndex(index);

	}

	private void changeDrvCheck() {
		String[] values = mRes.getStringArray(R.array.drv_check_values);
		String[] entries = mRes.getStringArray(R.array.drv_check_entries);
		int index = -1;
		for (int i = 0; i < values.length; i++) {
			if (GlobalSystemParam.drvCheckFs == Integer.parseInt(entries[i])) {
				index = i;
				break;
			}
		}
		mDrvCheck.setSummary(index > -1 ? values[index] : "未设定");
		mDrvCheck.setValueIndex(index);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//GlobalMethod.saveParam(self);
	}

	private void changePicCompress() {
		String[] values = mRes.getStringArray(R.array.pic_compress_values);
		String[] entries = mRes.getStringArray(R.array.pic_compress_entries);
		int index = -1;
		for (int i = 0; i < values.length; i++) {
			if (GlobalSystemParam.picCompress == Integer.parseInt(entries[i])) {
				index = i;
				break;
			}
		}
		mPicCompress.setSummary(index > -1 ? values[index] : "未设定");
		mPicCompress.setValueIndex(index);
	}

	private void changeNewWorkState() {
		mNetWorkState.setSummary(GlobalData.connCata.getName());
		mNetWorkState.setValueIndex(GlobalData.connCata.getIndex());
	}

	private void changeGpsIsUpload() {
		mGpsUploadState.setChecked(GlobalSystemParam.isGpsUpload);
		mGpsUploadState.setSummary(GlobalSystemParam.isGpsUpload ? "上传定位信息"
				: "不上传定位信息");
	}

    private void changeSkipSpinner() {
        mSkipSpinner.setChecked(GlobalSystemParam.isSkipSpinner);
        mSkipSpinner.setSummary(GlobalSystemParam.isGpsUpload ? "跳过修正下拉框"
                : "不修正下拉框");
    }

	private void changePreviewPhoto() {
		mPreviewPhoto.setChecked(GlobalSystemParam.isPreviewPhoto);
		mPreviewPhoto.setSummary(GlobalSystemParam.isPreviewPhoto ? "预览照片" : "不预览照片");

	}

	private void changeNeedSfzh() {
		mNeedSfzhState.setChecked(GlobalSystemParam.isCheckFjdcSfzm);
		mNeedSfzhState.setSummary(GlobalSystemParam.isCheckFjdcSfzm ? "严格验证模式"
				: "宽松模式");
	}

	private void changeGpsUpFreq() {
		String[] values = mRes.getStringArray(R.array.gps_freq_values);
		String[] entries = mRes.getStringArray(R.array.gps_freq_entries);
		int index = -1;
		for (int i = 0; i < values.length; i++) {
			if (GlobalSystemParam.uploadFreq == Integer.parseInt(entries[i])) {
				index = i;
				break;
			}
		}
		mGpsUpFreq.setSummary(index > -1 ? values[index] : "未设定");
		mGpsUpFreq.setValueIndex(index);
	}

//	private void changeResetGps() {
//		ViolationDAO.saveGpsUploadFreq(GlobalSystemParam.uploadFreq,
//				getContentResolver());
//		// 改变了发送的频率
//		if (MainLoading.checkServerRunning(self, "com.ntga.jwt",
//				"com.ntga.jwt.MainReferService")) {
//			stopService(new Intent(self, MainReferService.class));
//		}
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		startService(new Intent(self, MainReferService.class));
//	}



	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		if ("network_state".equals(key)) {
		} else if (TextUtils.equals("preview_photo", key)) {
			GlobalSystemParam.isPreviewPhoto = (Boolean) newValue;
			changePreviewPhoto();
		} else if ("gps_upload".equals(key)) {
			boolean isUp = (Boolean) newValue;
			if (isUp && !locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// 需要打开GPS且GPS功能未打开
				GlobalMethod.toggleGPS(self);
				// GlobalMethod.showDialogWithListener("系统提示", "请打开GPS定位设备",
				// "打开",
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// Intent intent = new Intent(
				// Settings.ACTION_SECURITY_SETTINGS);
				// startActivityForResult(intent, REQ_START_GPS);
				// }
				// }, self);
			}
			GlobalSystemParam.isGpsUpload = isUp;
			changeGpsIsUpload();
		} else if ("need_sfzh".equals(key)) {
			GlobalSystemParam.isCheckFjdcSfzm = (Boolean) newValue;
			changeNeedSfzh();
		} else if ("gps_up_freq".equals(key)) {
			GlobalSystemParam.uploadFreq = Integer.parseInt((String) newValue);
			changeGpsUpFreq();
			//changeResetGps();
		} else if ("pic_compress".equals(key)) {
			GlobalSystemParam.picCompress = Integer.parseInt((String) newValue);
			changePicCompress();
		} else if ("drv_check".equals(key)) {
			GlobalSystemParam.drvCheckFs = Integer.parseInt((String) newValue);
			changeDrvCheck();
		} else if ("veh_check".equals(key)) {
			GlobalSystemParam.vehCheckFs = Integer.parseInt((String) newValue);
			changeVehCheck();
		}else if("skip_spinner".equals(key)){
            GlobalSystemParam.isSkipSpinner = (Boolean)newValue;
            changeSkipSpinner();
        }
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_START_GPS) {
			if (locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				GlobalSystemParam.isGpsUpload = true;
				changeGpsIsUpload();
			}
		}

	}

}
