package com.jwt.globalquery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;


public class ZhcxThread extends Thread {

    private Handler mHandler;
    private ProgressDialog progressDialog;
    String id;
    String where;

    public ZhcxThread(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 启动线程
     */
    public void doStart(Context context, String id, String where) {
        // 显示进度对话框
        this.id = id;
        this.where = where;
        progressDialog = ProgressDialog.show(context, "提示",
                "正在请求数据,请稍等...", true);
        progressDialog.setCancelable(true);
        this.start();
    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        // 用的是JWT连接
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<GlobalQueryResult> webResult = dao.zhcxRestful(
                id, where);

        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putSerializable("queryResult", webResult);
        // int error = WebQueryInterface.NETERROR;
        // if (webResult.getStatus() == HttpStatus.SC_OK) {
        // if (webResult.getResult().getContents().length == 0)
        // error = WebQueryInterface.NODATA;
        // else {
        // error = WebQueryInterface.DATAOK;
        // //b.putString("type", webResult.getResult().getqObj().getTp());
        // }
        // }
        // b.putInt("error", error);
        msg.setData(b);
        mHandler.sendMessage(msg);
        progressDialog.dismiss();

    }

}