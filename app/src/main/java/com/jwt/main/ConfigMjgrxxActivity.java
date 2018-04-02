package com.jwt.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.jwt.activity.ActionBarListActivity;

import com.jwt.adapter.TwoLineSelectAdapter;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ConfigMjgrxxActivity extends ActionBarListActivity {
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_no_button_list);
        self = this;
        setTitle("警务通基本信息");
        Map<String, String> map = GlobalMethod.getSavedMjInfo(self);
        ArrayList<TwoLineSelectBean> list = changeMapIntoZh(map);
        TwoLineSelectAdapter ard = new TwoLineSelectAdapter(this,
                R.layout.two_line_list_item, list, false);
        getListView().setAdapter(ard);
    }

    private ArrayList<TwoLineSelectBean> changeMapIntoZh(Map<String, String> map) {
        ArrayList<TwoLineSelectBean> list = new ArrayList<TwoLineSelectBean>();
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> entry : set) {
            String zh = "";
            if (TextUtils.equals(entry.getKey(), GlobalConstant.YHBH))
                zh = "民警警号";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.YBMBH))
                zh = "大队代码";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.KSBMBH))
                zh = "中队代码";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.XM))
                zh = "民警姓名";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.FYJG))
                zh = "行政复议机关";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.SSJG))
                zh = "行政诉讼机关";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.BMMC))
                zh = "值勤机关名称";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.JKYH))
                zh = "缴款银行";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.CLJG1))
                zh = "机关信息";
            else if (TextUtils.equals(entry.getKey(), GlobalConstant.DWDZ))
                zh = "单位地址";
            else if (TextUtils.equals(entry.getKey(),
                    GlobalConstant.GRXX_CARD_READER_NAME))
                zh = "身份证读卡器";
            else if (TextUtils.equals(entry.getKey(),
                    GlobalConstant.GRXX_CARD_READER_ADDRESS))
                zh = "读卡器地址";
            if (!TextUtils.isEmpty(zh))
                list.add(new TwoLineSelectBean(zh, entry.getValue(), true));
        }
        String defPrinterName = GlobalSystemParam.getSavedInfo(this, GlobalConstant.GRXX_PRINTER_NAME);
        String defPrinterAddress = GlobalSystemParam.getSavedInfo(this, GlobalConstant.GRXX_PRINTER_ADDRESS);
        if(!TextUtils.isEmpty(defPrinterName))
            list.add(new TwoLineSelectBean("蓝牙打印机", defPrinterName, true));
        if(!TextUtils.isEmpty(defPrinterAddress))
            list.add(new TwoLineSelectBean("打印机地址", defPrinterAddress, true));
        list.add(new TwoLineSelectBean("非现场限时", GlobalSystemParam.unsend_fxc_hours + "小时", true));
        list.add(new TwoLineSelectBean("软件版本", "V" + getApkVerion(), true));
        //获取手机基本型号
        list.add(new TwoLineSelectBean("设备型号", getPhoneModel(), true));
        SCell cell = getCellInfo(ConfigMjgrxxActivity.this);
        if(cell != null){
            try {
                list.add(new TwoLineSelectBean("基站信息", cell.toJSON().toString(), true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private int getApkVerion() {
        String packageName = self.getPackageName();
        int version = 0;
        try {
            version = self.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    /**
     * 获取基站信息
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     */
    @SuppressLint("MissingPermission")
    public SCell getCellInfo(Context ctx) {
        SCell cell = new SCell();
        TelephonyManager tm = null;
        try {
            tm = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            return null;
        }
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        String IMSI = tm.getSubscriberId();
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                cell.NETWORK_TYPE = "CHINA MOBILE";

                GsmCellLocation location = (GsmCellLocation) tm
                        .getCellLocation();
                if (location == null) {
                    cell = null;
                } else {
                    String operator = tm.getNetworkOperator();
                    if (operator.length() > 4) {
                        int mcc = Integer.parseInt(operator.substring(0, 3));
                        int mnc = Integer.parseInt(operator.substring(3));
                        int cid = location.getCid();
                        int lac = location.getLac();
                        cell.MCC = mcc;
                        cell.MNC = mnc;
                        cell.LAC = lac;
                        cell.CID = cid;
                    } else {
                        cell = null;
                    }
                }
            } else if (IMSI.startsWith("46001")) {
                cell.NETWORK_TYPE = "CHINA UNICOM";
                GsmCellLocation location = (GsmCellLocation) tm
                        .getCellLocation();
                if (location == null) {
                    cell = null;
                } else {
                    String operator = tm.getNetworkOperator();
                    if (operator.length() > 4) {
                        int mcc = Integer.parseInt(operator.substring(0, 3));
                        int mnc = Integer.parseInt(operator.substring(3));
                        int cid = location.getCid();
                        int lac = location.getLac();
                        cell.MCC = mcc;
                        cell.MNC = mnc;
                        cell.LAC = lac;
                        cell.CID = cid;
                    } else {
                        cell = null;
                    }
                }
            } else if (IMSI.startsWith("46003")) {
                cell.NETWORK_TYPE = "CHINA TELECOM";
                CdmaCellLocation location = (CdmaCellLocation) tm
                        .getCellLocation();
                if (location == null) {
                    cell = null;
                } else {
                    String operator = tm.getNetworkOperator();
                    if (operator.length() > 4) {
                        int mcc = Integer.parseInt(operator.substring(0, 3));
                        int mnc = Integer.parseInt(operator.substring(3));
                        int cid = location.getBaseStationId();
                        int lac = location.getNetworkId();
                        cell.MCC = mcc;
                        cell.MNC = mnc;
                        cell.LAC = lac;
                        cell.CID = cid;
                    } else {
                        cell = null;
                    }
                }
            } else {
                // cell.NETWORK_TYPE = "UNDENTIFIED";
                cell = null;
            }
        } else {
            cell = null;
        }
        return cell;
    }

    /**
     * 基站信息
     */
    class SCell {

        public String NETWORK_TYPE;

        public int MCC;

        public int MNC;

        public int LAC;

        public int CID;

        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("network_type", NETWORK_TYPE);
            json.put("mcc", MCC);
            json.put("MNC", MNC);
            json.put("LAC", LAC);
            json.put("CID", CID);
            return json;
        }
    }

    /**
     * 获取手机型号
     * <p>
     * android.os.Build提供以下信息：
     * String  BOARD   The name of the underlying board, like "goldfish".
     * String  BRAND   The brand (e.g., carrier) the software is customized for, if any.
     * String  DEVICE  The name of the industrial design.
     * String  FINGERPRINT     A string that uniquely identifies this build.
     * String  HOST
     * String  ID  Either a changelist number, or a label like "M4-rc20".
     * String  MODEL   The end-user-visible name for the end product.
     * String  PRODUCT     The name of the overall product.
     * String  TAGS    Comma-separated tags describing the build, like "unsigned,debug".
     * long    TIME
     * String  TYPE    The type of build, like "user" or "eng".
     * String  USER
     */
    public String getPhoneModel() {
        return Build.MODEL + ";" + Build.BOARD
                + ";" + Build.BRAND
                + ";" + Build.DEVICE
                + ";" + Build.PRODUCT
                + ";" + Build.ID;
    }

    /**
     * 获取手机号码，一般获取不到
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * <p>
     * 要想获取更多电话、数据、移动网络相关信息请查阅TelephonyManager资料
     */
    @SuppressLint("MissingPermission")
    public String getLineNum(Context ctx) {
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            strResult = telephonyManager.getLine1Number();
        }
        return strResult;
    }

    /**
     * 获取移动用户标志，IMSI
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    @SuppressLint("MissingPermission")
    public String getSubscriberId(Context ctx) {
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            strResult = telephonyManager.getSubscriberId();
        }
        return strResult;
    }

    /**
     * 获取设备ID
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    @SuppressLint("MissingPermission")
    public String getDeviceID(Context ctx) {
        String strResult = null;
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            strResult = telephonyManager.getDeviceId();
        }
        if (strResult == null) {
            strResult = Settings.Secure.getString(ctx.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return strResult;
    }

    /**
     * 获取SIM卡号
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    @SuppressLint("MissingPermission")
    public String getSim(Context ctx) {
        String strResult = "";
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            strResult = telephonyManager.getSimSerialNumber();
        }
        return strResult;
    }

    /**
     * 获取Wifi Mac地址
     * <p>
     * 要想获取更多Wifi相关信息请查阅WifiInfo资料
     * <p>
     * 用到的权限：
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public String getMac(Context ctx) {

        WifiManager wifiManager = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wi = wifiManager.getConnectionInfo();
            return wi.getMacAddress();
        }
        return null;
    }

}
