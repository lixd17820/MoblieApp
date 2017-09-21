package com.jwt.thread;

import android.text.TextUtils;

import com.jwt.bean.LoginResultBean;
import com.jwt.event.LoginEvent;
import com.jwt.globalquery.CxMenus;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lixiaodong on 2017/8/25.
 */

public class LoginUpdateThread extends Thread {
    private String mjjh;
    private String mm;
    private String serial;


    public void doStart(String mjjh, String mm, String serial) {
        // 显示进度对话框
        this.mjjh = mjjh;
        this.mm = mm;
        this.serial = serial;
        this.start();

    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        // 目前处于登录阶段
        // int state = LOGIN_STATE;
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<String> login = dao.checkUserAndUpdate(
                mjjh, mm, serial);
        String err = GlobalMethod.getErrorMessageFromWeb(login);
        LoginEvent event = new LoginEvent();
        if(!TextUtils.isEmpty(err)){
            event.setStatus(0);
            event.setStMs(err);
            EventBus.getDefault().post(event);
            return;
        }
        LoginResultBean result = null;
        try {
            result = CommParserXml.parseXmlToObj(login.getResult(),
                    LoginResultBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result == null){
            event.setStatus(200);
            event.setStMs("服务器错误");
            EventBus.getDefault().post(event);
            return;
        }
        if(!TextUtils.equals("1",result.getCode())){
            event.setStatus(200);
            event.setStMs(result.getCwms());
            EventBus.getDefault().post(event);
            return;
        }
        WebQueryResult<List<CxMenus>> cxMenus = dao.restfulGetMenus();
        event.setStatus(login.getStatus());
        event.setStMs(login.getStMs());
        event.setResult(result);
        event.setCxMenuStr(ParserJson.arrayToJsonArray(cxMenus.getResult()).toString());
        EventBus.getDefault().post(event);
    }
}
