package com.jwt.thread;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jwt.event.ServiceEvent;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;

public class LogoutJwtNewThread extends Thread {

    public LogoutJwtNewThread() {

    }

    public void doStart() {
        this.start();
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        WebQueryResult<String> result = dao.logoutJwt(jh,
                GlobalData.serialNumber);
        String err = GlobalMethod.getErrorMessageFromWeb(result);
        ServiceEvent event = new ServiceEvent();
        event.catalog = GlobalConstant.SERVCIE_LOGOUT;
        event.code = TextUtils.isEmpty(err) && result != null && TextUtils.equals(result.getResult(), "1");
        EventBus.getDefault().post(event);
    }
}
