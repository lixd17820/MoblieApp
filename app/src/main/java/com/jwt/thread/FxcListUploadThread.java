package com.jwt.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.jwt.dao.FxczfDao;
import com.jwt.jbyw.VioFxcFileBean;
import com.jwt.jbyw.VioFxczfBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ZipUtils;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FxcListUploadThread extends Thread {
    private Handler mHandler;
    private List<VioFxczfBean> fxczfs;
    private ProgressDialog progressDialog;
    private Context context;
    private int maxStep = 0;
    private boolean isShowProgress;

    // private ProgressDialog progressDialog;

    public FxcListUploadThread(Context context, Handler mHandler,
                               List<VioFxczfBean> fxczfs) {
        this.mHandler = mHandler;
        this.context = context;
        this.fxczfs = fxczfs;
        this.isShowProgress = true;
    }

    public FxcListUploadThread(Context context, Handler mHandler,
                               List<VioFxczfBean> fxczfs, boolean isShowProgress) {
        this.mHandler = mHandler;
        this.context = context;
        this.fxczfs = fxczfs;
        this.isShowProgress = isShowProgress;
    }

    public void doStart() {
        for (VioFxczfBean fxc : fxczfs) {
            maxStep += Integer.valueOf(fxc.getPhotos()) + 2;
        }
        if (isShowProgress) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在上传非现场信息...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(maxStep);
            progressDialog.show();
        }
        Log.e("FxcListUploadThread", "maxStep: " + maxStep);
        start();
    }

    @Override
    public void run() {
        int step = 0;
        int error = 0;
        RestfulDao dao = RestfulDaoFactory.getDao();
        for (VioFxczfBean fxc : fxczfs) {
            List<VioFxcFileBean> files = FxczfDao.queryFxczfFileByFId(fxc.getId(), GlobalMethod.getBoxStore(context));
            String tzsh = fxc.getTzsh();
            boolean isUp = dao.checkFxcTzsh(tzsh);
            if (isUp) {
                step += files.size() + 1;
                showStep(step);
                FxczfDao.updateFxcUploaded(fxc.getId(), GlobalMethod.getBoxStore(context));
                continue;
            }
            showStep(step);
            List<String> imageInfo = new ArrayList<String>();
            for (VioFxcFileBean zp : files) {
                step++;
                showStep(step);
                File photo = new File(zp.getWjdz());
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
            if (imageInfo.size() != files.size()) {
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
            step++;
        }
        if (isShowProgress) {
            Bundle data = new Bundle();
            data.putInt("error", error);
            data.putInt("all", maxStep);
            sendData(data, GlobalConstant.WHAT_ALL_OK, maxStep);
            progressDialog.dismiss();
        }
    }

    private void showStep(int step) {
        if (isShowProgress)
            progressDialog.setProgress(step);
    }

//    @Override
//    public void run() {
//        int step = 0;
//        int error = 0;
//        RestfulDao dao = RestfulDaoFactory.getDao();
//        FxczfDao fxcDao = new FxczfDao(context);
//        for (VioFxczfBean fxc : fxczfs) {
//            List<VioFxcFileBean> files = fxcDao.queryUnuploadFxczfFileByFId(fxc
//                    .getId());
//            Log.e("FxcListUploadThread", "file size: " + files.size());
//            WebQueryResult<ZapcReturn> rs = dao.uploadFxczfJl(fxc);
//            String err = GlobalMethod.getErrorMessageFromWeb(rs);
//            if (!TextUtils.isEmpty(err) || rs.getResult() == null
//                    || !TextUtils.equals(rs.getResult().getCgbj(), "1")
//                    || rs.getResult().getPcbh() == null
//                    || rs.getResult().getPcbh().length == 0) {
//
//                step++;
//                if (isShowProgress)
//                    progressDialog.setProgress(step);
//                Log.e("FxcListUploadThread", "step: " + step);
//                error++;
//                continue;
//            }
//            // 信息成功加一
//            step++;
//            if (isShowProgress)
//                progressDialog.setProgress(step);
//            Log.e("FxcListUploadThread", "step: " + step);
//            String xtbh = rs.getResult().getPcbh()[0];
//            fxcDao.updateXtbhScbj(fxc.getId(), xtbh);
//            Long recID = Long.valueOf(xtbh);
//            for (int i = 0; i < files.size(); i++) {
//                VioFxcFileBean zp = files.get(i);
//                File photo = new File(zp.getWjdz());
//                if (!photo.exists()) {
//                    step++;
//                    if (isShowProgress)
//                        progressDialog.setProgress(step);
//                    Log.e("FxcListUploadThread", "step: " + step);
//                    error++;
//                    continue;
//                }
//                int isCompress = GlobalMethod.savePicLowFiftyByte(photo);
//                Log.e("FxcListUploadThread", "Compress count is: " + isCompress);
//                WebQueryResult<ZapcReturn> re = dao
//                        .uploadFxcPhoto(photo, recID);
//                String photoErr = GlobalMethod.getErrorMessageFromWeb(re);
//                if (!TextUtils.isEmpty(photoErr) || re.getResult() == null
//                        || !TextUtils.equals(re.getResult().getCgbj(), "1")) {
//                    step++;
//                    if (isShowProgress)
//                        progressDialog.setProgress(step);
//                    Log.e("FxcListUploadThread", "step: " + step);
//                    error++;
//                    continue;
//                }
//                fxcDao.setPhotoBj(zp.getId(), "1");
//                // 文件成功加一
//                step++;
//                if (isShowProgress)
//                    progressDialog.setProgress(step);
//                Log.e("FxcListUploadThread", "step: " + step);
//            }
//        }
//        fxcDao.closeDb();
//        if (isShowProgress) {
//            Bundle data = new Bundle();
//            data.putInt("error", error);
//            data.putInt("all", maxStep);
//            sendData(data, GlobalConstant.WHAT_ALL_OK, maxStep);
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
//        }
//    }

    private void sendData(Bundle data, int what, int step) {
        Message m = mHandler.obtainMessage();
        m.what = what;
        m.arg1 = step;
        if (data != null)
            m.setData(data);
        mHandler.sendMessage(m);
    }

}
