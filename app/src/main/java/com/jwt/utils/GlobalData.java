package com.jwt.utils;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.text.TextUtils;

import com.jwt.bean.KeyValueBean;
import com.jwt.dao.ZaPcdjDao;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmCode_;
import com.jwt.update.App;

import org.w3c.dom.Text;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


public class GlobalData {

    // 图片压缩比例
    //public static int picCompress = 60;
    // 保存决定书时验证驾驶员和机动车的方式,初始化为本地车和证
    //public static int drvCheckFs = 2;
    //public static int vehCheckFs = 2;

    public static String myTopic = "clgj.320601";

    public static boolean isBadger = true;

    // 是否已初始化加载数据
    public static boolean isInitLoadData = false;

    // 系统登录标记，用于心跳包的回传
    public static byte loginStatus = 1;
    // 是否上传GPS位置
    //public static boolean isGpsUpload = false;
    // 是否对非机动车身份证明进行严格证认
    //public static boolean isCheckFjdcSfzm = false;
    // 心跳包和GPS包上传频率，单位是分钟
    //public static int uploadFreq = 2;

    /**
     * 拍照后是否预览
     */
    //public static boolean isPreviewPhoto = false;

    public static ConnCata connCata = ConnCata.OUTSIDECONN;

    public static Map<DictName, ArrayList<KeyValueBean>> dictMap = new HashMap<>();

    public static ArrayList<KeyValueBean> ryflList = null;
    public static ArrayList<KeyValueBean> sfList = null;
    // public static ArrayList<KeyValueBean> chenshiList = null;

    public static ArrayList<KeyValueBean> hpzlList = null;
    public static ArrayList<KeyValueBean> jtfsList = null;
    public static ArrayList<KeyValueBean> jkfsList = null;
    public static ArrayList<KeyValueBean> jkbjList = null;
    public static ArrayList<KeyValueBean> hpqlList = null;
    // public static ArrayList<String> wfxwdmList = null;
    public static ArrayList<KeyValueBean> clflList = null;
    public static ArrayList<KeyValueBean> cfzlList = null;
    public static ArrayList<KeyValueBean> qzcslxList = null;
    public static ArrayList<KeyValueBean> sjxmList = null;
    public static ArrayList<KeyValueBean> zjcxList = null;
    public static ArrayList<KeyValueBean> clbjList = null;
    public static ArrayList<KeyValueBean> syxzList = null;
    public static ArrayList<KeyValueBean> zzmmList = null;
    public static ArrayList<KeyValueBean> zyxxList = null;
    public static ArrayList<KeyValueBean> xzqhList = null;
    public static ArrayList<KeyValueBean> zjlxList = null;


    // 公共字典数据
    public static ArrayList<KeyValueBean> xbList = null;

    // 事故处理字典表
    public static ArrayList<KeyValueBean> acdJszzlList = null;
    public static ArrayList<KeyValueBean> acdRylxList = null;
    public static ArrayList<KeyValueBean> acdSgzrList = null;
    public static ArrayList<KeyValueBean> acdJtfsList = null;
    public static ArrayList<KeyValueBean> acdWfxwList = null;
    public static ArrayList<KeyValueBean> arrayAcdTq = null;
    public static ArrayList<KeyValueBean> arrayAcdSgxt = null;
    public static ArrayList<KeyValueBean> arrayAcdCjpz = null;
    public static ArrayList<KeyValueBean> arrayAcdDcpz = null;
    public static ArrayList<KeyValueBean> arrayAcdTjfs = null;
    public static ArrayList<KeyValueBean> arrayAcdJafs = null;

    // 工作日志中警务状态的列表
    public static ArrayList<KeyValueBean> qwztList = null;

    //集成平台中报警种类字典表
    public static ArrayList<KeyValueBean> bjzlList = new ArrayList<>();
    //南通市管理部门六位数字典表
    public static ArrayList<KeyValueBean> glbmList = new ArrayList<>();

    public static Map<String, String> grxx = null;

    // public static String visitorXML;
    /**
     * 手机串号
     */
    public static String serialNumber;
    public static String photoName;
    public static int CAMER_REQUEST = 11111;

    public static ArrayList<KeyValueBean> getDictMap(DictName d) {
        ArrayList<KeyValueBean> map = dictMap.get(d);
        if (map == null)
            map = new ArrayList<>();
        return map;
    }

    private static List<FrmCode> getData(Box<FrmCode> box, String codes) {
        String[] cs = codes.split("/");
        QueryBuilder<FrmCode> builder = box.query();
        builder.equal(FrmCode_.xtlb, cs[0])
                .equal(FrmCode_.dmlb, cs[1]);
        List<FrmCode> zd = builder.build().find();
        return zd;
    }


