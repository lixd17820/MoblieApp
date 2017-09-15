package com.jwt.thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jwt.bean.SpringDjItf;
import com.jwt.bean.SpringKcdjBean;
import com.jwt.bean.SpringWhpdjBean;
import com.jwt.dao.MessageDao;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;


public class UploadSpringThread extends Thread {
	public static final String UPLOAD_RESULT = "uploadResult";

	private Handler mHandler;
	private SpringDjItf dj;
	private Context context;

	public UploadSpringThread(Handler handler, SpringDjItf dj, Context context) {
		this.mHandler = handler;
		this.dj = dj;
		this.context = context;
	}

	/**
	 * 线程运行
	 */
	@Override
	public void run() {
		RestfulDao dao = RestfulDaoFactory.getDao();
		WebQueryResult<ZapcReturn> rs = null;
		if (dj.getDjlx() == 0) {
			SpringKcdjBean kcdj = MessageDao.queryKcdjById(dj.getId(), GlobalMethod.getBoxStore(context));
			rs = dao.uploadSpringKcdj(kcdj);
		} else {
			SpringWhpdjBean whpdj = MessageDao.queryWhpdjById(dj.getId(), GlobalMethod.getBoxStore(context));
			rs = dao.uploadSpringWhpdj(whpdj);
		}
		Message msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putSerializable(UPLOAD_RESULT, rs);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}
}
