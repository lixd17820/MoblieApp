package com.jwt.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jwt.adapter.TwoLineSelectAdapter;
import com.jwt.dao.ViolationDAO;
import com.jwt.event.VioUploadEvent;
import com.jwt.pojo.VioViolation;
import com.jwt.update.JbywPrintJdsList;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by lixiaodong on 2017/9/15.
 */

public class UploadVioThread extends Thread {

    private ProgressDialog progressDialog;
    private VioViolation vio;
    private Activity self;
    private boolean isShowProgress;

    public UploadVioThread(VioViolation vio, Activity self) {
        this.vio = vio;
        this.self = self;
        this.isShowProgress = false;
    }

    public UploadVioThread(VioViolation vio, Activity self, boolean isShowProgress) {
        this.vio = vio;
        this.self = self;
        this.isShowProgress = isShowProgress;
    }

    public void doStart() {
        if (isShowProgress) {
            progressDialog = ProgressDialog.show(self, "提示", "正在发送文书,请稍等...",
                    true);
            progressDialog.setCancelable(true);
        }
        start();
    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        // WebQueryResult<LoginMessage> rs =
        // ViolationDAO.uploadViolation(vio);
        VioUploadEvent event = new VioUploadEvent();
        WebQueryResult<ZapcReturn> rs = ViolationDAO.uploadViolation(vio, GlobalMethod.getBoxStore(self));
        String err = GlobalMethod.getErrorMessageFromWeb(rs);
        if (TextUtils.isEmpty(err)) {
            ZapcReturn upRe = rs.getResult();
            if (upRe != null && "1".equals(upRe.getCgbj())) {
                event.scbj = 1;
                event.id = vio.getId();
            } else {
                event.message = "上传失败";
            }
        } else {
            event.message = err;
        }
        EventBus.getDefault().post(event);
        if (isShowProgress)
            progressDialog.dismiss();
    }
}
