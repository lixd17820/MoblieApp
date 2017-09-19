package com.jwt.thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.jwt.dao.FxczfDao;
import com.jwt.event.CommEvent;
import com.jwt.event.DownSpeedEvent;
import com.jwt.event.VioUploadEvent;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ZipUtils;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FxcUploadPhotoThread extends Thread {
    private VioFxczfBean fxc;
    private Context context;

    // private ProgressDialog progressDialog;

    public FxcUploadPhotoThread(Context context,
                                VioFxczfBean fxc) {
        this.context = context;
        this.fxc = fxc;
    }

    public void doStart() {
        start();
    }


    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        //修改，先上传照片，全部成功后上传文字，上传照片前获取MD5，上传中核对，保证数据完整性
        List<String> imageInfo = new ArrayList<String>();
        List<String> zpUpOk = new ArrayList<String>();
        String[] files = fxc.getPics().split(",");
        int total = files.length + 1;
        for (int i = 0; i < files.length; i++) {
            File photo = new File(files[i]);
            GlobalMethod.savePicLowFiftyByte(photo);
            //MD5
            String md5 = ZipUtils.getFileMd5(photo);
            String re = dao.uploadFxcPhotoAndMd5(photo, md5);
            String imageBh = GlobalMethod.getJsonField(re, "image_bh");
            if (TextUtils.isEmpty(imageBh)) {
                EventBus.getDefault().post(new DownSpeedEvent("上传验证图片失败", -1, -1, ""));
                return;
            }
            imageInfo.add(md5 + "," + imageBh);
            EventBus.getDefault().post(new DownSpeedEvent("", total, i + 1, ""));
        }
        fxc.setPics("");
        WebQueryResult<String> rs = dao.uploadFxczfJlAndImage(fxc, imageInfo);
        String err = GlobalMethod.getErrorMessageFromWeb(rs);
        if (!TextUtils.isEmpty(err)) {
            EventBus.getDefault().post(new DownSpeedEvent(err,-1,-1,""));
            return;
        }

        String xtbh = GlobalMethod.getJsonField(rs.getResult(), "xtbh");
        if (TextUtils.isEmpty(xtbh)) {
            //sendData("数据上传错误", GlobalConstant.WHAT_ALL_ERR, 0);
            EventBus.getDefault().post(new DownSpeedEvent("数据上传错误", -1, -1, ""));
            return;
        }

        FxczfDao.updateXtbhScbj(fxc.getId(), xtbh, GlobalMethod.getBoxStore(context));
        EventBus.getDefault().post(new DownSpeedEvent("", total, total, ""));
    }


}
