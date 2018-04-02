package com.jwt.thread;

import android.app.Activity;
import android.util.Log;

import com.jwt.event.DownDictEvent;
import com.jwt.globalquery.CxMenus;
import com.jwt.pojo.AcdLawBean;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmCode_;
import com.jwt.pojo.FrmDptCode;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.JtssParam;
import com.jwt.pojo.SeriousStreetBean;
import com.jwt.pojo.SysParaValue;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.pojo.WfxwForce;
import com.jwt.pojo.ZapcLxxx;
import com.jwt.utils.DictName;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;
import com.jwt.utils.ThreadMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/8/25.
 */

public class UpdateDictThread extends Thread {

    private Box<FrmCode> frmCodeBox;
    private Box<FrmRoadItem> frmRoadItemBox;
    private Box<FrmRoadSeg> frmRoadSegBox;
    private Box<VioWfdmCode> vioWfdmCodeBox;
    private Box<SysParaValue> vioSysParaBox;
    private BoxStore bs;
    private Activity activity;
    //是否强制同步
    private boolean forceDownload = false;

    public UpdateDictThread(BoxStore _bs, Activity activity) {
        this.bs = _bs;
        this.frmCodeBox = bs.boxFor(FrmCode.class);
        this.frmRoadItemBox = bs.boxFor(FrmRoadItem.class);
        this.frmRoadSegBox = bs.boxFor(FrmRoadSeg.class);
        this.vioWfdmCodeBox = _bs.boxFor(VioWfdmCode.class);
        this.vioSysParaBox = bs.boxFor(SysParaValue.class);
        this.activity = activity;
    }

