package com.jwt.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.github.chrisbanes.photoview.PhotoView;
import com.jwt.adapter.SpinnerCustomAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.UpdateFile;
import com.jwt.globalquery.ZhcxQueryResultBean;
import com.jwt.update.App;
import com.jwt.update.R;
import com.jwt.web.WebQueryResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.objectbox.BoxStore;

import static android.content.Context.MODE_PRIVATE;

public class GlobalMethod {

    public static String ifNull(String s) {
        return (TextUtils.isEmpty(s)) ? "" : s.trim();
    }

    public static boolean isDouble(String str) {
        final int len = str.length();
        int dot = 0;
        for (int i = 0; i < len; i++) {
            int ic = (int) str.charAt(i);
            if (ic == 46) {
                dot++;
                if (dot > 1)
                    return false;
            } else if (ic < 48 || ic > 59) {
                return false;
            }
        }
        return true;
    }

    public static int power(int s1, int s2) {
        int r = 1;
        for (int i = 0; i < s2; i++) {
            r *= s1;
        }
        return r;
    }

    /**
     * 判断是否为纯数字
     *
     * @param s
     * @return
     */
    public static boolean isNumberOrAZ(String s) {
        boolean result = false;
        Pattern pattern = Pattern.compile("[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(s.trim());
        if (matcher.find()) {
            result = matcher.end() == s.trim().length();
        }
        return result;
    }

    /**
     * 是否为汉字、字母或数字
     *
     * @param s
     * @return 没有非法字符返回真
     */
    public static boolean isChinaOrAz(String s) {
        Pattern pattern = Pattern.compile("^([\u4e00-\u9fa5]|[A-Za-z0-9])+$");
        // 非汉字^([A-Za-z0-9])+$
        // 包括汉字^([\u4e00-\u9fa5]|[A-Za-z0-9])+$
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 是否包含中文字符
     *
     * @param s
     * @return
     */
    public static boolean hasChina(String s) {
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.find();
    }

    /**
     * 根据违法代码查询记分数
     *
     * @param wfxw
     * @return
     */
    public static int getJfs(String wfxw) {
        int jf = Integer.valueOf(wfxw.substring(1, 2));
        if (jf == 7)
            jf = 12;
        return jf;

    }

    /**
     * 从键、值列表中取字符串列表
     *
     * @param l
     * @param kov
     * @return
     */
    public static List<String> getListFromKeyValues(List<KeyValueBean> l,
                                                    int kov) {
        List<String> list = new ArrayList<String>();
        if (l != null && l.size() > 0) {
            for (KeyValueBean kv : l) {
                list.add(kov == GlobalConstant.KEY ? kv.getKey() : kv
                        .getValue());
            }

        }
        return list;
    }

    /**
     * 在字符串前补零
     *
     * @param s   需要补零的字符串
     * @param len 补零后的长度
     * @return
     */
    public static String paddingZero(String s, int len) {
        s = s == null ? "" : s.trim();
        int slen = s.length();
        for (int i = slen; i < len; i++) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 显示一个对话框
     *
     * @param title
     * @param message
     * @param bt
     * @param context
     */
    public static void showDialog(String title, String message, String bt,
                                  Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        String button1String = bt;
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // eatenByGrue();
            }
        });
        ad.show();
    }

    public static void showPicDialog(String url, Context context) {
//        final Dialog jpgDialog = new Dialog(context);
//        jpgDialog.setContentView(R.layout.jpgdialog);
//        ImageView bmImage = (ImageView) jpgDialog.findViewById(R.id.image);
//        byte[] b = Base64.decode(url, Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//        if (bitmap != null)
//            bmImage.setImageBitmap(bitmap);
//        Button okDialogButton = (Button) jpgDialog
//                .findViewById(R.id.okdialogbutton);
//        okDialogButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                jpgDialog.dismiss();
//            }
//        });
//        jpgDialog.show();
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.show_photo)
                .customView(R.layout.jpgdialog, wrapInScrollView)
                .positiveText("确定")
                .show();
        PhotoView bmImage = (PhotoView) dialog.findViewById(R.id.image);
        byte[] b = Base64.decode(url, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        if (bitmap != null)
            bmImage.setImageBitmap(bitmap);
    }

//    public static Dialog showPicFileDialog(Bitmap bitmap, Context context,
//                                           View.OnClickListener click) {
//        Dialog jpgDialog = new Dialog(context);
//        jpgDialog.setContentView(R.layout.jpgdialog);
//        ImageView bmImage = (ImageView) jpgDialog.findViewById(R.id.image);
//        bmImage.setImageBitmap(bitmap);
//        Button okDialogButton = (Button) jpgDialog
//                .findViewById(R.id.okdialogbutton);
//        okDialogButton.setOnClickListener(click);
//        jpgDialog.show();
//        return jpgDialog;
//    }

//    public static void showCardReadDialog(PersonInfo person, Context context) {
//        final Dialog jpgDialog = new Dialog(context);
//        jpgDialog.setContentView(R.layout.card_demo);
//        MyView myview = (MyView) jpgDialog.findViewById(R.id.card_view1);
//        myview.mPerson = person;
//        myview.invalidate();
//        jpgDialog.show();
//    }
//
//    public static Bitmap returnBitMap(String url) {
//        Bitmap bitmap = null;
//        int status = HttpStatus.SC_BAD_REQUEST;
//        BasicHttpParams httpParameters = new BasicHttpParams();
//        HttpClient client = new DefaultHttpClient(httpParameters);
//
//        HttpGet request = new HttpGet(url);
//        try {
//
//            HttpResponse response = client.execute(request);
//            status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                // long fileSize = response.getEntity().getContentLength();
//                BufferedInputStream in = new BufferedInputStream(response
//                        .getEntity().getContent());
//                bitmap = BitmapFactory.decodeStream(in);
//                in.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    // public static Bitmap returnBitMap(String url) {
    // URL myFileUrl = null;
    // Bitmap bitmap = null;
    // try {
    // myFileUrl = new URL(url);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // }
    // try {
    // HttpURLConnection conn = (HttpURLConnection) myFileUrl
    // .openConnection();
    // //conn.setDoInput(true);
    // conn.connect();
    // InputStream is = conn.getInputStream();
    // bitmap = BitmapFactory.decodeStream(is);
    // is.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return bitmap;
    // }

