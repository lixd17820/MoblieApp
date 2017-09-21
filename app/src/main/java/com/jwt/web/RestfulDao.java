package com.jwt.web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.bean.GcmBbInfoBean;
import com.jwt.bean.GcmBbddBean;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.MjJobBean;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.event.DownApkEvent;
import com.jwt.pojo.RepairBean;
import com.jwt.bean.SchoolZtzBean;
import com.jwt.bean.SpringKcdjBean;
import com.jwt.bean.SpringWhpdjBean;
import com.jwt.bean.TTViolation;
import com.jwt.bean.TruckCheckBean;
import com.jwt.pojo.TruckDriverBean;
import com.jwt.bean.TruckVehicleBean;
import com.jwt.bean.UpdateFile;
import com.jwt.globalquery.CxMenus;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.jbyw.JdsUnjkPrintBean;
import com.jwt.jbyw.VioDrvBean;
import com.jwt.jbyw.VioVehBean;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.pojo.THmb;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.TypeCenvert;
import com.jwt.utils.ZipUtils;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.zapc.ZapcReturn;
import com.jwt.bean.ZapcRypcxxBean;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class RestfulDao {


    private boolean isDebug = false;

    public final static int GET = 0;
    public final static int POST = 1;
    private int timeoutConnection = 30000;
    private int timeoutSocket = 30000;
    private String URL_PATH = "/ydjw/services/ydjw/";
    private String FORBID_PATH = "/forbid/services/forbid/";
    private String CROSS_PATH = "/cross/services/cross";
    private String ZHCXURL = URL_PATH + "newQuery";
    private String menuUrl = URL_PATH + "zhcxMenus";
    protected String PIC_URL = "/phototemp/";
    protected String JQTB_FILE_URL = "/ydjw/DownloadJqtbFile";

    private String GZXXUPLOAD = URL_PATH + "zapcTestUpload";
    private String RYXXUPLOAD = URL_PATH + "ryxxTestUpload";
    private String WPXXUPLOAD = URL_PATH + "wpxxTestUpload";

    private String ACDUPLOAD = URL_PATH + "uploadAcd";
    private String ACDBILLHQ = URL_PATH + "billAcdHq";
    private String ACDBILLSJ = URL_PATH + "billAcdSj";
    private String UPLOADREP = URL_PATH + "uploadRepair";
    protected String UPLOAD_REP_PIC = URL_PATH + "uploadRepairPic";
    private String UPLOADVIO = URL_PATH + "uploadVio";
    private String UPLOADVIO_MOBILE = URL_PATH + "uploadViolationMobile";

    private String UPLOADVIO_PIC = URL_PATH + "uploadViolationPic";

    private String GAIN_VIO_BILL = URL_PATH + "gainVioBill";
    private String BACK_VIO_BILL = URL_PATH + "backVioBill";

    private String SYN_VIO_BILL = URL_PATH + "synVioBill";

    private String SYS_CONFIG = URL_PATH + "getSysConfig";
    private String QUERY_MJ_JOB = URL_PATH + "queryMjJob";
    private String UPLOAD_ACD_RECODE = URL_PATH + "uploadAcdRecode";
    private String UPLOAD_ACD_PHOTO = URL_PATH + "uploadAcdPhoto";

    private String QUERY_VIO_DRV = URL_PATH + "queryVioDrv";
    private String QUERY_VIO_VEH = URL_PATH + "queryVioVeh";
    private String JYCX_QUERY_DRV = URL_PATH + "jycxQueryDrv";
    private String JYCX_QUERY_VEH = URL_PATH + "jycxQueryVeh";


    private String QUERY_JQTB = URL_PATH + "getJqtb";

    private String GAT_ALL_GCM_DD = URL_PATH + "allBbdd";

    private String UPLOAD_GCM_INFO = URL_PATH + "bbinfoUpload";

    private String GET_UN_JK_JDS = URL_PATH + "jdsPrint";

    private String IS_OPEN_UNJK = URL_PATH + "isUnjk";

    private String GPS_UPLOAD = URL_PATH + "uploadGpsInfo";

    private String CHECK_USER = URL_PATH + "checkJwtUser";

    private String CHECK_SIX_SP = URL_PATH + "isSixSp";

    private String QUERY_SIX_SP = URL_PATH + "getAllSixUnsp";

    private String SUBMIT_SIX_SP = URL_PATH + "sixSp";

    private String LOGOUT_JWT = URL_PATH + "logoutJwtUser";

    private String GAT_ALL_ICON = URL_PATH + "getSysIconList";

    private String QUEYR_VIO_RKQK = URL_PATH + "queryVioRkqk";

    private String IS_DOWNLOAD_ICON = URL_PATH + "isDownloadIcon";

    private String GET_SYSTEM_TIME = URL_PATH + "getSystemTime";

    private String IS_DUP_VIO = URL_PATH + "isDupVio";

    private String UPLOAD_SPRING_KCDJ = URL_PATH + "uploadSpingKcdj";

    private String UPLOAD_SPRING_WHPDJ = URL_PATH + "uploadSpingWhpdj";

    private String QUERY_TRUCK_CHECK = URL_PATH + "queryTruckCheck";
    private String UPLOAD_TRUCK_VEH = URL_PATH + "uploadTruckVeh";
    private String UPLOAD_TRUCK_DRV = URL_PATH + "uploadTruckDrv";

    private String QUERY_ALL_QYMC = URL_PATH + "queryAllQymc";

    private String GET_ALL_WFXW_CLLX = URL_PATH + "allWfxwCllx";

    private final String UPLOAD_FXC_PHOTO_CHECK = URL_PATH + "uploadFxcPhotoCheck";

    private final String UPLOAD_FXC_PHOTO = URL_PATH + "uploadFxcPhoto";

    private final String UPLOAD_FXC_JL = URL_PATH + "uploadFxcJl";

    private final String UPLOAD_FXC_PHOTO_MD5 = URL_PATH + "uploadFxcPhotoMd5";

    private final String UPLOAD_FXC_JL_IMG = URL_PATH + "uploadFxcJlImg";

    private final String CHECK_USER_MD5 = URL_PATH + "checkUserMd5";

    private final String TEST_NETWORK = URL_PATH + "testNetwork";

    private final String QUERY_FXC_RKQK = URL_PATH + "queryFxcRkqk";

    private final String ZHBD = URL_PATH + "zhbd";

    private final String QUERY_CLGJ = URL_PATH + "queryClgj";

    private final String QUERY_SCHOOL = URL_PATH + "querySchoolZtz";

    //程序文件更新
    private final String UPDATE_FILE = URL_PATH + "updateFileVersion";

    private final String SERIOUS_STREET = URL_PATH + "getSeriousStreet";

    private final String CHECK_FXC_ALL_OK = URL_PATH + "checkFxcAllOk";

    private final String CHECK_FXC_ZQMJ_ALL_UPLOAD = URL_PATH + "checkFxcZqmjAllUpload";

    private final String CHECK_FXC_TZSH = URL_PATH + "checkFxcTzsh";

    private String checkUrl = "/ydjw/services/login/checkJwtUser";
    private String checkMd5 = "/ydjw/services/ydjw/checkJwtUserNew";

    //---------------------------禁区通行证常量-------------------------------
    private final String QUERY_FROBID = FORBID_PATH + "queryPassInfo";


//    /**
//     * 获取严管违停信息
//     *
//     * @param version
//     * @return 实体为空或返回码不为200，不更新
//     */
//    public WebQueryResult<List<SeriousStreetBean>> getSeriousStreet(String version) {
//        return GlobalMethod.webXmlStrToListObj(
//                httpTextClient(getUrl() + SERIOUS_STREET, POST, "version", version),
//                SeriousStreetBean.class);
//    }


    /**
     * 系统文件更新信息，用于和本地文件进行比对
     *
     * @return
     */
    public WebQueryResult<List<UpdateFile>> updateInfoRestful() {

        WebQueryResult<String> re = httpTextClient(getUrl() + UPDATE_FILE, GET);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        WebQueryResult<List<UpdateFile>> res = null;
        if (TextUtils.isEmpty(err)) {
            res = GlobalMethod.webXmlStrToListObj(re, UpdateFile.class);
        }
        return res;
    }


    /**
     * 测试网络连接
     *
     * @return true 可连接 false 不可以连接
     */
    public boolean testNetwork() {
        WebQueryResult<String> re = httpTextClient(getUrl() + TEST_NETWORK, GET);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            return re != null && re.getResult() != null && "OK".equals(re.getResult());
        }
        return false;
    }

    /**
     * 全局综合查询
     *
     * @param cxid
     * @param conds
     * @return
     */
    public WebQueryResult<GlobalQueryResult> zhcxRestful(String cxid,
                                                         String conds) {
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + ZHCXURL, POST, "cxid", cxid, "conds", conds),
                GlobalQueryResult.class);
    }

    /**
     * 获取系统菜单
     *
     * @return
     */
    public WebQueryResult<String> getSystemMenu() {
        return httpTextClient(getUrl() + menuUrl, GET);
    }

    public WebQueryResult<List<CxMenus>> restfulGetMenus() {
        WebQueryResult<String> ws = getSystemMenu();
        return GlobalMethod.webXmlStrToListObj(ws, CxMenus.class);
    }

    /**
     * 获取系统变量参数配置
     *
     * @return
     */
    public WebQueryResult<List<KeyValueBean>> restfulGetSysConfig() {
        WebQueryResult<String> ws = httpTextClient(getUrl() + SYS_CONFIG, GET);
        return GlobalMethod.webXmlStrToListObj(ws, KeyValueBean.class);
    }

    /**
     * 上传工作信息
     *
     * @param gzxx
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcGzxx(ZapcGzxxBean gzxx) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(gzxx);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + GZXXUPLOAD, POST, "gzxx", xml), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传人员盘查信息
     *
     * @param ryxx
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcRypcxx(com.jwt.bean.ZapcRypcxxBean ryxx,
                                                       String gzid, String kssj) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(ryxx);
            Map<String, String> postParams = new HashMap<>();
            postParams.put("ryxx", xml);
            postParams.put("dwdm", GlobalData.grxx
                    .get(GlobalConstant.YBMBH));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.put("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh);
            postParams.put("gzid", gzid);
            postParams.put("kssj", kssj);
            re = GlobalMethod
                    .webXmlStrToObj(
                            httpTextClient(getUrl() + RYXXUPLOAD,
                                    POST, postParams), ZapcReturn.class
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传物品信息
     *
     * @param wpxx 物品信息
     * @param gzid 工作号
     * @param kssj 开始时间
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadZapcWpxx(ZapcWppcxxBean wpxx,
                                                     String gzid, String kssj) {
        WebQueryResult<ZapcReturn> re = null;
        try {
            String xml = CommParserXml.objToXml(wpxx);
            Map<String, String> postParams = new HashMap<>();
            postParams.put("wpxx", xml);
            postParams.put("dwdm", GlobalData.grxx
                    .get(GlobalConstant.YBMBH));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.put("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh);
            postParams.put("gzid", gzid);
            postParams.put("kssj", kssj);
            re = GlobalMethod
                    .webXmlStrToObj(
                            httpTextClient(getUrl() + WPXXUPLOAD, POST, postParams), ZapcReturn.class
                    );
            // (uploadAction(postParams, WPXXUPLOAD),
            // ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }


    /**
     * 上传交通事故
     *
     * @param acd
     * @param humans
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdInfo(AcdSimpleBean acd,
                                                    ArrayList<AcdSimpleHumanBean> humans) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            String xml = CommParserXml.objToXml(acd);
            String ryxx = "";
            for (AcdSimpleHumanBean human : humans) {
                ryxx += AcdSimpleDao.createRyxxStr(human);
            }
            Map<String, String> postParams = new HashMap<>();
            postParams.put("acd", xml);
            postParams.put("ryxx", ryxx);
            postParams.put("dwdm", GlobalData.grxx
                    .get(GlobalConstant.BMBH));
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            postParams.put("jybh", jh.length() == 8 ? jh
                    .substring(2) : jh);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + ACDUPLOAD, POST, postParams),
                    ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

    /**
     * 获取简易事故文书编号
     *
     * @param jybh
     * @param glbm
     * @param hdzl
     * @return
     */
    public WebQueryResult<THmb> hqAcdWs(String jybh, String glbm, String hdzl) {
        // WebQueryResult<THmb> res = new WebQueryResult<THmb>();
        Map<String, String> postParams = new HashMap<>();
        postParams.put("jybh", jybh);
        postParams.put("glbm", glbm);
        postParams.put("hdzl", hdzl);
        WebQueryResult<String> rs = httpTextClient(getUrl() + ACDBILLHQ, POST, postParams);
        return GlobalMethod.webXmlStrToObj(rs, THmb.class);
    }

    /**
     * 简易事故程序文书上交
     *
     * @param hdids
     * @param wsbhs
     * @return
     */
    public WebQueryResult<String> sjAcdWs(String hdids, String wsbhs) {
        WebQueryResult<String> rs = httpTextClient(getUrl() + ACDBILLSJ,
                POST, "hdids", hdids, "wsbhs", wsbhs);
        // restfulPostQuery(postParams, ACDBILLSJ);
        return rs;
    }

    /**
     * 上传报修文字信息
     *
     * @param rep
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadRepair(RepairBean rep) {
        try {
            String xml = CommParserXml.objToXml(rep);
            String jh = GlobalData.grxx.get(GlobalConstant.JH);
            WebQueryResult<String> r = httpTextClient(getUrl() + UPLOADREP,
                    POST, "rep", xml, "dwdm", GlobalData.grxx.get(
                            GlobalConstant.YBMBH).substring(0, 6),
                    "jybh", jh.length() == 8 ? jh
                            .substring(2) : jh
            );
            return GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传简易处罚
     *
     * @param vio
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadViolation(VioViolation vio) {
        WebQueryResult<ZapcReturn> re = null;// new
        // WebQueryResult<ZapcReturn>();
        try {
            String xml = CommParserXml.objToXml(vio);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + UPLOADVIO, POST, "vio", xml),
                    ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传简易处罚
     *
     * @param vio
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadViolationMobile(JSONObject vio) {
        try {
            WebQueryResult<String> re = httpTextClient(getUrl() + UPLOADVIO_MOBILE, POST, "param", vio.toString());
            return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新的获取法律文书方法
     *
     * @param yhbh
     * @param wslx
     * @return
     */
    public WebQueryResult<List<THmb>> hqVioWs(String yhbh, String wslx) {
        WebQueryResult<String> re = httpTextClient(getUrl() + GAIN_VIO_BILL, POST, "yhbh", yhbh, "wslx", wslx);
        return GlobalMethod.webXmlStrToListObj(re, THmb.class);
    }

    /**
     * 大于服务器的文件，同步文书
     *
     * @param hmb
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> synVioWs(THmb hmb, String jybh) {
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + SYN_VIO_BILL, POST,
                        "hdid", hmb.getHdid(), "dqz", hmb.getDqhm(), "yhbh", jybh),
                ZapcReturn.class
        );
    }

    /**
     * 新的文书上交方法
     *
     * @param hmb
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> backVioWs(THmb hmb, String jybh) {
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + BACK_VIO_BILL, POST,
                        "hdid", hmb.getHdid(), "dqz", hmb.getDqhm(), "yhbh", jybh),
                // uploadAction(postParams, BACK_VIO_BILL),
                ZapcReturn.class
        );

    }

    /**
     * 查询民警工作量的方法
     *
     * @param jybh
     * @param stime
     * @param etime
     * @return
     */
    public WebQueryResult<MjJobBean> queryMjJob(String jybh, String stime,
                                                String etime) {
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + QUERY_MJ_JOB, POST, "jybh", jybh, "stime", stime, "etime", etime),
                MjJobBean.class);
    }

    /**
     * 上传事故文本信息，由返回的数据写记录号
     *
     * @param acd
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdRecode(AcdPhotoBean acd) {
        Map<String, String> postParams = new HashMap<>();
        String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
        postParams.put("zqmj", jh.length() == 8 ? jh.substring(2) : jh);
        postParams.put("sgbh", acd.getSgbh());
        postParams.put("sgsj", acd.getSgsj());
        postParams.put("sgdd", acd.getSgdd());
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + UPLOAD_ACD_RECODE, POST,
                        postParams), ZapcReturn.class
        );
    }

    /**
     * 上传GPS信息
     *
     * @param b
     * @return
     */
    public WebQueryResult<String> uploadGpsInfo(byte[] b) {
        String jwtUpUrl = getUrl() + GPS_UPLOAD;
        WebQueryResult<String> sre = uploadByte(jwtUpUrl, b, null, 0, null);
        return sre;
    }

    /**
     * 上传事故照片
     *
     * @param file
     * @param xtbh
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadAcdPhoto(File file, long xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        long fileLen = file.length();
        String jwtUpUrl = getUrl() + UPLOAD_ACD_PHOTO;
        Log.e("uploadAcdPhoto", url);
        byte[] data = TypeCenvert.long2Byte(xtbh);
        try {
            FileInputStream in = new FileInputStream(file);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, null);
            in.close();
            if (sre.getStatus() == HttpURLConnection.HTTP_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpURLConnection.HTTP_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public String uploadFxcPhotoAndMd5(File file, String md5) {
        String result = "";
        if (!file.exists())
            return "";
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO_MD5 + "?md5=" + md5.toLowerCase();
        Log.e("uploadFxcPhotoAndMd5", jwtUpUrl);
        try {
            URL nurl = new URL(jwtUpUrl);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                result += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("uploadFxcPhotoAndMd5", "图片数据返回：" + result);
        return result;
    }

    public WebQueryResult<String> uploadFxczfJlAndImage(VioFxczfBean fxc, List<String> imageInfo) {
        String ph = "";
        for (String s : imageInfo)
            ph += s + ",";
        ph = ph.substring(0, ph.length() - 1);
        fxc.setCwms(ph);
        try {
            String xml = CommParserXml.objToXml(fxc);
            WebQueryResult<String> re = httpTextClient(getUrl() + UPLOAD_FXC_JL_IMG,
                    POST, "fxcjl", xml);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传违章决定书照片
     *
     * @param vio
     * @return
     */
    public WebQueryResult<String> uploadVioPic(VioViolation vio) {
        String url = getUrl() + UPLOADVIO_PIC + "?jdsbh=" + vio.getJdsbh() + "&wslb=" + vio.getWslb();
        File pic = new File(vio.getPicFile());
        return uploadFile(url, pic);
    }

    public boolean checkFxcTzsh(String tzsh) {
        String url = getUrl() + CHECK_FXC_TZSH + "?tzsh=" + tzsh;
        WebQueryResult<String> json = httpTextClient(url, GET);
        try {
            JSONObject obj = new JSONObject(json.getResult());
            if (TextUtils.equals(obj.optString("re"), "1"))
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public WebQueryResult<ZapcReturn> uploadFxczfJl(VioFxczfBean fxc) {
        try {
            String xml = CommParserXml.objToXml(fxc);
            WebQueryResult<String> r = httpTextClient(getUrl() + UPLOAD_FXC_JL,
                    POST, "fxcjl", xml);
            // uploadAction(postParams, UPLOADREP);
            return GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uploadFxcPhoto(File file, Long xtbh) {
        String result = "";
        if (!file.exists())
            return "";
        String md5 = ZipUtils.getFileMd5(file).toLowerCase();
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO + "?fxcId=" + xtbh + "&md5=" + md5;
        try {
            URL nurl = new URL(jwtUpUrl);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                result += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public WebQueryResult<ZapcReturn> uploadFxcPhotoOld(File file, Long xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        long fileLen = file.length();
        String jwtUpUrl = getUrl() + UPLOAD_FXC_PHOTO_CHECK;
        byte[] data = TypeCenvert.long2Byte(xtbh);
        data = TypeCenvert.addByte(data, TypeCenvert.long2Byte(fileLen));
        try {
            FileInputStream in = new FileInputStream(file);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, null);
            in.close();
            if (sre.getStatus() == HttpURLConnection.HTTP_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpURLConnection.HTTP_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }


    /**
     * 简易处罚中专用的查询驾驶员简项信息
     *
     * @param dabh    档案编号
     * @param islocal 是否为本地，无用的参数
     * @return
     */
    public WebQueryResult<VioDrvBean> queryVioDrv(String dabh, String islocal) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("dabh", dabh);
        postParams.put("islocal", islocal);
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        postParams.put("jybh", jh);
        postParams.put("meid", GlobalData.serialNumber);
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + QUERY_VIO_DRV, POST, postParams),
                VioDrvBean.class);
    }

    /**
     * 简易处罚中专用的查询驾驶员信息，包括比对信息
     *
     * @param dabh 档案编号
     * @param sfzh 身份证号
     * @return
     */
    public WebQueryResult<String> jycxQueryDrv(String dabh, String sfzh) {
        String url = getUrl() + JYCX_QUERY_DRV + "?dabh=" + dabh + "&sfzh=" + sfzh;
        WebQueryResult<String> json = httpTextClient(url, GET);
        return json;
    }

    /**
     * 简易程序查询机动车信息包括比对信息
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<String> jycxQueryVeh(String hpzl, String hphm) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("hpzl", hpzl);
        postParams.put("hphm", hphm);
        return httpTextClient(getUrl() + JYCX_QUERY_VEH, POST, postParams);
    }

    /**
     * 简易处罚中用查询机动车简易项信息
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<VioVehBean> queryVioVeh(String hpzl, String hphm) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("hpzl", hpzl);
        postParams.put("hphm", hphm);
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        postParams.put("jybh", jh);
        postParams.put("meid", GlobalData.serialNumber);
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + QUERY_VIO_VEH, POST, postParams),
                VioVehBean.class);
    }


    /**
     * 获取所有关城门地点列表
     *
     * @return
     */
    public WebQueryResult<List<GcmBbddBean>> getAllGcmDd() {
        WebQueryResult<String> re = httpTextClient(getUrl() + GAT_ALL_GCM_DD,
                GET);
        return GlobalMethod.webXmlStrToListObj(re, GcmBbddBean.class);
    }

    /**
     * 上传关城门信息
     *
     * @param info
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadGcmBb(GcmBbInfoBean info) {
        Map<String, String> postParams = new HashMap<>();
        try {
            String xml = CommParserXml.objToXml(info);
            postParams.put("bbInfo", xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + UPLOAD_GCM_INFO, POST, postParams),
                ZapcReturn.class);
    }


    /**
     * 获取未缴款的决定书信息
     *
     * @param sfzh
     * @return
     */
    public WebQueryResult<List<JdsUnjkPrintBean>> getUnJkJds(String sfzh,
                                                             String hpzl, String hphm) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("sfzh", sfzh);
        postParams.put("hpzl", hpzl);
        postParams.put("hphm", hphm);
        WebQueryResult<String> re = httpTextClient(getUrl() + GET_UN_JK_JDS,
                POST, postParams);
        return GlobalMethod.webXmlStrToListObj(re, JdsUnjkPrintBean.class);
    }

    /**
     * 是否可以打开补打印决定书模块
     *
     * @param jybh
     * @return
     */
    public WebQueryResult<String> isOpenUnjk(String jybh) {
        WebQueryResult<String> re = httpTextClient(getUrl() + IS_OPEN_UNJK,
                POST, "jybh", jybh);
        return re;
    }

    /**
     * 上传报修的图片
     *
     * @param xtbh
     * @param path
     * @param handler
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadRepPic(long xtbh, String path,
                                                   Handler handler) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        re.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        File f = new File(path);
        long fileLen = f.length();
        String jwtUpUrl = getUrl() + UPLOAD_REP_PIC;
        byte[] data = TypeCenvert.long2Byte(xtbh);
        try {
            FileInputStream in = new FileInputStream(f);
            WebQueryResult<String> sre = uploadByte(jwtUpUrl, data, in,
                    fileLen, handler);
            in.close();
            if (sre.getStatus() == HttpURLConnection.HTTP_OK
                    && !TextUtils.isEmpty(sre.getResult())) {
                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
                        ZapcReturn.class);
                re.setResult(g);
                re.setStatus(HttpURLConnection.HTTP_OK);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 验证六分审批权限
     *
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> checkSixSp(String jybh) {
        String url = getUrl() + CHECK_SIX_SP;
        WebQueryResult<String> re = httpTextClient(url, POST, "jybh", jybh);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);
    }

    /**
     * 六分审批主方法
     *
     * @param jdsbh
     * @param sp
     * @param spnr
     * @param jybh
     * @return
     */
    public WebQueryResult<ZapcReturn> submitSixSp(String jdsbh, String sp,
                                                  String spnr, String jybh) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("jdsbh", jdsbh);
        postParams.put("sp", sp);
        postParams.put("spnr", spnr);
        postParams.put("jybh", jybh);
        String url = getUrl() + SUBMIT_SIX_SP;
        WebQueryResult<String> re = httpTextClient(url, POST, postParams);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);

    }

    /**
     * 获取该单位的审批列表
     *
     * @param dwdm
     * @param sp   审批状态 9为未审批，8为审批否定，5为审批合格
     * @return
     */
    public WebQueryResult<List<TTViolation>> querySixSpList(String dwdm,
                                                            String sp) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("dwdm", dwdm);
        postParams.put("sp", sp);
        String url = getUrl() + QUERY_SIX_SP;
        WebQueryResult<String> re = httpTextClient(url, POST, postParams);
        return GlobalMethod.webXmlStrToListObj(re, TTViolation.class);
    }

    /**
     * 警务通用户登出
     *
     * @param yhbh
     * @param sbid
     * @return
     */
    public WebQueryResult<String> logoutJwt(String yhbh, String sbid) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("yhbh", yhbh);
        postParams.put("sbid", sbid);
        String url = getUrl() + LOGOUT_JWT;
        WebQueryResult<String> re = httpTextClient(url, POST, postParams);
        return re;
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    public Date getSystemTime() {
        WebQueryResult<String> re = httpTextClient(getUrl() + GET_SYSTEM_TIME,
                GET);
        if (re.getStatus() == HttpURLConnection.HTTP_OK
                && !TextUtils.isEmpty(re.getResult())) {
            Long l = Long.valueOf(re.getResult());
            Date d = new Date(l);
            return d;
        }
        return null;
    }

    /**
     * 检查决定书在服务器端是否存在重复
     *
     * @param wslb
     * @param jdsbh
     * @return
     */
    public WebQueryResult<String> isDupVio(String wslb, String jdsbh) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("wslb", wslb);
        postParams.put("jdsbh", jdsbh);
        String url = getUrl() + IS_DUP_VIO;
        WebQueryResult<String> re = httpTextClient(url, POST, postParams);
        return re;
    }

    /**
     * 查询决定书入库情况
     *
     * @param jdsbh
     * @return
     */
    public WebQueryResult<ZapcReturn> queryVioRkqk(String jdsbh, String wslb) {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("jdsbh", jdsbh);
        postParams.put("wslb", wslb);
        String url = getUrl() + QUEYR_VIO_RKQK;
        WebQueryResult<String> re = httpTextClient(url, POST, postParams);
        return GlobalMethod.webXmlStrToObj(re, ZapcReturn.class);
    }

    /**
     * 上传客车登记
     *
     * @param kcdj
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadSpringKcdj(SpringKcdjBean kcdj) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(kcdj);
            Map<String, String> postParams = new HashMap<>();
            postParams.put("kcdj", xml);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + UPLOAD_SPRING_KCDJ,
                            POST, postParams), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传危化品登记
     *
     * @param whpdj
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadSpringWhpdj(SpringWhpdjBean whpdj) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(whpdj);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + UPLOAD_SPRING_WHPDJ,
                            POST, "whpdj", xml), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询重型车登记情况
     *
     * @param hpzl
     * @param hphm
     * @return
     */
    public WebQueryResult<TruckCheckBean> queryTruckCheck(String hpzl,
                                                          String hphm) {
        WebQueryResult<TruckCheckBean> re = null;
        Map<String, String> postParams = new HashMap<>();
        postParams.put("hpzl", hpzl);
        postParams.put("hphm", hphm);
        re = GlobalMethod.webXmlStrToObj(
                httpTextClient(getUrl() + QUERY_TRUCK_CHECK, POST, postParams),
                TruckCheckBean.class);
        return re;
    }

    /**
     * 上传重型车登记
     *
     * @param truck
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadTruckVeh(TruckVehicleBean truck) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(truck);
            Log.e("uploadTruckVeh", xml);
            Map<String, String> postParams = new HashMap<>();
            postParams.put("truck", xml);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + UPLOAD_TRUCK_VEH, POST,
                            postParams), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 上传重型车驾驶员信息
     *
     * @param drv
     * @return
     */
    public WebQueryResult<ZapcReturn> uploadTruckDrv(TruckDriverBean drv) {
        WebQueryResult<ZapcReturn> re = null;// new
        try {
            String xml = CommParserXml.objToXml(drv);
            Log.e("uploadTruckDrv", xml);
            Map<String, String> postParams = new HashMap<>();
            postParams.put("drv", xml);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + UPLOAD_TRUCK_DRV, POST,
                            postParams), ZapcReturn.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 获取所有车辆运输单位名称
     *
     * @return
     */
    public WebQueryResult<List<KeyValueBean>> queryAllQymc() {
        WebQueryResult<String> re = httpTextClient(getUrl() + QUERY_ALL_QYMC,
                POST);
        return GlobalMethod.webXmlStrToListObj(re, KeyValueBean.class);
    }

    public WebQueryResult<KeyValueBean> getBjbdBySfzh(String sfzh) {
        WebQueryResult<String> re = httpTextClient(getUrl() + ZHBD + "?sfzh=" + sfzh.toUpperCase(),
                GET);
        WebQueryResult<KeyValueBean> kv = new WebQueryResult<KeyValueBean>();
        kv.setStatus(re.getStatus());
        try {
            JSONObject obj = new JSONObject(re.getResult());
            KeyValueBean k = new KeyValueBean();
            k.setKey(obj.getString("key"));
            k.setValue(obj.getString("value"));
            kv.setResult(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kv;
    }

    /**
     * 获取所有违法行为和车辆类型的逻辑关系
     *
     * @return
     */
    public WebQueryResult<String> getAllWfxwCllxCheck() {
        return httpTextClient(getUrl() + GET_ALL_WFXW_CLLX, GET);
    }

    /**
     * 验证用户的各种MD5值
     *
     * @param jybh    八位警号
     * @param meid    手机串号
     * @param md5
     * @param catalog 分类
     * @return
     */
    public WebQueryResult<String> checkUserMd5(String jybh, String meid,
                                               String md5, String catalog) {
        WebQueryResult<String> re = null;// new
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("jybh", jybh);
            postParams.put("meid", meid);
            postParams.put("md5", md5);
            postParams.put("catalog", catalog);
            re = httpTextClient(getUrl() + CHECK_USER_MD5, POST, postParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 查询非现场入库情况
     *
     * @param xtbh
     * @return
     */
    public WebQueryResult<String> queryFxcRkqk(String xtbh) {
        WebQueryResult<String> re = null;
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("xtbh", xtbh);
            re = httpTextClient(getUrl() + QUERY_FXC_RKQK, POST, postParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public WebQueryResult<ZapcReturn> checkFxcIsUpOK(String xtbh) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("xtbh", xtbh);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + CHECK_FXC_ALL_OK, POST,
                            postParams), ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 验证该民警的非现场是否均已上传完整
     *
     * @param zqmj
     * @return
     */
    public WebQueryResult<ZapcReturn> checkFxcZqmjAllUpload(String zqmj) {
        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("zqmj", zqmj);
            re = GlobalMethod.webXmlStrToObj(
                    httpTextClient(getUrl() + CHECK_FXC_ZQMJ_ALL_UPLOAD, POST,
                            postParams), ZapcReturn.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public WebQueryResult<SchoolZtzBean> querySchool(String pzh) {
        WebQueryResult<SchoolZtzBean> re = new WebQueryResult<SchoolZtzBean>();
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("pzh", pzh);
            WebQueryResult<String> s = httpTextClient(getUrl() + QUERY_SCHOOL, POST, postParams);
            re = GlobalMethod.webXmlStrToObj(s, SchoolZtzBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public void queryForbidById(String id) {

    }

    public static final String DOWN_DICT_URL = "/ydjw/services/ydjw/getFrmCode";
    public static final String DOWN_WFXW_URL = "/ydjw/services/ydjw/getVioWfdmCode";
    public static final String DOWN_ROAD_ITEM_URL = "/ydjw/services/ydjw/getFrmRoadItem";
    public static final String DOWN_ROAD_SEG_URL = "/ydjw/services/ydjw/getFrmRoadSeg";
    public static final String DOWN_SQL_VALUE_URL = "/ydjw/services/ydjw/queryOracle";

    public WebQueryResult<String> updateDictMap(String xtlb, String dmlb) {
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("xtlb", xtlb);
            postParams.put("dmlb", dmlb);
            WebQueryResult<String> s = httpTextClient(getUrl() + DOWN_DICT_URL, POST, postParams);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebQueryResult<String> updateOtherDict(String lb, String version) {
        String url = DOWN_WFXW_URL;
        if ("road_item".equals(lb))
            url = DOWN_ROAD_ITEM_URL;
        else if ("road_seg".equals(lb))
            url = DOWN_ROAD_SEG_URL;
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("version", version);
            WebQueryResult<String> s = httpTextClient(getUrl() + url, POST, postParams);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebQueryResult<String> downloadSqlValue(String sql, String params) {
        try {
            Map<String, String> postParams = new HashMap<>();
            postParams.put("sql", sql);
            if (!TextUtils.isEmpty(params))
                postParams.put("params", params);
            WebQueryResult<String> s = httpTextClient(getUrl() + DOWN_SQL_VALUE_URL, POST, postParams);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //------------------------------------------登录文件更新---------------------------------------------

    protected String url = "ydjw/services/login/updateFileVersion";


    public WebQueryResult<String> checkUserAndUpdate(String yhbh, String mm, String sbid) {
        String url = getUrl() + checkUrl;
        Log.e("login url", url);
        Log.e("login url", yhbh + "," + mm + "," + sbid);
        Map<String, String> postParams = new HashMap<>();
        postParams.put("yhbh", yhbh);
        postParams.put("sbid", sbid);
        postParams.put("mm", mm);
        return httpTextClient(url, POST, postParams);
    }

    /**
     * 最近的验证、更新、警员信息一次请求返回的方法
     *
     * @param yhbh
     * @param mm
     * @param sbid
     * @return
     */
    public WebQueryResult<String> checkUserAndUpdate2222(String yhbh, String mm,
                                                         String sbid, boolean isCheckMd5) {
        String url = getUrl()
                + (isCheckMd5 ? checkMd5 : checkUrl);
        // writeDisk(request.getURI().toString());
        Map<String, String> postParams = new HashMap<>();
        postParams.put("yhbh", yhbh);
        postParams.put("sbid", sbid);
        postParams.put("mm", mm);
        return httpTextClient(url, POST, postParams);
    }

    // ------------------------以下是本类中公用的方法-----------------------------------

    public WebQueryResult<String> httpTextClient(String url, int method, String... param) {
        Map<String, String> params = new HashMap<>();
        if (param.length > 0 && param.length % 2 == 0) {
            for (int i = 0; i < param.length / 2; i++) {
                params.put(param[2 * i], param[2 * i + 1]);
            }
        }
        return httpTextClient(url, method, params);
    }

    public WebQueryResult<String> httpTextClient(String url, int method, Map<String, String> params) {
        String jybh = GlobalData.grxx == null ? "" : GlobalData.grxx.get(GlobalConstant.JH);
        String meid = GlobalData.serialNumber == null ? "" : GlobalData.serialNumber;
        WebQueryResult<String> web = new WebQueryResult<>();
        web.setStatus(400);
        OkHttpClient client = new OkHttpClient();
        Log.e("ResultDao", "url: " + url + ", method:" + method + "params: " + ((params == null) ? "0" : params.size()));
        Log.e("ResultDao", "jybh: " + jybh + ", meid:" + meid);
        GlobalMethod.logMap(params, "httpTextClient");
        Request.Builder reqb = new Request.Builder().url(url);
        if (!TextUtils.isEmpty(jybh))
            reqb = reqb.header("jybh", jybh);
        if (!TextUtils.isEmpty(meid))
            reqb = reqb.header("meid", meid);
        if (method == POST) {
            FormBody.Builder mb = new FormBody.Builder();
            if (params != null)
                for (Map.Entry<String, String> m : params.entrySet()) {
                    mb.add(m.getKey(), m.getValue());
                }
            reqb = reqb.post(mb.build());
        }
        Response response = null;
        try {
            response = client.newCall(reqb.build()).execute();
            int code = response.code();
            web.setStatus(code);
            Log.e("ResultDao", "code: " + code);
            if (response.isSuccessful()) {
                String s = response.body().string();
                Log.e("ResultDao", "return: " + s);
                web.setResult(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            web.setStMs(e.getMessage());
        }
        return web;
    }

    public WebQueryResult<String> uploadFile(String url, File file) {
        WebQueryResult<String> re = new WebQueryResult<String>(
                HttpURLConnection.HTTP_BAD_REQUEST);
        try {
            long length = file.length();
            long readLen = 0;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            URL nurl = new URL(url);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            if (in != null) {
                int len = 0;
                byte[] buffer = new byte[1024 * 8];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    readLen += len;
                }
            }
            out.flush();
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s = null;
            String result = "";
            while ((s = reader.readLine()) != null) {
                result += s;
            }
            if (!TextUtils.isEmpty(result)) {
                re.setResult(result);
                re.setStatus(HttpURLConnection.HTTP_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public WebQueryResult<String> uploadByte(String url, byte[] data,
                                             InputStream in, long inLength, Handler handler) {
        WebQueryResult<String> re = new WebQueryResult<String>(
                HttpURLConnection.HTTP_BAD_REQUEST);
        try {
            int readLen = 0;
            sendData(0, (int) inLength, 0, handler);
            URL nurl = new URL(url);
            URLConnection conn = nurl.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            if (data != null && data.length > 0)
                out.write(data);
            if (in != null) {
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    readLen += len;
                    sendData(0, (int) inLength, readLen, handler);
                }
                sendData(0, (int) inLength, (int) inLength, handler);
            }
            out.close();
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            String s = null;
            String result = "";
            while ((s = reader.readLine()) != null) {
                result += s;
            }
            Log.e("uploadByte", result);
            if (!TextUtils.isEmpty(result)) {
                re.setResult(result);
                re.setStatus(HttpURLConnection.HTTP_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendData(1, 0, 0, handler);
        }

        return re;
    }

    /**
     * 下载小文件的公共方法
     *
     * @param urlStr
     * @param dest
     * @return
     */
    public long downloadFile(String urlStr, File dest) {
        long count = 0;
        byte[] b = new byte[1024];
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK)
                return 0;
            InputStream is = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(dest);
            int len = -1;
            while ((len = is.read(b)) > 0) {
                out.write(b, 0, len);
                count += len;
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public long downloadFile(String urlStr, File dest, long fileSize, String fn) {
        long count = 0;
        byte[] b = new byte[1024];
        try {
            //String u = getUrl() + urlStr;
            Log.e("RestfulDao", "下载文件地址：" + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int code = conn.getResponseCode();
            Log.e("RestfulDao", "下载文件状态：" + code);
            if (code != HttpURLConnection.HTTP_OK)
                return 0;
            InputStream is = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(dest);
            Log.e("down file",dest.getAbsolutePath());
            int len = 0;
            while ((len = is.read(b)) > 0) {
                out.write(b, 0, len);
                count += len;
                int step = (int) (count * 100 / fileSize);
                //sendData(0, 0, step, handler);
                EventBus.getDefault().post(new DownApkEvent(false, step, fn));
            }
            out.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new DownApkEvent(false, 100, fn));
        return count;
    }


    public void sendData(int err, int what, int step, Handler mHandler) {
        if (mHandler == null)
            return;
        Message m = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("catalog", 0);
        b.putInt("length", what);
        b.putInt("step", step);
        b.putInt("err", err);
        m.setData(b);
        mHandler.sendMessage(m);
    }


    // public abstract WebQueryResult<String> restfulQuery(String url,
    // List<NameValuePair> params, int method);

    public abstract String getUrl();

    public abstract String getMqttUrl();

    public abstract String getClassName();

    public abstract String getPicUrl();

    public abstract String getFileUrl();

    public abstract String getJqtbFileUrl();

    public String getIconFileUrl() {
        return getUrl() + "/ydjw/DownloadIconFile";
    }


}