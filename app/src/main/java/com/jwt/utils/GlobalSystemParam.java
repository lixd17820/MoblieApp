package com.jwt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlobalSystemParam {

    /**
     * 图片压缩比例
     */
    public static int picCompress = 60;
    /**
     * 保存决定书时验证驾驶员和机动车的方式,初始化为本地车和证
     */
    public static int drvCheckFs = 2;
    /**
     * 机动车验证方式
     */
    public static int vehCheckFs = 2;

    /**
     * 是否上传GPS位置
     */
    public static boolean isGpsUpload = false;
    /**
     * 是否对非机动车身份证明进行严格证认
     */
    public static boolean isCheckFjdcSfzm = false;
    /**
     * 心跳包和GPS包上传频率，单位是分钟
     */
    public static int uploadFreq = 2;

    /**
     * 拍照后是否预览
     */
    public static boolean isPreviewPhoto = true;

    /**
     * 是否跳过两次下拉框联动赋值
     */
    public static boolean isSkipSpinner = false;

    public static int unsend_fxc_hours = 24 * 6;

    //是否接收报警
    public static boolean isReciveBj = true;
    //是否接收同事信息
    public static boolean isReciveText = true;

    public static boolean isNotNotice = true;

    public static boolean isConnBjbd = true;

    public static boolean isLogToFile = false;

    public static String bjRingtone = "";

    public static Set<String> recBjbdZl = new HashSet<>();

    public static Set<String> recBjbdFW = new HashSet<>();

    public static Set<String> bjVehSet = new HashSet<>();

    public static Set<String> bjCbzSet = new HashSet<>();

    public static Set<String> bjzlNames = new HashSet<>();


    public static void syncBjzl() {
        bjzlNames.clear();
        if (recBjbdZl != null && !recBjbdZl.isEmpty()) {
            for (String key : recBjbdZl) {
                String name = GlobalMethod.getStringFromKVListByKey(GlobalData.bjzlList, key);
                bjzlNames.add(name);
            }
        }
    }

    /**
     * 保存系统参数
     *
     * @param context
     */
    public static void readSysParamFormSpf(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        GlobalSystemParam.vehCheckFs = Integer.valueOf(prefs.getString("veh_check", "2"));
        //GlobalSystemParam.drvCheckFs = Integer.valueOf(prefs.getString("drv_check", "2"));
        GlobalSystemParam.picCompress = Integer.valueOf(prefs.getString("pic_compress", "60"));
        GlobalSystemParam.uploadFreq = Integer.valueOf(prefs.getString("gps_up_freq", "5"));
        GlobalSystemParam.isPreviewPhoto = prefs.getBoolean("preview_photo", true);
        GlobalSystemParam.isGpsUpload = prefs.getBoolean("gps_upload", false);
        GlobalSystemParam.isSkipSpinner = prefs.getBoolean("skip_spinner", false);
        GlobalSystemParam.isCheckFjdcSfzm = prefs.getBoolean("need_sfzh", false);
        GlobalSystemParam.recBjbdFW = prefs.getStringSet("bjbd_fw", new HashSet<String>());
        GlobalSystemParam.recBjbdZl = prefs.getStringSet("bjbd_catalog", new HashSet<String>());
        GlobalSystemParam.isReciveBj = prefs.getBoolean("is_rec_bj", true);
        GlobalSystemParam.isReciveText = prefs.getBoolean("is_rec_text", true);
        GlobalSystemParam.isNotNotice = prefs.getBoolean("not_notice", true);
        GlobalSystemParam.bjRingtone = prefs.getString("bj_ringtone", "");
        GlobalSystemParam.isConnBjbd = prefs.getBoolean("is_conn_bjbd", false);
        GlobalSystemParam.isLogToFile = prefs.getBoolean("log_status", false);
    }

//    public static void saveSysParamIntoSpf(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putInt("veh_check", GlobalSystemParam.vehCheckFs);
//        edit.putInt("drv_check", GlobalSystemParam.drvCheckFs);
//        edit.putInt("pic_compress", GlobalSystemParam.picCompress);
//        edit.putInt("gps_up_freq", GlobalSystemParam.uploadFreq);
//        edit.putBoolean("preview_photo", GlobalSystemParam.isPreviewPhoto);
//        edit.putBoolean("gps_upload", GlobalSystemParam.isGpsUpload);
//        edit.putBoolean("skip_spinner", GlobalSystemParam.isSkipSpinner);
//        edit.putBoolean("need_sfzh", GlobalSystemParam.isCheckFjdcSfzm);
//        edit.putStringSet("bjbd_fw", GlobalSystemParam.recBjbdFW);
//        edit.putStringSet("bjbd_catalog", GlobalSystemParam.recBjbdZl);
//        edit.putBoolean("is_rec_bj", GlobalSystemParam.isReciveBj);
//        edit.putBoolean("is_rec_text", GlobalSystemParam.isReciveText);
//        edit.putBoolean("not_notice", GlobalSystemParam.isNotNotice);
//        edit.putString("bj_ringtone", GlobalSystemParam.bjRingtone);
//        edit.putBoolean("is_conn_bjbd", GlobalSystemParam.isConnBjbd);
//        edit.apply();
//    }

    public static void loadParam(Context context, String name, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(name, value);
        edit.apply();
    }

    public static String getParam(Context context, String name, String defaultVal) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(name, defaultVal);
    }

    public static void loadParam(SharedPreferences prefs, String name, boolean value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(name, value);
        edit.apply();
    }

    /**
     * 获取保存值
     *
     * @param self
     * @param name
     * @return
     */
    public static String getSavedInfo(Context self, String name) {
        return getParam(self, name, "");
    }

    /**
     * 保存值
     *
     * @param self
     * @param name
     * @param value
     */
    public static void putSavedInfo(Context self, String name, String value) {
        loadParam(self, name, value);
    }

}
