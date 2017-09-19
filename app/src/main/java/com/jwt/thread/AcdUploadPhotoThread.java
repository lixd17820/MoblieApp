package com.jwt.thread;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.event.CommEvent;
import com.jwt.event.FxcUploadEvent;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.greenrobot.eventbus.EventBus;


public class AcdUploadPhotoThread extends Thread {
    private AcdPhotoBean acd;

    private Context context;
    private int maxStep;

    public AcdUploadPhotoThread(AcdPhotoBean acd, Context context) {
        this.context = context;
        this.acd = acd;
    }

    public void doStart() {
        maxStep = acd.getPhoto().split(",").length + 1;
        start();
    }

    @Override
    public void run() {
        FxcUploadEvent event = new FxcUploadEvent();
        event.isDone = true;
        event.err = 1;
        int step = 0;
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<ZapcReturn> rs = dao.uploadAcdRecode(acd);
        String err = GlobalMethod.getErrorMessageFromWeb(rs);
        if (!TextUtils.isEmpty(err)) {
            event.message = err;
            EventBus.getDefault().post(event);
            return;
        } else {
            EventBus.getDefault().post(new FxcUploadEvent(maxStep, ++step));
        }
        Long recID = Long.valueOf(rs.getResult().getPcbh()[0]);
        Log.e("uploadacdphoto", "pcbh: " + recID);
        String[] files = acd.getPhoto().split(",");
        for (int i = 0; i < files.length; i++) {
            File photo = new File(files[0]);
            if (!photo.exists()) {
                event.message = "上传文件不存在";
                EventBus.getDefault().post(event);
                return;
            }
            Log.e("uploadacdphoto", photo.getAbsolutePath());
            WebQueryResult<ZapcReturn> re = dao.uploadAcdPhoto(photo, recID);
            String photoErr = GlobalMethod.getErrorMessageFromWeb(re);
            if (!TextUtils.isEmpty(photoErr) || !"1".equals(re.getResult().getCgbj())) {
                event.message = "上传文件失败";
                EventBus.getDefault().post(event);
                return;
            } else {
                EventBus.getDefault().post(new FxcUploadEvent(maxStep, ++step));
                //sendData("图片上传成功", GlobalConstant.WHAT_PHOTO_OK, ++step * 25);
            }
        }
        event.err = 0;
        event.message = "上传成功";
        EventBus.getDefault().post(event);
        AcdSimpleDao.updateAcdPhotoRecodeScbj(acd.getId(), recID, GlobalMethod.getBoxStore(context));
        EventBus.getDefault().post(new CommEvent(200, recID + ""));
        //Bundle data = new Bundle();
        //data.putLong("xtbh", recID);
        //data.putLong("acdID", acd.getId());
    }

}
