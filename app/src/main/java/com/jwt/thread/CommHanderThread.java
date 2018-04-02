package com.jwt.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.pojo.JtssLightPic;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import io.objectbox.Box;

/**
 * Created by lixiaodong on 2017/9/8.
 */

public class CommHanderThread extends Thread {

    public static final int QUERY_ROAD = 900;
    public static final int QUERY_CROSS = 901;
    public static final int QUERY_LIGHT = 902;
    public static final int UPLOAD_PIC = 903;
    private int queryCata;
    private String[] params;
    private ProgressDialog progressDialog;
    private Context context;
    private Handler mHander;

    public CommHanderThread(int queryCata, String[] params, Handler mHander,
                            Context context) {
        this.queryCata = queryCata;
        this.params = params;
        this.mHander = mHander;
        this.context = context;
    }

    public void doStart() {
        Log.e("CommQueryThread", "do start");
        progressDialog = ProgressDialog.show(context, "提示", "正在操作,请稍等...",
                true);
        progressDialog.setCancelable(true);
        start();
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        if (queryCata == QUERY_ROAD) {
            WebQueryResult<String> json = dao.queryRoadByXzqh(params[0]);
            queryReturn(queryCata, json);
        } else if (queryCata == QUERY_CROSS) {
            WebQueryResult<String> json = dao.queryCrossByDldm(params[0]);
            queryReturn(queryCata, json);
        } else if (queryCata == QUERY_LIGHT) {
            WebQueryResult<String> json = dao.queryLightByCross(params[0]);
            queryReturn(queryCata, json);
        } else if (queryCata == UPLOAD_PIC) {
            Message message = mHander.obtainMessage();
            Bundle data = new Bundle();
            JSONObject reval = new JSONObject();
            ParserJson.putJsonVal(reval, "catalog", UPLOAD_PIC);
            JSONArray pics = ParserJson.getJsonArray(params[0]);
            if (pics == null || pics.length() == 0) {
                returnError(reval, message, data, "无数据");
                return;
            }
            Box<JtssLightPic> box = GlobalMethod.getBoxStore(context).boxFor(JtssLightPic.class);
            int count = 0;
            ParserJson.putJsonVal(reval, "total", pics.length());
            for (int i = 0; i < pics.length(); i++) {
                JSONObject obj = pics.optJSONObject(i);
                long id = obj.optLong("id");
                String lightId = obj.optString("lightId");
                String pic = obj.optString("pic");
                if (TextUtils.isEmpty(lightId) || TextUtils.isEmpty(pic)) {
                    ParserJson.putJsonVal(reval, "count", count);
                    returnError(reval, message, data, "数据为空");
                    return;
                }
                WebQueryResult<String> we = dao.uploadLightPic(lightId, new File(pic));
                String err = GlobalMethod.getErrorMessageFromWeb(we);
                if (!TextUtils.isEmpty(err)) {
                    ParserJson.putJsonVal(reval, "count", count);
                    returnError(reval, message, data, err);
                    return;
                }
                JtssLightPic jtss = box.get(id);
                jtss.setScbj(1);
                box.put(jtss);
                count++;
            }
            ParserJson.putJsonVal(reval, "count", count);
            data.putString("data", reval.toString());
            message.setData(data);
            mHander.sendMessage(message);
        }
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void returnError(JSONObject reval, Message message, Bundle data, String content) {
        ParserJson.putJsonVal(reval, "err", content);
        data.putString("data", reval.toString());
        message.setData(data);
        mHander.sendMessage(message);
    }

    private void queryReturn(int catalog, WebQueryResult<String> json) {
        Message message = mHander.obtainMessage();
        Bundle data = new Bundle();
        String err = GlobalMethod.getErrorMessageFromWeb(json);
        JSONObject obj = new JSONObject();

        if (TextUtils.isEmpty(err)) {
            JSONArray array = ParserJson.getJsonArray(json.getResult());
            if (array != null && array.length() > 0)
                ParserJson.putJsonVal(obj, "array", array);
        } else
            ParserJson.putJsonVal(obj, "err", err);
        ParserJson.putJsonVal(obj, "catalog", catalog);
        data.putString("data", obj.toString());
        message.setData(data);
        mHander.sendMessage(message);
    }
}