    /**
     * 显示一个不可以取消的对话框
     *
     * @param title
     * @param message
     * @param bt1
     * @param listener
     * @param context
     */
    public static void showDialogWithListener(String title, String message,
                                              String bt1,
                                              OnClickListener listener,
                                              Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(bt1, listener);
        ad.setCancelable(false);
        ad.show();
    }

    /**
     * 显示一个可以取消的确认对话框
     *
     * @param title
     * @param message
     * @param bt1
     * @param listener
     * @param context
     */
    public static void showCanCancelDialogWithListener(String title,
                                                       String message, String bt1,
                                                       OnClickListener listener,
                                                       Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(bt1, listener);
        ad.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    public static void showDialogTwoListener(String title, String message,
                                             String bt1, String bt2,
                                             OnClickListener listener,
                                             Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(bt1, listener);
        ad.setNegativeButton(bt2, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        ad.setCancelable(false);
        ad.show();
    }

    public static void showErrorDialog(String message, Context context) {
        showDialog("错误信息", message, "确定", context);
    }

    public static void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 根据列表生成下拉框,这是三个下拉框共用的方法
     *
     * @param spinner
     * @param list
     */
    public static void changeAdapter(Spinner spinner, List<KeyValueBean> list,
                                     Activity context) {
        changeAdapter(spinner, list, context, false);
    }

    /**
     * 填充下拉框，第一个是否为空白
     *
     * @param spinner
     * @param list
     * @param context
     * @param isFirstWhite 真，第一个为空白
     */
    public static void changeAdapter(Spinner spinner, List<KeyValueBean> list,
                                     Activity context, boolean isFirstWhite) {
        SpinnerCustomAdapter adapter = (SpinnerCustomAdapter) spinner
                .getAdapter();
        if (list != null && list.size() > 0) {
            adapter = new SpinnerCustomAdapter(context, copyList(list));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            if (isFirstWhite)
                adapter.insert(new KeyValueBean("", ""), 0);
        } else {
            if (adapter == null) {
                // 第一次加载数据,需加载的数据为空
                adapter = new SpinnerCustomAdapter(context,
                        new ArrayList<KeyValueBean>());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                adapter.clear();
            }
        }

        adapter.notifyDataSetChanged();
    }

    public static void changeAdapter(Spinner spinner, KeyValueBean kv,
                                     Activity context, int v) {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        list.add(kv);
        changeAdapter(spinner, list, context, false);
    }

    private static List<KeyValueBean> copyList(List<KeyValueBean> list) {
        List<KeyValueBean> destList = new ArrayList<KeyValueBean>();
        if (list != null && !list.isEmpty()) {
            for (KeyValueBean keyValueBean : list) {
                destList.add(keyValueBean);
            }
        }
        return destList;
    }

    public static void clearSpinnerAdapter(Spinner sp, Activity context) {
        SpinnerCustomAdapter ad = new SpinnerCustomAdapter(context,
                new ArrayList<KeyValueBean>());
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp.setAdapter(ad);
        ad.clear();
    }

    /**
     * 改变下拉框的选择项
     *
     * @param sp       下拉框
     * @param value    值
     * @param keyOrVal 关键字或值
     */
    public static boolean changeSpinnerSelect(Spinner sp, String value,
                                              int keyOrVal, boolean animate) {
        SpinnerCustomAdapter adapter = (SpinnerCustomAdapter) sp.getAdapter();
        if (adapter == null || TextUtils.isEmpty(value))
            return false;
        for (int i = 0; i < adapter.getCount(); i++) {
            KeyValueBean kv = adapter.getItem(i);
            String v = keyOrVal == GlobalConstant.KEY ? kv.getKey() : kv
                    .getValue();
            if (TextUtils.equals(v, value)) {
                sp.setSelection(i, animate);
                return true;
            }
        }
        return false;
    }

    public static boolean changeSpinnerSelect(Spinner sp, String value,
                                              int keyOrVal) {
        return changeSpinnerSelect(sp, value, keyOrVal, false);
    }

    /**
     * 根据关键字在列表中取出位置
     *
     * @param list
     * @param key
     * @return
     */
    public static int getPositionByKey(List<KeyValueBean> list, String key) {
        int position = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (TextUtils.equals(list.get(i).getKey(), key)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public static KeyValueBean findKvByKvlist(List<KeyValueBean> list, String key) {
        for (KeyValueBean kv : list) {
            if (kv.getKey().equals(key))
                return kv;
        }
        return null;
    }

    /**
     * 根据值在列表中取出位置
     *
     * @param list
     * @param value
     * @return
     */
    public static int getPositionByValue(List<KeyValueBean> list, String value) {
        int position = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (TextUtils.equals(list.get(i).getValue(), value)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public static String getStringFromKVListByKey(List<KeyValueBean> list,
                                                  String key) {
        if (list == null || list.isEmpty())
            return null;
        String value = "";
        for (KeyValueBean kv : list) {
            if (TextUtils.equals(kv.getKey(), key)) {
                value = kv.getValue();
                break;
            }
        }
        return value;
    }

    /**
     * 指定数据是否在列表中存在
     *
     * @param list
     * @param value
     * @return
     */
    public static boolean isInKVListByValue(List<KeyValueBean> list,
                                            String value) {
        boolean position = false;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (TextUtils.equals(list.get(i).getValue(), value)) {
                    position = true;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 从查询结果中取字段值，用于综合查询
     *
     * @param field
     * @param qrb
     * @return
     */
    public static String getValueFromResultBeanByField(String field,
                                                       ArrayList<ZhcxQueryResultBean> qrb) {
        String result = null;
        for (ZhcxQueryResultBean queryResultBean : qrb) {
            if (TextUtils.equals(field, queryResultBean.getField())) {
                result = queryResultBean.getValue();
                break;
            }
        }
        return result;
    }

    /**
     * 对界面下拉框中取出选择项的键或实际值
     *
     * @param sp   下拉框控件
     * @param list 填充下拉框的键值列表
     * @return
     */
    public static String getKeyFromSpinnerSelected(Spinner sp,
                                                   ArrayList<KeyValueBean> list, int keyOrVal) {
        int pos = sp.getSelectedItemPosition();
        if (pos < 0)
            return "";
        KeyValueBean kv = list.get(pos);
        return keyOrVal == GlobalConstant.KEY ? kv.getKey() : kv.getValue();
    }

    /**
     * 对界面下拉框中取出选择项的键或实际值
     *
     * @param sp
     * @param keyOrVal
     * @return
     */
    public static String getKeyFromSpinnerSelected(Spinner sp, int keyOrVal) {
        SpinnerCustomAdapter adapter = (SpinnerCustomAdapter) sp.getAdapter();
        if (adapter == null || sp.getSelectedItemPosition() < 0
                || adapter.getArray() == null || adapter.getArray().isEmpty()) {
            return "";
        }
        KeyValueBean kv = adapter.getItem(sp.getSelectedItemPosition());
        int pos = sp.getSelectedItemPosition();
        if (pos < 0)
            return "";
        return keyOrVal == GlobalConstant.KEY ? kv.getKey() : kv.getValue();
    }

    public static String stringAddLong(String s, int i) {
        return String.valueOf(Long.valueOf(s) + i);
    }

    public static boolean showOfflineNotQuery(Context context) {
        boolean l = isOnline();
        if (!l) {
            GlobalMethod.showErrorDialog("此操作离线模式下不能完成!", context);
        }
        return l;
    }

    /**
     * 根据控件的光标位置确定改变时间或日期
     *
     * @param edDateTime
     * @param context
     */
    public static void changeTime(final EditText edDateTime, Context context) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Calendar calendar = Calendar.getInstance();
        // 读取控件的时间
        Date d = null;
        try {
            d = sdf.parse(edDateTime.getText().toString());
        } catch (Exception e) {
        }
        if (d == null) {
            Toast.makeText(context, "日期格式不正确", Toast.LENGTH_LONG).show();
            calendar.setTime(new Date());
        } else
            calendar.setTime(d);

        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                edDateTime.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    /**
     * 改变日期
     *
     * @param edDateTime
     * @param context
     */
    public static void changeDate(final EditText edDateTime, Context context) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final Calendar calendar = Calendar.getInstance();
        // 读取控件的时间
        Date d = null;
        try {
            d = sdf.parse(edDateTime.getText().toString());
        } catch (Exception e) {
        }
        if (d == null) {
            Toast.makeText(context, "日期格式不正确", Toast.LENGTH_LONG).show();
            calendar.setTime(new Date());
        } else
            calendar.setTime(d);
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edDateTime.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // public static void makeNotification(String mess, int jb, Context context,
    // QueryResultMapList ql) {
    // int icon = android.R.drawable.stat_notify_chat;
    // if (jb == 0)
    // icon = R.drawable.warn_green;
    // else if (jb == 1)
    // icon = R.drawable.warn_yellow;
    // else if (jb == 2)
    // icon = R.drawable.warn_red;
    // String tickerText = "你有新的消息";
    // long when = System.currentTimeMillis();
    // Notification notification = new Notification(icon, tickerText, when);
    // Uri ringURI = Uri.fromFile(new File(
    // "/system/media/audio/notifications/Bells.ogg"));
    // notification.sound = ringURI;
    //
    // String svcName = Context.NOTIFICATION_SERVICE;
    // NotificationManager nm = (NotificationManager) context
    // .getSystemService(svcName);
    //
    // RemoteViews contentView = new RemoteViews(context.getPackageName(),
    // R.layout.notification);
    // contentView.setImageViewResource(R.id.image, R.drawable.mess);
    // contentView.setTextViewText(R.id.text1, "系统比对通知");
    // contentView.setTextViewText(R.id.text2, mess);
    // notification.contentView = contentView;
    //
    // Intent notificationIntent = new Intent(context,
    // QueryResultActivity.class);
    // notificationIntent.putExtra(QueryResultActivity.SERIALNAME, ql);
    // notificationIntent.putExtra(QueryResultActivity.FROMNOTICE, true);
    // notificationIntent.putExtra(QueryResultActivity.NOTICEID, jb);
    // PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
    // notificationIntent, 0);
    // notification.contentIntent = contentIntent;
    //
    // notification.when = System.currentTimeMillis();
    // notification.tickerText = mess;
    // nm.notify(jb, notification);
    // }

//    public static String checkWarinMessage(QueryResultMapList queryObject) {
//        String message = null;
//        String yxqz = queryObject.findValueByField(0, "YXQZ");
//        String hphm = queryObject.findValueByField(0, "$HPHM$");
//        String cllx = queryObject.findValueByField(0, "CLLX1");
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date d = sdf2.parse(yxqz);
//            if (d.getTime() < new Date().getTime()) {
//                // 验车过期了
//                message = cllx + " " + hphm + "验车过期";
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return message;
//    }

    /**
     * 将XML文件内容存入文件
     *
     * @param fileName
     * @param txt
     */
    public static void writeInDisk(String fileName, String txt) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(txt);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public static String getErrorMessage(WebQueryResult<LoginMessage> lm) {
    // String err = "";
    // if (lm == null || lm.getStatus() != HttpStatus.SC_OK) {
    // // 服务器返回数据正确性验证， 网络状态正常
    // return "网络状态异常";
    // }
    // int code = lm.getResult().getCode();
    // // 返回数据不正确,显示错误信息并退出程序
    // // 需要重新对话框的确定按扭监听
    // if (code != 0) {
    // return lm.getResult().getMessage();
    // }
    //
    // if (lm.getResult().getFields().size() != lm.getResult().getValues()
    // .size()) {
    // return "数据传输错误,不能正常处罚,按确定退出重新登录";
    //
    // }
    // return err;
    // }

    //    public static <E> JSONObject getErrorMessageFromJson(WebQueryResult<E> re, String sjson) {
//        JSONObject json = null;
//        try {
//            json = new JSONObject(sjson);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (json == null) {
//            re.setStMs("查询出现错误");
//            return null;
//        }
//        String err = json.optString("err");
//        if (!TextUtils.isEmpty(json.optString("err"))) {
//            if (TextUtils.isDigitsOnly(err)) {
//                re.setStatus(Integer.valueOf(err));
//            } else {
//                re.setStMs(err);
//            }
//            return null;
//        }
//        re.setStatus(200);
//        return json;
//    }
//
    public static <E> String getErrorMessageFromWeb(WebQueryResult<E> webResult) {
        String err = "";
        if (webResult == null)
            return "网络连接失败，请检查配查或与管理员联系！";
        String ms = webResult.getStMs();
        if (!TextUtils.isEmpty(ms))
            return ms;
        if (webResult.getStatus() != HttpURLConnection.HTTP_OK) {
            // 服务器返回数据正确性验证， 网络状态正常
            if (webResult.getStatus() == 204) {
                return "未查询到符合条件的记录！";
            } else if (webResult.getStatus() == 500) {
                return "服务不能提供，请与管理员联系！";
            } else if (webResult.getStatus() == 404) {
                return "该查询在服务器不能实现，请与管理员联系！";
            } else {
                return "服务器出现未知错误";
            }
        }

        if (webResult.getResult() == null) {
            return "未能获取数据";
        }
        return err;
    }

    /**
     * 根据身份证号计算年龄
     *
     * @param sfzh
     * @return
     */
    public static int countAgeFromSfzh(String sfzh) {
        int age = 0;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        String borth = sfzh.substring(6, 10);
        if (TextUtils.isDigitsOnly(borth)) {
            age = year - Integer.valueOf(borth);
        }
        return age;
    }

    /**
     * 根据身份证号取性别代码
     *
     * @param sfzh
     * @return 1 为男，2 为女
     */
    public static String getXbFromSfzh(String sfzh) {
        String xb = "0";
        String xbsb = sfzh.substring(16, 17);
        if (TextUtils.isDigitsOnly(xbsb)) {
            xb = Integer.valueOf(xbsb) % 2 == 0 ? "2" : "1";
        }
        return xb;
    }

    /**
     * 根据图片的名称取出对应的资源ID号
     *
     * @param name 图片文件名
     * @return 资源ID号
     */
    public static int getImageResouseByName(String name) {
        Field[] fields = R.drawable.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().startsWith("$"))
                continue;
            try {
                int index = field.getInt(R.drawable.class);
                if (name.equals(field.getName().toLowerCase()))
                    return index;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 根据输入法名称获得输入法的资源号
     *
     * @param name
     * @return
     */
    public static int getInputMethodByName(String name) {
        if (name != null && !TextUtils.isEmpty(name)) {
            Field[] fields = android.text.InputType.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(name.toUpperCase())) {
                    try {
                        return field.getInt(android.text.InputType.class);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return android.text.InputType.TYPE_CLASS_TEXT;
    }

    /**
     * 读取文本文件的内容
     *
     * @param fileName
     * @return
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        return readFileContent(file);
    }

    public static String readFileContent(File file) {
        String result = "";
        if (!file.exists())
            return result;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "utf-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                result += s;
            }
            br.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询一特定的值在数组中的位置,不区分大小写
     *
     * @param array
     * @param value
     * @return
     */
    public static int getPositionFromArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (value.toUpperCase().equals(array[i].toUpperCase()))
                return i;
        }
        return -1;
    }

    /**
     * 本方法用于综合查询返回值的取值
     *
     * @param names
     * @param content
     * @param fieldName
     * @return
     */
    public static String getValueByFieldName(String[] names, String[] content,
                                             String fieldName) {
        int pos = GlobalMethod.getPositionFromArray(names, fieldName);
        return pos > -1 ? content[pos] : "";
    }

    /**
     * 打开GPS功能，该方法只能用于2.2以下，或是MOTO
     *
     * @param context
     */
    public static void toggleGPS(final Context context) {
        showDialogTwoListener("系统提示", "因业务需要，请打开GPS，点确定打开", "确定", "取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent gpsIntent = new Intent();
//                gpsIntent.setClassName("com.android.settings",
//                        "com.android.settings.widget.SettingsAppWidgetProvider");
//                gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
//                gpsIntent.setData(Uri.parse("custom:3"));
//                try {
//                    PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
//                } catch (CanceledException e) {
//                    e.printStackTrace();
//                }
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // The Android SDK doc says that the location settings activity
                    // may not be found. In that case show the general settings.
                    // General settings activity
                    intent.setAction(Settings.ACTION_SETTINGS);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                    }
                }
            }
        }, context);

    }

    /**
     * 更新系统配置
     */
    //public static void updateSysConfig() {
    //   updateSysConfig(GlobalData.connCata);
    //}

//    /**
//     * 从服务器中更新系统配置参数
//     */
//    public static void updateSysConfig(ConnCata conn) {
//        Log.e("GlobalMethod", "update system config");
//        WebQueryResult<List<KeyValueBean>> sysConfigs = RestfulDaoFactory
//                .getDao(conn).restfulGetSysConfig();
//        String err = GlobalMethod.getErrorMessageFromWeb(sysConfigs);
//        if (TextUtils.isEmpty(err)) {
//            List<KeyValueBean> list = sysConfigs.getResult();
//            for (KeyValueBean kv : list) {
//                String key = kv.getKey().trim();
//                if (TextUtils.equals(key, "GPS_UPLOAD")) {
//                    // 如果需要开，则打开，如果不需要开，系统已打开则不管他
//                    GlobalSystemParam.isGpsUpload = TextUtils.equals(kv
//                            .getValue().trim(), "1") ? true : false;
//                } else if (TextUtils.equals(key, "CHECK_FJDC_SFZH")) {
//                    GlobalSystemParam.isCheckFjdcSfzm = TextUtils.equals(kv
//                            .getValue().trim(), "1") ? true
//                            : GlobalSystemParam.isCheckFjdcSfzm;
//                } else if (TextUtils.equals(key, "drvCheckFs")) {
//                    int val = Integer.valueOf(kv.getValue().trim());
//                    if (GlobalSystemParam.drvCheckFs < val)
//                        GlobalSystemParam.drvCheckFs = val;
//                } else if (TextUtils.equals(key, "vehCheckFs")) {
//                    int val = Integer.valueOf(kv.getValue().trim());
//                    if (GlobalSystemParam.vehCheckFs < val)
//                        GlobalSystemParam.vehCheckFs = val;
//                } else if (TextUtils.equals(key, "unsendFxcHours")) {
//                    if (!TextUtils.isEmpty(kv.getValue()) && TextUtils.isDigitsOnly(kv.getValue())) {
//                        int val = Integer.valueOf(kv.getValue().trim());
//                        GlobalSystemParam.unsend_fxc_hours = val;
//                    }
//                }
//            }
//        }
//    }
    public static void sendInfoToHandler(Handler mHandler, String info,
                                         int what, int step) {
        Message m = mHandler.obtainMessage();
        m.what = what;
        m.arg1 = step;
        if (!TextUtils.isEmpty(info)) {
            Bundle data = new Bundle();
            data.putString("info", info);
            m.setData(data);
        }
        mHandler.sendMessage(m);
    }

    public static <T> WebQueryResult<List<T>> webXmlStrToListObj(
            WebQueryResult<String> re, Class<T> cl) {
        WebQueryResult<List<T>> reza = new WebQueryResult<List<T>>();
        reza.setStatus(re.getStatus());
        try {
            if (!TextUtils.isEmpty(re.getResult())) {
                List<T> g = CommParserXml.ParseXmlToListObj(re.getResult(), cl);
                reza.setResult(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reza;
    }

    public static <T> WebQueryResult<T> webXmlStrToObj(
            WebQueryResult<String> re, Class<T> cl) {
        WebQueryResult<T> reza = new WebQueryResult<T>();
        reza.setStatus(re.getStatus());
        try {
            if (re != null && !TextUtils.isEmpty(re.getResult())) {
                T g = CommParserXml.parseXmlToObj(re.getResult(), cl);
                reza.setResult(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reza;
    }

    /**
     * 按指定压缩比压缩
     *
     * @param bitmap
     * @param picFile
     * @param compress 60，80，100
     * @return
     */
    public static boolean savePicIntoFile(Bitmap bitmap, File picFile, int compress) {
        try {
            FileOutputStream fo = new FileOutputStream(picFile, false);
            bitmap.compress(CompressFormat.JPEG,
                    compress, fo);
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 对于大于五百KB的文件进行压缩
     *
     * @param picFile
     * @return 是否被压缩过
     */
    public static int savePicLowFiftyByte(File picFile) {
        int count = 0;
        while (picFile.length() > 500 * 1024) {
            Bitmap bitmap = getImageFromFile(picFile.getAbsolutePath());
            savePicIntoFile(bitmap, picFile, 80);
            count++;
        }
        return count;
    }

    /**
     * 按系统默压缩比压缩文件
     *
     * @param bitmap
     * @param picFile
     * @return
     */
    public static boolean savePicIntoFile(Bitmap bitmap, File picFile) {
        return savePicIntoFile(bitmap, picFile, GlobalSystemParam.picCompress);
    }

    public static String savePicIntoSmall(Bitmap smallImg, File picDir) {
        String smallPath = "";
        if (smallImg != null) {
            try {
                if (!picDir.exists())
                    picDir.mkdirs();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());
                File f = new File(picDir, timeStamp + ".jpg");
                FileOutputStream fo = new FileOutputStream(f);
                smallImg.compress(CompressFormat.JPEG,
                        GlobalSystemParam.picCompress, fo);
                fo.close();
                smallPath = f.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return smallPath;
    }

    public static String getBitmapFilePath(ContentResolver cr, Uri uri) {
        String bigFile = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        String id = uri.getLastPathSegment();
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, MediaStore.Images.Media._ID + ">=?",
                new String[]{id}, MediaStore.Images.Media._ID);
        int column_index_data = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToLast())
            bigFile = cursor.getString(column_index_data);
        cursor.close();
        return bigFile;
    }

//    /**
//     * 缩放图片至设备宽度大小并保存文件
//     *
//     * @param bigFile
//     * @param small
//     */
//    public static Bitmap scalePicIntoDeviceWidthSaveFile(File bigFile,
//                                                         File small, Activity context) {
//        Point px = getDevicePixel(context);
//        int targetW = Math.min(px.x, px.y) - 50;
//        // Get the dimensions of the bitmap
//        Options bmOptions = new Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(bigFile.getAbsolutePath(), bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        Log.e("scalePicIntoDeviceWidth", "w: " + photoW + " h: " + photoH
//                + " tw: " + px.x + " th: " + px.y);
//        int pwh = Math.max(photoW, photoH);
//
//        int scaleFactor = pwh / targetW;
//        Log.e("scalePicIntoDeviceWidth", "pwh: " + pwh + " targetW: " + targetW
//                + " sf: " + scaleFactor);
//        scaleFactor = getPower(scaleFactor, false);
//
//        Log.e("scalePicIntoDeviceWidth", "scaleFactor: " + scaleFactor);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//        Bitmap bitmap = BitmapFactory.decodeFile(bigFile.getAbsolutePath(),
//                bmOptions);
//        savePicIntoFile(bitmap, small);
//        return bitmap;
//    }

    private static int getPower(int scale, boolean isLow) {
        for (int i = 0; i < 10; i++) {
            int pl = (int) Math.pow(2, i);
            int ph = (int) Math.pow(2, i + 1);
            if (scale >= pl && scale < ph)
                return isLow ? pl : ph;

        }
        return 1;
    }

    /**
     * 创建图片文件
     *
     * @param context
     * @param isPrivate
     * @return
     * @throws IOException
     */
    public static File createImageFile(Activity context, boolean isPrivate) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStorageDirectory();
        storageDir = new File(storageDir, "jwtdb");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = new File(storageDir, imageFileName + ".jpg");
        return image;
    }

    public static Bitmap compressBitmap(String bigFile, int width,
                                        boolean isText) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bigFile, options);
        int w = options.outWidth;
        // int h = options.outHeight;
        int sz = w / width;
        sz = getPower(sz, true);
        // int sz = options.outWidth / 800;
        options.inJustDecodeBounds = false;
        options.inSampleSize = sz > 1 ? sz : 1;
        options.inPurgeable = true;
        Bitmap smallImg = BitmapFactory.decodeFile(bigFile, options);
        if (isText)
            smallImg = GlobalMethod.drawBitmapText(smallImg, null);
        return smallImg;
    }

    public static Bitmap compressBitmap(String bigFile, int width, String text) {
        Bitmap smallImg = compressBitmap(bigFile, width, false);
        if (!TextUtils.isEmpty(text))
            smallImg = drawBitmapText(smallImg, text);
        return smallImg;
    }

    public static Bitmap drawBitmapText(Bitmap src, String text) {
        // String tag = "createBitmap";
        // Log.d(tag, "create a new bitmap");
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int textSize = w / 40;
        // create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        // 创建一个新的和SRC长度宽度一样的位图

        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint p = new Paint();
        String familyName = "宋体";
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        p.setColor(Color.RED);
        p.setTypeface(font);
        p.setTextSize(textSize);
        if (TextUtils.isEmpty(text))
            text = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    .format(new Date());
        cv.drawText(text, 20, textSize + 20, p);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }

    /**
     * 从文件中读取实际图片
     *
     * @param file
     * @return 位图
     */
    public static Bitmap getImageFromFile(String file) {
        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        Bitmap smallImage = BitmapFactory.decodeFile(file, options);
        return smallImage;
    }

    public static void cleanText(EditText... ed) {
        for (EditText editText : ed) {
            editText.setText("");
        }
    }

    public static void setEnable(boolean isEndable, View... views) {
        for (View view : views) {
            view.setEnabled(isEndable);
            if (view instanceof Spinner && !isEndable) {
                ((Spinner) view).setSelection(0);
            }
        }
    }

//    /**
//     * 保存系统参数
//     *
//     * @param context
//     */
//    public static void saveParam(Context context) {
//        // File paramDir = new File("/data/data/"
//        File paramDir = context.getFilesDir();
//        if (paramDir.exists())
//            paramDir.mkdirs();
//        File paramFile = new File(paramDir, "param.xml");
//        saveParam(context, paramFile);
//    }

    // public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
    // {
    // int w = bitmap.getWidth();
    // int h = bitmap.getHeight();
    // Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
    // Canvas canvas = new Canvas(output);
    // final int color = 0xff424242;
    // final Paint paint = new Paint();
    // final Rect rect = new Rect(0, 0, w, h);
    // final RectF rectF = new RectF(rect);
    // paint.setAntiAlias(true);
    // canvas.drawARGB(0, 0, 0, 0);
    // paint.setColor(color);
    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    // canvas.drawBitmap(bitmap, rect, rect, paint);
    // return output;
    // }

    public static Bitmap getIconByName(String iconName, Context self) {
        Drawable d = self.getResources().getDrawable(R.drawable.ic_launcher);
        if (TextUtils.isEmpty(iconName))
            return ((BitmapDrawable) d).getBitmap();
        int resource = getImageResouseByName(iconName);
        if (resource > 0) {
            d = self.getResources().getDrawable(resource);
            return ((BitmapDrawable) d).getBitmap();
        }
        if (!iconName.endsWith(".png"))
            iconName += ".png";
        String iconDirStr = self.getFilesDir().getPath() + "/icon/";
        File iconDir = new File(iconDirStr);
        File icon = new File(iconDir, iconName);
        if (!icon.exists()) {
            return ((BitmapDrawable) d).getBitmap();
        }
        Drawable rs = Drawable.createFromPath(icon.getPath());
        if (rs == null) {
            return ((BitmapDrawable) d).getBitmap();
        }
        return ((BitmapDrawable) rs).getBitmap();
    }

//    /**
//     * 保存参数到文件中
//     *
//     * @param context
//     * @param paramFile
//     */
//    public static void saveParam(Context context, File paramFile) {
//        Map<String, String> map = new HashMap<String, String>();
//        Class<?> cl = GlobalSystemParam.class;
//        Field[] fields = cl.getFields();
//        for (Field field : fields) {
//            // if (!Modifier.isStatic(field.getModifiers()))
//            // continue;
//            Object value = null;
//            try {
//                value = field.get(null);
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            if (value == null) {
//                map.put(field.getName(), "");
//                continue;
//            }
//            if (field.getType().isPrimitive()
//                    && field.getType().getName().toLowerCase()
//                    .equals("boolean")) {
//                Boolean b = (Boolean) value;
//                map.put(field.getName(), b ? "1" : "0");
//            } else {
//                map.put(field.getName(), value.toString());
//            }
//        }
//        logMap(map, "saveParam");
//        // ParamBean p = new ParamBean();
//        // p.setNetwork(GlobalData.connCata.getIndex() + "");
//        // p.setGpsState(GlobalData.isGpsUpload ? "1" : "0");
//        // p.setPreviewPhoto(GlobalData.isPreviewPhoto ? "1" : "0");
//        // p.setGpsUpFreq("" + GlobalData.uploadFreq);
//        // p.setNeedSfzh(GlobalData.isCheckFjdcSfzm ? "1" : "0");
//        // p.setPicComp("" + GlobalData.picCompress);
//        // p.setDrvCheck(String.valueOf(GlobalData.drvCheckFs));
//        // p.setVehCheck(String.valueOf(GlobalData.vehCheckFs));
//        try {
//            String xml = CommParserXml.mapToXml(map);
//            FileWriter fw = new FileWriter(paramFile);
//            fw.write(xml);
//            fw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 调试用打印相关的记录
     *
     * @param map
     */
    public static void logMap(Map<String, String> map, String methodName) {
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> entry : set) {
            Log.e("GlobalMethod " + methodName,
                    entry.getKey() + ": " + entry.getValue());
        }
    }

//    /**
//     * 读出保存的系统参数
//     *
//     * @param context
//     * @return
//     */
//    public static void readParam(Context context) {
//        File paramFile = new File(context.getFilesDir(), "param.xml");
//        try {
//            String xml = readFileContent(paramFile);
//            Map<String, String> map = CommParserXml.xmlToMap(xml);
//            logMap(map, "readParam");
//            Field[] fields = GlobalSystemParam.class.getFields();
//            for (Field field : fields) {
//                field.setAccessible(true);
//                String name = field.getName();
//                String value = map.get(name);
//                if (TextUtils.isEmpty(value))
//                    continue;
//                Class<?> type = field.getType();
//                if (type.getName().equals("boolean")) {
//                    field.setBoolean(GlobalSystemParam.class,
//                            TextUtils.equals(value, "1") ? true : false);
//                } else if (type.getName().equals("int")) {
//                    field.setInt(GlobalSystemParam.class,
//                            Integer.valueOf(value));
//                } else if (type == String.class) {
//                    field.set(GlobalSystemParam.class, value);
//                }
//            }
//            // ParamBean p = CommParserXml.parseXmlToObj(xml, ParamBean.class);
//            // if (TextUtils.isEmpty(p.getNetwork()))
//            // p.setNetwork(GlobalData.connCata.getIndex() + "");
//            // if (TextUtils.isEmpty(p.getGpsState()))
//            // p.setGpsState(GlobalData.isGpsUpload ? "1" : "0");
//            // if (TextUtils.isEmpty(p.getGpsUpFreq()))
//            // p.setGpsUpFreq("" + GlobalData.uploadFreq);
//            // if (TextUtils.isEmpty(p.getNeedSfzh()))
//            // p.setNeedSfzh(GlobalData.isCheckFjdcSfzm ? "1" : "0");
//            // if (TextUtils.isEmpty(p.getPicComp()))
//            // p.setPicComp("" + GlobalData.picCompress);
//            // if (TextUtils.isEmpty(p.getDrvCheck()))
//            // p.setDrvCheck("" + GlobalData.drvCheckFs);
//            // if (TextUtils.isEmpty(p.getVehCheck()))
//            // p.setVehCheck("" + GlobalData.vehCheckFs);
//            // return p;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // return null;
//
//    }

    /**
     * 读取图标的MD5
     *
     * @param context
     * @return
     */
    public static String readIconMd5(Context context) {
        File md5File = new File(context.getFilesDir(), "md5.txt");
        if (!md5File.exists())
            return null;
        return readFileContent(md5File);
    }

    public static boolean isOnline() {
        return GlobalData.connCata != ConnCata.OFFCONN;
    }

    public static Point getDevicePixel(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        // float density = dm.density;
        // int screenWidth = (int) (widthPixels * density);
        // int screenHeight = (int) (heightPixels * density);
        Point p = new Point(widthPixels, heightPixels);
        return p;
    }

    /**
     * 读手机串号
     *
     * @param context
     * @return
     */
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

    /**
     * 获取更新文件列表
     *
     * @param context
     * @return
     */
    public static UpdateFile[] getUpdateFileFromDb(Context context) {
        List<UpdateFile> list = new ArrayList<UpdateFile>();
        Uri CONTENT_URI = Uri.parse("content://com.google.provider.SysData/querySys");
        Cursor c = context.getContentResolver().query(CONTENT_URI, null,
                "key like 'APK:%'", null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    UpdateFile uf = new UpdateFile();
                    String[] ar = c.getString(0).split(":");
                    String[] ar2 = c.getString(1).split(":");
                    uf.setPackageName(ar[1]);
                    uf.setVersion(ar2[0]);
                    uf.setId(ar2[1]);
                    uf.setHashValue(ar2[2]);
                    uf.setFileName(ar2[3]);
                    list.add(uf);
                } while (c.moveToNext());
            }
            c.close();
        }
        if (!list.isEmpty()) {
            UpdateFile[] ufs = new UpdateFile[list.size()];
            return list.toArray(ufs);
        }
        return null;
    }

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

    public static double getApkVerionName(String packName, Context self) {
        double version = 0.0;
        try {
            version = str2Double(self.getPackageManager().getPackageInfo(
                    packName, 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    /**
     * 是否需要标准值和实测值
     *
     * @param wfxw
     * @return
     */
    public static boolean isInBzzScz(String wfxw) {
        if (TextUtils.isEmpty(wfxw) || wfxw.length() < 4)
            return false;
        String sf = wfxw.substring(0, 4);
        return GlobalConstant.sczCode.indexOf(sf) > -1;
    }

    public static String getJsonField(String str, String field) {
        if (TextUtils.isEmpty(str))
            return null;
        JSONObject json = null;
        try {
            json = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null)
            return null;
        String xtbh = json.optString(field, "");
        if (!TextUtils.isEmpty(xtbh))
            return xtbh;
        return null;
    }

    public static Map<String, String> getSavedMjInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        String mjxx = sharedPreferences.getString("mjxx", "{}");
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(mjxx);
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.opt(key).toString();
                map.put(key.toUpperCase(), value);
                Log.e("getSavedMjInfo", key + ":" + value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static BoxStore getBoxStore(Activity self) {
        return ((App) self.getApplication()).getBoxStore();
    }

    public static BoxStore getBoxStore(Service self) {
        return ((App) self.getApplication()).getBoxStore();
    }

    public static BoxStore getBoxStore(Context self) {
        if (self instanceof Activity) {
            Activity ac = (Activity) self;
            return getBoxStore(ac);
        }
        if (self instanceof Service) {
            Service as = (Service) self;
            return getBoxStore(as);
        }
        return null;
    }

    public static int getEditInt(EditText et) {
        return TextUtils.isEmpty(et.getText()) ? 0 : Integer.valueOf(et.getText().toString());
    }

    /**
     * 获取保存值
     *
     * @param self
     * @param name
     * @return
     */
    public static String getSavedInfo(Context self, String name) {
        SharedPreferences sharedPreferences = self.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        return sharedPreferences.getString(name, "");
    }

    /**
     * 保存值
     *
     * @param self
     * @param name
     * @param value
     */
    public static void putSavedInfo(Context self, String name, String value) {
        SharedPreferences sharedPreferences = self.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString(name, value);
        editor.commit();//提交修改
    }

    public static String join(List<String> list, String d) {
        String re = "";
        for (String s : list) {
            if (!TextUtils.isEmpty(s))
                re += s + d;
        }
        if (re.length() > 0) {
            re = re.substring(0, re.length() - d.length());
        }
        return re;
    }

    public static String join(String[] array, String d) {
        String re = "";
        for (String s : array) {
            if (!TextUtils.isEmpty(s))
                re += s + d;
        }
        if (re.length() > 0) {
            re = re.substring(0, re.length() - d.length());
        }
        return re;
    }

}
