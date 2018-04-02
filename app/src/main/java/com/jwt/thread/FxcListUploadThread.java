package com.jwt.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.jwt.dao.FxczfDao;
import com.jwt.event.FxcUploadEvent;
import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ZipUtils;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FxcListUploadThread extends Thread {
    private List<VioFxczfBean> fxczfs;
    private Context context;
    private int maxStep = 0;

    public FxcListUploadThread(Context context,
                               List<VioFxczfBean> fxczfs) {
        this.context = context;
        this.fxczfs = fxczfs;
    }

    public void doStart() {
        for (VioFxczfBean fxc : fxczfs) {
            maxStep += Integer.valueOf(fxc.getPhotos()) + 1;
        }
        EventBus.getDefault().post(new FxcUploadEvent(maxStep, 0));
        Log.e("FxcListUploadThread", "maxStep: " + maxStep);
        start();
    }

    @Override
    public void run() {
        int step = 0;
        int error = 0;
        RestfulDao dao = RestfulDaoFactory.getDao();
        for (VioFxczfBean fxc : fxczfs) {
            String fs = fxc.getPics();
            if (TextUtils.isEmpty(fs))
                continue;
            String[] files = fxc.getPics().split(",");
            List<String> imageInfo = new ArrayList<String>();
            for (String zp : files) {
                EventBus.getDefault().post(new FxcUploadEvent(maxStep, ++step));
                File photo = new File(zp);
                if (!photo.exists()) {
                    error++;
                    continue;
                }
                int isCompress = GlobalMethod.savePicLowFiftyByte(photo);
                Log.e("FxcListUploadThread", "Compress count is: " + isCompress);
                String md5 = ZipUtils.getFileMd5(photo);
                String re = dao.uploadFxcPhotoAndMd5(photo, md5);
                String imageBh = GlobalMethod.getJsonField(re, "image_bh");
                if (TextUtils.isEmpty(imageBh)) {
                    error++;
                    continue;
                }
                imageInfo.add(md5 + "," + imageBh);
                //保存图片状态
                //fxcDao.setPhotoBj(zp.getId(), "1");
            }
            if (imageInfo.size() != files.length) {
                error++;
                step++;
                continue;
            }
            WebQueryResult<String> rs = dao.uploadFxczfJlAndImage(fxc, imageInfo);
            String xtbh = GlobalMethod.getJsonField(rs.getResult(), "xtbh");
            if (TextUtils.isEmpty(xtbh)) {
                error++;
                step++;
                continue;
            }
            FxczfDao.updateXtbhScbj(fxc.getId(), xtbh, GlobalMethod.getBoxStore(context));
            EventBus.getDefault().post(new FxcUploadEvent(maxStep, ++step));
        }
        FxcUploadEvent event = new FxcUploadEvent(maxStep, maxStep);
        event.err = error;
        event.isDone = true;
        event.message = "完成";
        EventBus.getDefault().post(event);
    }

}
