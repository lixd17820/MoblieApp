package com.jwt.thread;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.jwt.dao.AcdSimpleDao;
import com.jwt.event.CommEvent;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.greenrobot.eventbus.EventBus;


public class AcdUploadPhotoThread extends Thread {
    private AcdPhotoBean acd;
    private ProgressDialog progressDialog;
    private Context context;
    private int maxStep;

    public AcdUploadPhotoThread(AcdPhotoBean acd, Context context) {
        this.context = context;
        this.acd = acd;
    }

    public void doStart() {
        progressDialog = ProgressDialog.show(context, "提示", "正在上传事故信息...",
                true);
        maxStep = acd.getPhoto().split(",").length + 1;
        progressDialog.setTitle("提示");
        // progressDialog.setMessage("正在上传事故信息...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxStep);
        progressDialog.show();
        start();
    }

    @Override
    public void run() {
        int step = 0;
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<ZapcReturn> rs = dao.uploadAcdRecode(acd);
        String err = GlobalMethod.getErrorMessageFromWeb(rs);
        if (!TextUtils.isEmpty(err)) {
            EventBus.getDefault().post(new CommEvent(-1, err));
            progressDialog.dismiss();
            return;
        } else {
            progressDialog.setMessage("记录上传成功");
            progressDialog.setProgress(++step);
        }
        Long recID = Long.valueOf(rs.getResult().getPcbh()[0]);
        List<String> files = Arrays.asList(acd.getPhoto().split(","));
        for (int i = 0; i < files.size(); i++) {
            File photo = new File(files.get(i));
            if (!photo.exists()) {
                EventBus.getDefault().post(new CommEvent(-1, "上传文件不存在"));
                progressDialog.dismiss();
                return;
            }
            WebQueryResult<ZapcReturn> re = dao.uploadAcdPhoto(photo, recID);
            String photoErr = GlobalMethod.getErrorMessageFromWeb(re);
            if (!TextUtils.isEmpty(photoErr)) {
                EventBus.getDefault().post(new CommEvent(-1, photoErr));
                progressDialog.dismiss();
                return;
            } else {
                progressDialog.setProgress(++step);
                progressDialog.setMessage("图片上传成功");
                //sendData("图片上传成功", GlobalConstant.WHAT_PHOTO_OK, ++step * 25);
            }
        }
        progressDialog.dismiss();
        AcdSimpleDao.updateAcdPhotoRecode(recID, acd.getId(), GlobalMethod.getBoxStore(context));
        AcdSimpleDao.updateAcdPhotoRecodeScbj(acd.getId(), GlobalMethod.getBoxStore(context));
        EventBus.getDefault().post(new CommEvent(200, recID + ""));
        //Bundle data = new Bundle();
        //data.putLong("xtbh", recID);
        //data.putLong("acdID", acd.getId());
    }

}