    public static int initGlobalData(BoxStore boxStore) {
        Box<FrmCode> box = boxStore.boxFor(FrmCode.class);
        int count = 0;

//        // 加载治安盘查数据
//        ZaPcdjDao.initZapcData(resolver);
//
        // 个人信息应在程序初始化时从服务器处获取,然后存入数据库
//
//        grxx = ViolationDAO.getMjgrxx(resolver);
//
        clbjList = new ArrayList<KeyValueBean>();
        clbjList.add(new KeyValueBean("0", "未处理"));
        clbjList.add(new KeyValueBean("1", "已处理"));

        ryflList = loadData(getData(box, GlobalConstant.RYFL), true);
        for (int i = ryflList.size() - 1; i >= 0; i--) {
            KeyValueBean kv = ryflList.get(i);
            String cond = "8,9";
            if (cond.indexOf(kv.getKey()) >= 0) {
                ryflList.remove(i);
            }
        }
        count += ryflList.size();
        sfList = loadData(getData(box, GlobalConstant.SHENFEN), false);
        hpzlList = loadData(getData(box, GlobalConstant.HPZL), true);
        jtfsList = loadData(getData(box, GlobalConstant.JTFS), true);
        jkbjList = loadData(getData(box, GlobalConstant.JKBJ), true);
        jkfsList = loadData(getData(box, GlobalConstant.JKFS), true);
        count += jkfsList.size();
        for (int i = jkfsList.size() - 1; i >= 0; i--) {
            KeyValueBean kv = jkfsList.get(i);
            if (Integer.valueOf(kv.getKey()) >= 3) {
                jkfsList.remove(i);
            }
        }
        hpqlList = loadData(getData(box, GlobalConstant.HPQL), true);
        clflList = loadData(getData(box, GlobalConstant.CLFL), true);
        cfzlList = loadData(getData(box, GlobalConstant.CFZL), true);
        for (int i = cfzlList.size() - 1; i >= 0; i--) {
            KeyValueBean kv = cfzlList.get(i);
            if (Integer.valueOf(kv.getKey()) >= 3) {
                cfzlList.remove(i);
            }
        }
        qzcslxList = loadData(getData(box, GlobalConstant.QZCSLX), true);
        zjcxList = loadData(getData(box, GlobalConstant.ZJCX), true);
        xbList = loadData(getData(box, GlobalConstant.FRM_XB), true);
        syxzList = loadData(getData(box, GlobalConstant.SYXZ), true);
        zzmmList = loadData(getData(box, GlobalConstant.ZZMM), true);
        zyxxList = loadData(getData(box, GlobalConstant.ZYXX), true);
        xzqhList = loadData(getData(box, GlobalConstant.XZQH), true);
        zjlxList = loadData(getData(box, GlobalConstant.ZJLX), true);
        acdJszzlList = loadData(getData(box, GlobalConstant.ACD_JSZZL), true);
        acdRylxList = loadData(getData(box, GlobalConstant.ACD_RYLX), true);
        acdSgzrList = loadData(getData(box, GlobalConstant.ACD_SGZR), true);
        acdJtfsList = loadData(getData(box, GlobalConstant.ACD_JTFS), true);
        acdWfxwList = loadData(getData(box, GlobalConstant.ACD_WFXW), true);
        arrayAcdTq = loadData(getData(box, GlobalConstant.ACD_TQ), true);
        arrayAcdSgxt = loadData(getData(box, GlobalConstant.ACD_SGXT), true);
        arrayAcdCjpz = loadData(getData(box, GlobalConstant.ACD_CLJPZ), true);
        arrayAcdDcpz = loadData(getData(box, GlobalConstant.ACD_DLPZ), true);
        arrayAcdTjfs = loadData(getData(box, GlobalConstant.ACD_TJFS), true);
        arrayAcdJafs = loadData(getData(box, GlobalConstant.ACD_JAFS), true);
        // 收缴项目列表,和系统不同,在前面加1
        sjxmList = loadData(getData(box, GlobalConstant.SJXM), true);
        for (KeyValueBean kv : sjxmList) {
            kv.setKey("1" + kv.getKey());
        }
        //加载大平台字典库
        ZaPcdjDao.initZapcData(boxStore);
        //加载预定报警
        String bmbh = GlobalData.grxx.get(GlobalConstant.YBMBH);
        if (!TextUtils.isEmpty(bmbh) && bmbh.length() > 6)
            bmbh = bmbh.substring(0, 6);
        myTopic = "clgj." + bmbh;
        //加载管理部门
        if (glbmList == null)
            glbmList = new ArrayList<>();
        glbmList.clear();
        for (Map.Entry<String, String> entry : GlobalConstant.fxjgMap.entrySet()) {
            glbmList.add(new KeyValueBean(entry.getKey(), entry.getValue()));
        }
        Collections.sort(glbmList, new Comparator<KeyValueBean>() {
            @Override
            public int compare(KeyValueBean o1, KeyValueBean o2) {
                return Integer.valueOf(o1.getKey())-Integer.valueOf(o2.getKey());
            }
        });
        //加载报警种类
        if (bjzlList == null)
            bjzlList = new ArrayList<>();
        bjzlList.clear();
        bjzlList.add(new KeyValueBean("1", "车辆所有人驾驶证异常"));
        bjzlList.add(new KeyValueBean("2", "逾期未年检车辆"));
        bjzlList.add(new KeyValueBean("3", "重点关注大客车"));
        //
        isInitLoadData = true;
        return count;
    }

    private static ArrayList<KeyValueBean> loadData(List<FrmCode> data, boolean isFirst) {
        ArrayList<KeyValueBean> list = new ArrayList<>();
        for (FrmCode code : data) {
            KeyValueBean kv = new KeyValueBean();
            kv.setKey(code.getDmz());
            kv.setValue(isFirst ? code.getDmsm1() : code.getDmsm2());
            list.add(kv);
        }
        return list;
    }


}
