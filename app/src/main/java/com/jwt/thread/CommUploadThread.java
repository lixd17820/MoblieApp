package com.jwt.thread;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jwt.event.AcdUploadEvent;
import com.jwt.event.UploadEvent;
import com.jwt.pojo.TruckDriverBean;
import com.jwt.bean.TruckVehicleBean;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.pojo.VioViolation;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.greenrobot.eventbus.EventBus;


public class CommUploadThread extends Thread {

    public static final int UPLOAD_TRUCK_VEH = 100;
    public static final int UPLOAD_TRUCK_DRV = 101;
    public static final int UPLOAD_ACD = 102;
    public static final String RESULT_UPLOAD_ACD = "upload_acd";
    public static final String UPLOAD_ACD_BEAN = "acd_bean";
    public static final String RESULT_UP_TRUCK_VEH = "truck_veh";
    public static final String RESULT_UP_TRUCK_DRV = "truck_drv";
    public static final int UPLOAD_VIO_PIC = 103;

    private int queryCata;
    private Object[] params;
    private ProgressDialog progressDialog;
    private Context context;

    /**
     * @param queryCata 操作类型
     * @param context   上下文
     * @paintram params    上传对象
     */
    public CommUploadThread(int queryCata, Object[] params,
                            Context context) {
        this.queryCata = queryCata;
        this.params = params;
        this.context = context;
    }

    public void doStart() {
        progressDialog = ProgressDialog.show(context, "提示", "正在上传请求数据,请稍等...",
                true);
        progressDialog.setCancelable(true);
        start();
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        if (queryCata == UPLOAD_TRUCK_VEH) {
            TruckVehicleBean tv = (TruckVehicleBean) params[0];
            WebQueryResult<ZapcReturn> re = dao.uploadTruckVeh(tv);
            EventBus.getDefault().post(re);
        } else if (queryCata == UPLOAD_TRUCK_DRV) {
            TruckDriverBean drv = (TruckDriverBean) params[0];
            WebQueryResult<ZapcReturn> re = dao.uploadTruckDrv(drv);
            EventBus.getDefault().post(re);
        } else if (queryCata == UPLOAD_ACD) {
            AcdSimpleBean acd = (AcdSimpleBean) params[0];
            ArrayList<AcdSimpleHumanBean> humans = (ArrayList<AcdSimpleHumanBean>) params[1];
            WebQueryResult<ZapcReturn> re = dao.uploadAcdInfo(acd, humans);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            acd.setScbj("0");
            AcdUploadEvent event = new AcdUploadEvent();
            event.setAcd(acd);
            if (TextUtils.isEmpty(err)) {
                ZapcReturn result = re.getResult();
                if (TextUtils.equals("1", result.getCgbj())) {
                    event.setScbj(1);
                    acd.setScbj("1");
                }else{
                    event.setMessage(result.getScms());
                }
            }else{
                event.setMessage(err);
            }
            //EventBus.getDefault().post(re);
            EventBus.getDefault().post(event);
        } else if (queryCata == UPLOAD_VIO_PIC) {
            VioViolation vio = (VioViolation) params[0];
            WebQueryResult<String> re = dao.uploadVioPic(vio);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            if (TextUtils.isEmpty(err)) {
                EventBus.getDefault().post(new UploadEvent(vio.getId(), true, ""));
            } else {
                EventBus.getDefault().post(new UploadEvent(vio.getId(), false, err));
            }

        }
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
