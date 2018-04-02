package com.jwt.dao;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.bean.UpdateFile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class LoginDao {
    /**
     * 检查返回的ID是否在需更新文件的列表中
     *
     * @param list
     * @param id
     * @return
     */
    public static boolean isInUpdateFileList(List<UpdateFile> list, int id) {
        for (UpdateFile uf : list) {
            if (uf.getId().equals(String.valueOf(id))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取对应ID的文件名，用于删除临时文件
     *
     * @param list
     * @param id
     * @return
     */
    public static String getFileById(List<UpdateFile> list, int id) {
        for (UpdateFile uf : list) {
            if (uf.getId().equals(String.valueOf(id))) {
                return uf.getFileName();
            }
        }
        return null;
    }

//    /**
//     * 从数据库查询警号
//     *
//     * @param context
//     * @return
//     */
//    public static String getMjJh(Context context) {
//        String jybh = "3206";
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/querySys");
//        Cursor c = context.getContentResolver().query(CONTENT_URI, null,
//                SysDataProvider.KEY_WORD + "=?", new String[]{"YHBH"}, null);
//        if (c != null) {
//            if (c.moveToFirst()) {
//                jybh = c.getString(1);
//            }
//            c.close();
//        }
//
//        return jybh;
//    }
//
//    public static List<UpdateFile> getOldCompareUps(Context context) {
//        UpdateFile[] ar = getUpdateFileFromDb(context);
//        if (ar == null)
//            return null;
//        return compareOldAndNewVersion(ar, context);
//
//    }

//    /**
//     * 验证是否有包未安装
//     *
//     * @param context
//     * @return
//     */
//    public static int checkApkUnInstall(Context context) {
//        int row = 0;
//        UpdateFile[] ar = getUpdateFileFromDb(context);
//        if (ar == null)
//            return 0;
//        for (UpdateFile uf : ar) {
//            String packageName = uf.getPackageName();
//            int oldVersion = getApkVerion(packageName, context);
//            //Log.e(uf.getPackageName(), oldVersion + "," + uf.getVersion());
//            if (oldVersion < Integer.valueOf(uf.getVersion()) && !"com.jwt.update".equals(packageName)) {
//                // 需要更新
//                row++;
//            }
//        }
//        return row;
//    }

//    public static UpdateFile[] getUpdateFileFromDb(Context context) {
//        List<UpdateFile> list = new ArrayList<UpdateFile>();
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/querySys");
//        Cursor c = context.getContentResolver().query(CONTENT_URI, null,
//                SysDataProvider.KEY_WORD + " like 'APK:%'", null, null);
//        if (c != null) {
//            if (c.moveToFirst()) {
//                do {
//                    UpdateFile uf = new UpdateFile();
//                    String[] ar = c.getString(0).split(":");
//                    String[] ar2 = c.getString(1).split(":");
//                    uf.setPackageName(ar[1]);
//                    uf.setVersion(ar2[0]);
//                    uf.setId(ar2[1]);
//                    uf.setHashValue(ar2[2]);
//                    uf.setFileName(ar2[3]);
//                    list.add(uf);
//                } while (c.moveToNext());
//            }
//            c.close();
//        }
//        if (!list.isEmpty()) {
//            UpdateFile[] ufs = new UpdateFile[list.size()];
//            return list.toArray(ufs);
//        }
//        return null;
//    }
//
//    public static String getLoginMd5(Context context) {
//        String md5 = "";
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/querySys");
//        Cursor c = context.getContentResolver().query(CONTENT_URI, null,
//                SysDataProvider.KEY_WORD + "=?", new String[]{"LOGIN_MD5"},
//                null);
//        if (c != null) {
//            if (c.moveToFirst()) {
//                md5 = c.getString(1);
//            }
//            c.close();
//        }
//        return md5;
//    }

    public static double str2Double(String s) {
        StringBuilder str = new StringBuilder(s);
        int index = 0;
        int dot = -1;
        while ((dot = str.indexOf(".")) > -1) {
            str = str.deleteCharAt(dot);
            index++;
        }
        if (index > 1) {
            for (int i = 1; i < index; i++) {
                int d = s.lastIndexOf(".");
                s = s.substring(0, d) + s.substring(d + 1, s.length());
            }
        }
        return Double.valueOf(s);
    }

    public static int getApkVerion(String packName, Context self) {
        int version = 0;
        try {
            version = self.getPackageManager().getPackageInfo(packName, 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    public static void getInstalledApk(Context self) {
        PackageManager pm = self.getPackageManager();
        List<PackageInfo> pks = pm.getInstalledPackages(0);
        for (PackageInfo pk : pks) {
            Log.e(pk.packageName, pk.versionName);
        }
    }

    public static double getApkVerionName(String packName, Context self) {
        double version = 0.0;
        try {
            version = str2Double(self.getPackageManager().getPackageInfo(
                    packName, 0).versionName);
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    public static List<UpdateFile> compareOldAndNewVersion(UpdateFile[] ver, Context context) {
        List<UpdateFile> needUpdate = new ArrayList<UpdateFile>();
        for (UpdateFile uf : ver) {
            String packageName = uf.getPackageName();
            int oldVersion = getApkVerion(packageName, context);
            if (oldVersion < Integer.valueOf(uf.getVersion()) && !"com.jwt.main".equals(packageName)) {
                // 需要更新
                needUpdate.add(uf);
            }
        }
        // 将自身的安装放在最后
        if (!needUpdate.isEmpty() && needUpdate.size() > 1) {
            int index = -1;
            for (int i = 0; i < needUpdate.size(); i++) {
                UpdateFile updateFile = needUpdate.get(i);
                if (updateFile.getPackageName().equals(
                        context.getApplicationInfo().packageName)) {
                    index = i;
                    break;
                }
            }
            if (index > -1) {
                UpdateFile u = needUpdate.get(index);
                needUpdate.remove(index);
                needUpdate.add(u);
            }
        }
        return needUpdate;
    }

    public static int checkInstalledPackVersion(List<UpdateFile> files,
                                                Context context) {
        int row = 0;
        for (UpdateFile f : files) {
            int nb = getApkVerion(f.getPackageName(), context);
            if (nb < Integer.valueOf(f.getVersion()))
                row++;
        }
        return row;
    }

    public static Map<String, Object> objToMapObj(Object pojo) {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> classType = pojo.getClass();
        Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            // 结果集对象字段属性名称如 getString
            String rsGetMethod = "get"
                    + fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);
            Method rsGetMe = null;
            try {
                rsGetMe = classType.getMethod(rsGetMethod, new Class[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (rsGetMe == null)
                continue;
            ;
            Object value = null;
            try {
                value = rsGetMe.invoke(pojo, new Object[]{});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (value != null) {
                map.put(fieldName.toUpperCase(),
                        value);
            }
        }
        return map;
    }


//    public static int saveSerialIntoDb(String serial, ContentResolver contentRes) {
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/updateSys");
//        ContentValues values = new ContentValues();
//        values.put(SysDataOpenHelper.KEY_WORD, "SERIAL_NUMBER");
//        values.put(SysDataOpenHelper.KEY_DEFINITION, serial);
//        return contentRes.update(CONTENT_URI, values, null, null);
//    }

//    public static int saveLoginMd5IntoDb(String md5, ContentResolver contentRes) {
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/updateSys");
//        ContentValues values = new ContentValues();
//        values.put(SysDataOpenHelper.KEY_WORD, "LOGIN_MD5");
//        values.put(SysDataOpenHelper.KEY_DEFINITION, md5);
//        return contentRes.update(CONTENT_URI, values, null, null);
//    }

//    public static int saveUpdateFileListIntoDb(UpdateFile[] ups,
//                                               ContentResolver contentRes) {
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/updateSys");
//        int count = 0;
//        for (UpdateFile up : ups) {
//            ContentValues values = new ContentValues();
//            values.put(SysDataOpenHelper.KEY_WORD, "APK:" + up.getPackageName());
//            values.put(
//                    SysDataOpenHelper.KEY_DEFINITION,
//                    up.getVersion() + ":" + up.getId() + ":"
//                            + up.getHashValue() + ":" + up.getFileName());
//            count += contentRes.update(CONTENT_URI, values, null, null);
//        }
//
//        return count;
//    }
//
//    public static int saveMjInfoIntoDb(LoginMjxxBean mj,
//                                       ContentResolver contentRes) {
//        Map<String, String> mjxx = null;
//        try {
//            mjxx = CommParserXml.objToMap(mj);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (mjxx == null)
//            return 0;
//        long count = 0L;
//        Uri CONTENT_URI = Uri.parse("content://" + SysDataProvider.AUTHORITY
//                + "/updateSys");
//        for (String key : mjxx.keySet()) {
//            String value = mjxx.get(key);
//            Log.e("LoginDao-saveMjInfo", key + ":" + value);
//            ContentValues values = new ContentValues();
//            values.put(SysDataOpenHelper.KEY_WORD, key.toUpperCase());
//            values.put(SysDataOpenHelper.KEY_DEFINITION,
//                    (TextUtils.isEmpty(value) ? "" : value));
//            count += contentRes.update(CONTENT_URI, values, null, null);
//        }
//        return (int) count;
//    }


    @SuppressLint("NewApi")
    public static String getSerial(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
        String serial = tm.getDeviceId();// "862020981638724";
        if (serial != null)
            return serial.toUpperCase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return Build.SERIAL.toUpperCase();
        }
        return "";
    }

    public static void sendData(Handler mHandler, int err, int what, int step) {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = err;
        msg.what = what;
        msg.arg2 = step;
        mHandler.sendMessage(msg);
    }
}
