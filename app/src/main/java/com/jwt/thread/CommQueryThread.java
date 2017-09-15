package com.jwt.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.SchoolZtzBean;
import com.jwt.bean.TruckCheckBean;
import com.jwt.bean.WfxwCllxCheckBean;
import com.jwt.event.CommEvent;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.jbyw.VioDrvBean;
import com.jwt.jbyw.VioVehBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by lixiaodong on 2017/9/8.
 */

public class CommQueryThread extends Thread {

    public static final int QUERY_DRV_DABH_INFO = 100;
    public static final int QUERY_TRUCK_CHECK = 101;
    public static final int QUERY_VEH_HPHM_INFO = 200;
    public static final int QUERY_QYMC_INFO = 210;
    public static final int QUERY_ZHCX = 400;
    public static final int QUERY_WFXW_CLLX_CHECK = 401;
    public static final int QUERY_FXCZF_RKQK = 403;
    public static final int DEL_PHOTO_FILE = 404;
    public static final int OPER_BJBD = 405;
    public static final int JSON_QUERY_DRV = 701;
    public static final int JSON_QUERY_VEH = 702;
    public static final int QUERY_SCHOOL = 600;
    public static final int CHECK_DRV_VEH = 800;
    public static final String RESULT_QYMC = "RESULT_QYMC";
    private int queryCata;
    private String[] params;
    private ProgressDialog progressDialog;
    private Context context;

    public CommQueryThread(int queryCata, String[] params,
                           Context context) {
        this.queryCata = queryCata;
        this.params = params;
        this.context = context;
    }

    public void doStart() {
        Log.e("CommQueryThread", "do start");
        progressDialog = ProgressDialog.show(context, "提示", "正在操作,请稍等...",
                true);
        progressDialog.setCancelable(true);
        start();
    }

    @Override
    public void run() {
        RestfulDao dao = RestfulDaoFactory.getDao();
        if (queryCata == QUERY_DRV_DABH_INFO) {
            WebQueryResult<VioDrvBean> re = dao.queryVioDrv(params[0],
                    TextUtils.equals(params[1], "苏F") ? "1" : "0");
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_VEH_HPHM_INFO) {
            WebQueryResult<VioVehBean> re = dao.queryVioVeh(params[0],
                    params[1]);
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_ZHCX) {
            WebQueryResult<GlobalQueryResult> re = dao.zhcxRestful(
                    params[0], params[1]);
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_QYMC_INFO) {
            WebQueryResult<List<KeyValueBean>> re = dao.queryAllQymc();
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_TRUCK_CHECK) {
            WebQueryResult<TruckCheckBean> re = dao.queryTruckCheck(
                    params[0], params[1]);
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_WFXW_CLLX_CHECK) {
            WebQueryResult<String> re = dao.getAllWfxwCllxCheck();
            WebQueryResult<List<WfxwCllxCheckBean>> wr = GlobalMethod
                    .webXmlStrToListObj(re, WfxwCllxCheckBean.class);
            EventBus.getDefault().post(wr);
        } else if (queryCata == QUERY_FXCZF_RKQK) {
            WebQueryResult<String> re = dao.queryFxcRkqk(params[0]);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            CommEvent event = new CommEvent();
            if(!TextUtils.isEmpty(err)){
                event.setStatus(0);
                event.setMessage(err);
            }else{
                event.setStatus(200);
                event.setMessage(re.getResult());
            }
            EventBus.getDefault().post(event);
        } else if (queryCata == DEL_PHOTO_FILE) {
            int row = 0;
            if (params != null && params.length > 0) {
                for (String fs : params) {
                    File f = new File(fs);
                    if (f.exists())
                        row += f.delete() ? 1 : 0;
                }
            }
            EventBus.getDefault().post(row);
            Log.e("QueryDrvVehThread", "del row: " + row);
        } else if (queryCata == OPER_BJBD) {
            WebQueryResult<KeyValueBean> re = dao.getBjbdBySfzh(params[0]);
            EventBus.getDefault().post(re);
        } else if (queryCata == QUERY_SCHOOL) {
            WebQueryResult<SchoolZtzBean> re = dao.querySchool(params[0]);
            EventBus.getDefault().post(re);
        } else if (queryCata == JSON_QUERY_DRV) {
            WebQueryResult<String> json = dao.jycxQueryDrv(params[0], params[1]);
            String err = GlobalMethod.getErrorMessageFromWeb(json);
            JSONObject obj = new JSONObject();
            if (TextUtils.isEmpty(err))
                obj = ParserJson.getJsonObject(json.getResult());
            else
                ParserJson.putJsonVal(obj, "err", err);
            ParserJson.putJsonVal(obj, "catalog", JSON_QUERY_DRV);
            EventBus.getDefault().post(obj);
        } else if (queryCata == JSON_QUERY_VEH) {
            WebQueryResult<String> json = dao.jycxQueryVeh(params[0], params[1]);
            String err = GlobalMethod.getErrorMessageFromWeb(json);
            JSONObject obj = new JSONObject();
            if (TextUtils.isEmpty(err))
                obj = ParserJson.getJsonObject(json.getResult());
            else
                ParserJson.putJsonVal(obj, "err", err);
            ParserJson.putJsonVal(obj, "catalog", JSON_QUERY_VEH);
            EventBus.getDefault().post(obj);
        }
        Log.e("QueryDrvVehThread", "over thread");
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