    /**
     * 线程运行
     */
    @Override
    public void run() {
        String dataVersion = "dataVersion";
        //Log.e("Object box test", "frm2 code count: " + box.count() + "");
        // 目前处于登录阶段
        // int state = LOGIN_STATE;
        RestfulDao dao = RestfulDaoFactory.getDao();
        WebQueryResult<List<CxMenus>> cxMenus = dao.restfulGetMenus();
        if (cxMenus.getStatus() == 200 && cxMenus.getResult() != null) {
            GlobalData.zhcxMenus = cxMenus.getResult();
            GlobalSystemParam.loadParam(activity, GlobalConstant.SP_CX_MENUS,
                    ParserJson.arrayToJsonArray(cxMenus.getResult()).toString());
        }
        //下载决定书号码，一定同步

        DictName[] vs = DictName.values();

        //下载各种数据版本
        WebQueryResult<String> dv = dao.downloadSqlValue("select name,version from t_jwt_data_version", "");
        String dvs = GlobalSystemParam.getParam(activity, dataVersion, "{}");
        JSONObject savedVer = ParserJson.getJsonObject(dvs);
        Log.e("UpdateDict", "saved: " + dvs.toString());
        JSONObject version = new JSONObject();
        if (dv.getStatus() == 200 && dv.getResult() != null && dv.getResult().startsWith("[")) {
            Log.e("UpdateDict", "server: " + dv.getResult());
            version = ParserJson.arrayToObj(ParserJson.getJsonArray(dv.getResult()), "name", "version");

        }
        int add = 12;
        int len = vs.length;
        int total = len + add;
        int step = len - 1;
        Log.e("update dict", "1：");
        downloadFrmCode(dao, vs, add, savedVer, version);
        Log.e("update dict", "2");
        EventBus.getDefault().post(new DownDictEvent("初始化数据", total, ++step, "加载内存中..."));
        Log.e("update dict", "3");
        int c = GlobalData.initGlobalData(bs);
        Log.e("update dict", "4");
        EventBus.getDefault().post(new DownDictEvent("初始化数据", total, ++step, "加载内存中..."));
        Log.e("update dict", "字典表共：" + c);

        long wfdmCount = vioWfdmCodeBox.count();
        long roadItemCount = frmRoadItemBox.count();
        long roadSegCount = frmRoadSegBox.count();
        long sysParaCount = vioSysParaBox.count();
        long dptCodeCount = bs.boxFor(FrmDptCode.class).count();
        long lxxxCount = bs.boxFor(ZapcLxxx.class).count();
        long acdlawCount = bs.boxFor(AcdLawBean.class).count();
        long jtssParmaCount = bs.boxFor(JtssParam.class).count();

        EventBus.getDefault().post(new DownDictEvent("下载事故依据", total, ++step, "下载事故依据..."));
        if (acdlawCount <= 0 || savedVer.optInt(acdlawVersion) < version.optInt(acdlawVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select xh,flmc,tkmc,tknr from trff_app.acd_laws", "");
            ThreadMethod.saveAcdLawInDb(dict2, GlobalMethod.getBoxStore(activity).boxFor(AcdLawBean.class));
            ParserJson.putJsonVal(savedVer, acdlawVersion, version.optInt(acdlawVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载违法代码..."));
        if (wfdmCount <= 0 || savedVer.optInt(wfdmVersion) < version.optInt(wfdmVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.updateOtherDict("wfdm", "1");
            ThreadMethod.saveWfdmInDb(dict2, vioWfdmCodeBox);
            ParserJson.putJsonVal(savedVer, wfdmVersion, version.optInt(wfdmVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载强制措施..."));
        Box<WfxwForce> forceBox = bs.boxFor(WfxwForce.class);
        long count = forceBox.count();
        if (count <= 0 || savedVer.optInt(forceVersion) < version.optInt(forceVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select wfdm,wfxw,qzyj,zt,to_char(gxsj,'yyyy-mm-dd'),bz from forcecode", "");
            ThreadMethod.saveForceDb(dict2, forceBox);
            ParserJson.putJsonVal(savedVer, forceVersion, version.optInt(forceVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载道路代码..."));
        if (roadItemCount <= 0 || savedVer.optInt(roadVersion) < version.optInt(roadVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.updateOtherDict("road_item", "1");
            ThreadMethod.saveRoadItemInDb(dict2, frmRoadItemBox);
            ParserJson.putJsonVal(savedVer, roadVersion, version.optInt(roadVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载路段代码..."));
        if (roadSegCount <= 0 || savedVer.optInt(roadVersion) < version.optInt(roadVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.updateOtherDict("road_seg", "1");
            ThreadMethod.saveRoadSegInDb(dict2, frmRoadSegBox);
            ParserJson.putJsonVal(savedVer, roadVersion, version.optInt(roadVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载系统参数..."));
        if (sysParaCount <= 0 || savedVer.optInt(sysVersion) < version.optInt(sysVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select xtlb,glbm,gjz,csz,csbj,bjcsbj from trff_app.frm_syspara_value", "");
            ThreadMethod.saveSysParaInDb(dict2, vioSysParaBox);
            ParserJson.putJsonVal(savedVer, sysVersion, version.optInt(sysVersion));
            Log.e("updateDict", "step: " + step);
        }
        //根据严管街版本下载
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载严管街..."));
        Box<SeriousStreetBean> box = GlobalMethod.getBoxStore(activity).boxFor(SeriousStreetBean.class);
        long streeCount = box.count();
        if (streeCount <= 0 || savedVer.optInt(streeVersion) < version.optInt(streeVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select wfdd,n_wfxw nwfxw,s_version version from t_jwt_serious_street", "");
            ThreadMethod.saveStreeDataInDb(dict2, box);
            ParserJson.putJsonVal(savedVer, streeVersion, version.optInt(streeVersion));
            Log.e("updateDict", "step: " + step);
        }
        //大平台字典
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载大平台字典表..."));
        if (dptCodeCount <= 0 || savedVer.optInt(dptVersion) < version.optInt(dptVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select xtlb,dmlb,dmz,dmsm1,dmsm2,dmlbsm,zt from frm_dpt_code", "");
            ThreadMethod.saveDptCodeInDb(dict2, bs.boxFor(FrmDptCode.class));
            ParserJson.putJsonVal(savedVer, dptVersion, version.optInt(dptVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载盘查路线..."));
        if (lxxxCount <= 0 || savedVer.optInt(lxxxVersion) < version.optInt(lxxxVersion) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadSqlValue("select xldm,xlxl,trffbh from v_jwt_zapc_lxxx", "");
            ThreadMethod.saveZapcLxxxInDb(dict2, bs.boxFor(ZapcLxxx.class));
            ParserJson.putJsonVal(savedVer, lxxxVersion, version.optInt(lxxxVersion));
            Log.e("updateDict", "step: " + step);
        }
        EventBus.getDefault().post(new DownDictEvent("下载数据", total, ++step, "下载交通设施字典表..."));
        if (jtssParmaCount <= 0 || savedVer.optInt(jtssVesion,0) < version.optInt(jtssVesion,0) || forceDownload) {
            WebQueryResult<String> dict2 = dao.downloadJtssParam();
            if (dict2.getStatus() == 200) {
                ThreadMethod.saveJtssParamInDb(dict2, bs.boxFor(JtssParam.class));
                ParserJson.putJsonVal(savedVer, jtssVesion, version.optInt(jtssVesion));
            }
        }
        //保存版本号的数据
        GlobalSystemParam.loadParam(activity, dataVersion, savedVer.toString());
        EventBus.getDefault().post(new DownDictEvent("", total, total, ""));
        Log.e("UpdateDict", "has saved: " + savedVer.toString());
    }

    private static String codeVersion = "codeVersion",
            roadVersion = "roadVersion", wfdmVersion = "wfdmVersion",
            forceVersion = "forceVersion", sysVersion = "sysVersion",
            streeVersion = "streeVersion", dptVersion = "dptVersion",
            lxxxVersion = "lxxxVersion", acdlawVersion = "acdlawVersion",
            jtssVesion = "jtssVersion";

    /**
     * 保存数据到字典表数据库中
     *
     * @param dao
     */
    private void downloadFrmCode(RestfulDao dao, DictName[] vs, int add, JSONObject savedVer, JSONObject version) {
        int servceVersion = version.optInt(codeVersion);
        boolean isNeed = savedVer.optInt(codeVersion) < servceVersion;
        if (isNeed)
            frmCodeBox.removeAll();
        int total = vs.length + add;
        EventBus.getDefault().post(new DownDictEvent("联网下载数据", total, 0, "联网下载中"));
        int step = 0;
        for (DictName d : vs) {
            step++;
            String[] code = d.getCode().split("/");
            long count = frmCodeBox.query().equal(FrmCode_.xtlb, code[0]).equal(FrmCode_.dmlb, code[1]).build().count();
            if (count <= 0 || isNeed) {
                WebQueryResult<String> dict = dao.updateDictMap(
                        code[0], code[1]);
                if (dict.getStatus() != 200) {
                    Log.e("downloadFrmCode", d.getName() + "下载出现错误");
                    continue;
                }
                String re = dict.getResult();
                List<FrmCode> codes = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(re);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        FrmCode frmCode = ParserJson.parseJsonToObj(obj, FrmCode.class);
                        codes.add(frmCode);
                    }
                    if (codes != null && codes.size() > 0) {
                        frmCodeBox.put(codes);
                    }
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new DownDictEvent("联网下载数据", total, step, d.getName()));
            }
        }
        ParserJson.putJsonVal(savedVer, codeVersion, servceVersion);
    }


}
