package com.jwt.thread;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.dao.WsglDAO;
import com.jwt.event.DownSpeedEvent;
import com.jwt.pojo.THmb;
import com.jwt.update.App;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.objectbox.Box;

/**
 * Created by lixiaodong on 2017/8/29.
 */

public class WsglThread extends Thread {

    private Activity self;
    private String jh;
    private String TAG = "WsglThread";

    public WsglThread(Activity self, String jh) {
        this.self = self;
        this.jh = jh;
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        synJdsHmb(dao, jh);
    }

    private void synJdsHmb(RestfulDao dao, String jh) {
        if (TextUtils.isEmpty(jh)) {
            EventBus.getDefault().post(new DownSpeedEvent("警号不能为空", 1, 0, ""));
            return;
        }
        boolean isok = downloadHmb(dao);
        EventBus.getDefault().post(new DownSpeedEvent((isok ? "更新成功" : "更新失败"), 1, 1, ""));
    }

    public boolean downloadHmb(RestfulDao dao) {
        Box<THmb> box = ((App) self.getApplication()).getBoxStore().boxFor(THmb.class);
        WebQueryResult<List<THmb>> webHmbs = dao.hqVioWs(jh, "");
        String err = GlobalMethod.getErrorMessageFromWeb(webHmbs);
        if (!TextUtils.isEmpty(err))
            return false;
        List<THmb> hmbs = webHmbs.getResult();
        if (hmbs == null || hmbs.isEmpty())
            return false;
        String[] hds = {"1", "3", "9"};
        // 检查目前号码表是否在服务器获取的列表中，
        // 如果不在，则删除，不上交，由服务器决定是否已调动单位
        for (String hd : hds) {
            List<THmb> curJds = WsglDAO.getJdsListByHdzl(hd, jh,
                    GlobalMethod.getBoxStore(self));
            if (curJds == null || curJds.isEmpty())
                continue;
            for (THmb tHmb : curJds) {
                if (!checkHmbInWebHmbList(tHmb, hmbs)) {
                    box.remove(tHmb);
                }
            }
        }
        // 与本地的号码表进行比对, 相同则不做更新
        for (THmb hmb : hmbs) {
            // 转换服务器种类为警务通种类
            try {
                Log.e(TAG, ParserJson.objToJson(hmb).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String jwthd = GlobalConstant.hdzh.get(hmb.getHdzl());
            if (TextUtils.equals("009", jwthd))
                jwthd = "9";
            Log.e(TAG, "jwthd " + jwthd);
            hmb.setHdzl(jwthd);
            THmb curHmb = WsglDAO.getHmbByHdId(hmb.getHdid(), hmb.getHdzl(), GlobalMethod.getBoxStore(self));
            if (curHmb == null) {
                // 该民警还未获取法律文书，直接将获取的文书写入到表中
                box.put(hmb);
                Log.e(TAG, "cyrHmb is null and save. id: " + hmb.getId());
            } else {
                long serverDqhm = Long.valueOf(hmb.getDqhm());
                long clientDqhm = Long.valueOf(curHmb.getDqhm());
                Log.e(TAG, serverDqhm + "/" + clientDqhm);
                if (serverDqhm > clientDqhm) {
                    // 服务器大则更新本地，
                    curHmb.setDqhm(hmb.getDqhm());
                    box.put(curHmb);
                    Log.e(TAG, "服务器大则更新本地. " + jwthd);
                } else if (serverDqhm < clientDqhm) {
                    // 服务器小则更新服务器，当前值减一
                    curHmb.setDqhm((Long.valueOf(curHmb.getDqhm()) - 1) + "");
                    Log.e(TAG, "服务器小则更新服务器，当前值减一. " + jwthd);
                    dao.synVioWs(curHmb, jh);
                } else {
                    // 全部相符合，不更新本地的数据
                    Log.e(TAG, "全部相符合，不更新本地的数据. " + jwthd);
                }
            }
        }
        return true;
    }

    private boolean checkHmbInWebHmbList(THmb tHmb, List<THmb> webHmbs) {
        for (THmb h : webHmbs) {
            if (TextUtils.equals(tHmb.getHdid(), h.getHdid()))
                return true;
        }
        return false;
    }

}
