package com.jwt.thread;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jwt.bean.TruckDriverBean;
import com.jwt.bean.TruckVehicleBean;
import com.jwt.jbyw.AcdSimpleBean;
import com.jwt.jbyw.AcdSimpleHumanBean;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;


public class CommUploadThread extends Thread {

	public static final int UPLOAD_TRUCK_VEH = 100;
	public static final int UPLOAD_TRUCK_DRV = 101;
	public static final int UPLOAD_ACD = 102;
	public static final String RESULT_UPLOAD_ACD = "upload_acd";
	public static final String UPLOAD_ACD_BEAN = "acd_bean";
	public static final String RESULT_UP_TRUCK_VEH = "truck_veh";
	public static final String RESULT_UP_TRUCK_DRV = "truck_drv";

	private Handler mHandler;
	private int queryCata;
	private Object[] params;
	private ProgressDialog progressDialog;
	private Context context;

	/**
	 * 
	 * @param mHandler
	 *            回调
	 * @param queryCata
	 *            操作类型
	 * @param params
	 *            上传对象
	 * @param context
	 *            上下文
	 */
	public CommUploadThread(Handler mHandler, int queryCata, Object[] params,
			Context context) {
		this.mHandler = mHandler;
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
		Message msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		if (queryCata == UPLOAD_TRUCK_VEH) {
			TruckVehicleBean tv = (TruckVehicleBean) params[0];
			WebQueryResult<ZapcReturn> re = dao.uploadTruckVeh(tv);
			b.putSerializable(RESULT_UP_TRUCK_VEH, re);
		} else if (queryCata == UPLOAD_TRUCK_DRV) {
			TruckDriverBean drv = (TruckDriverBean) params[0];
			WebQueryResult<ZapcReturn> re = dao.uploadTruckDrv(drv);
			b.putSerializable(RESULT_UP_TRUCK_DRV, re);
		} else if (queryCata == UPLOAD_ACD) {
			AcdSimpleBean acd = (AcdSimpleBean) params[0];
			ArrayList<AcdSimpleHumanBean> humans = (ArrayList<AcdSimpleHumanBean>) params[1];
			WebQueryResult<ZapcReturn> re = dao.uploadAcdInfo(acd, humans);
			b.putSerializable(RESULT_UPLOAD_ACD, re);
			b.putSerializable(UPLOAD_ACD_BEAN, acd);
		}
		msg.setData(b);
		mHandler.sendMessage(msg);
		if (progressDialog.isShowing())
			progressDialog.dismiss();
	}
}
