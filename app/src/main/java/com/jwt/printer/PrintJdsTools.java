package com.jwt.printer;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.jwt.bean.WfxwBzz;
import com.jwt.dao.ViolationDAO;
import com.jwt.dao.WfdmDao;
import com.jwt.jbyw.JdsUnjkPrintBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

public class PrintJdsTools {

    // public static final int LEFT = 0;
    // public static final int CENTER = 1;
    // public static final int RIGHT = 2;

    private static SimpleDateFormat sdfFull = new SimpleDateFormat(
            "yyyy年MM月dd日HH时mm分");
    private static SimpleDateFormat sdfShort = new SimpleDateFormat(
            "yyyy年MM月dd日");
    private static SimpleDateFormat sdfNoSec = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");

    private static String TAG = "PrintJdsTools";

    public static ArrayList<JdsPrintBean> getPrintJdsByBh(String bh,
                                                          Activity resolver) {
        VioViolation v = ViolationDAO.getViolationByJdsbh(bh, GlobalMethod.getBoxStore(resolver));
        if (v == null)
            return null;
        return getPrintJdsContent(v, resolver);
    }

    /**
     * 用于预览打印
     *
     * @param violation
     * @param resolver
     * @return
     */
    public static String getPreviewJds(VioViolation violation,
                                       Activity resolver) {
        ArrayList<JdsPrintBean> jds = getPrintJdsContent(violation, resolver);
        StringBuilder sb = new StringBuilder();
        for (JdsPrintBean j : jds) {
            String c = j.getContent().replaceAll("", "&nbsp;");
            sb.append(c).append("<br>");
        }
        return sb.toString();
    }

    /**
     * 打印违停告知单
     *
     * @param fxc
     * @return
     */
    public static ArrayList<JdsPrintBean> getPrintFxczfContent(
            VioFxczfBean fxc) {
        //测试一下数据
        Set<Entry<String, String>> set = GlobalData.grxx.entrySet();
        for (Entry<String, String> e : set) {
            Log.e(TAG, e.getKey() + ":" + e.getValue());
        }
        if (fxc == null)
            return null;
        ArrayList<JdsPrintBean> jds = new ArrayList<JdsPrintBean>();
        String fxjgqc = GlobalData.grxx.get(GlobalConstant.BMMC);
        String dwdz = TextUtils.isEmpty(GlobalData.grxx.get(GlobalConstant.DWDZ)) ?
                GlobalData.grxx.get(GlobalConstant.CLJG1)
                : GlobalData.grxx.get(GlobalConstant.DWDZ);
        String hpzl = GlobalMethod.getStringFromKVListByKey(
                GlobalData.hpzlList, fxc.getHpzl());
        String mjxm = GlobalData.grxx.get(GlobalConstant.XM);
        String wfsj = "";
        String cfrq = "";
        String wfxw = fxc.getWfxw();
        boolean isSerious = TextUtils.equals("13446", wfxw);
        try {
            Date d = sdfNoSec.parse(fxc.getWfsj());
            wfsj = sdfFull.format(d);
            cfrq = sdfShort.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        int jIndex = fxjgqc.indexOf("局") + 1;
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(0, jIndex)));
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(jIndex)));
        if (isSerious)
            jds.add(new JdsPrintBean(Gravity.CENTER, "违反禁令标志停车告知单"));
        else
            jds.add(new JdsPrintBean(Gravity.CENTER, "违法停车告知单"));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "编号: " + fxc.getTzsh()));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.LEFT, "号牌种类:" + hpzl));
        jds.add(new JdsPrintBean(Gravity.LEFT, "车辆牌号:" + fxc.getHphm()));
        jds.add(new JdsPrintBean(Gravity.LEFT, "违法停车时间:" + wfsj));
        jds.add(new JdsPrintBean(Gravity.LEFT, "违法代码:" + wfxw));
        jds.add(new JdsPrintBean(Gravity.LEFT, "违法停车地点:" + fxc.getWfdz()));
        if (isSerious) {
            jds.add(new JdsPrintBean(Gravity.LEFT,
                    "    该机动车在上述时间、地点违反禁令标示停车，违反了《道路交通安全法》第三十八条、"
                            + "《江苏省道路交通安全条例》第五十七条第一项规定，请驾驶人于三日后十五日内持本告知单，到" + fxjgqc
                            + "接受处理。"
            ));
        } else {
            jds.add(new JdsPrintBean(Gravity.LEFT,
                    "    该机动车在上述时间、地点停放，违反了《道路交通安全法》第五十六条、"
                            + "《江苏省道路交通安全条例》第四十三条的规定，请于三日后十五日内持本告知单，到" + fxjgqc
                            + "接受处理。"
            ));
        }
        if (!TextUtils.isEmpty(dwdz))
            jds.add(new JdsPrintBean(Gravity.LEFT, "    处理地点：" + dwdz));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "交通警察: " + mjxm));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.RIGHT, cfrq));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.LEFT, "备注：机动车所有人登记的住所地址或者联系电话发生变化的，"
                + "请及时向登记地车辆管理所申请变更备案。"));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        return jds;
    }

    public static ArrayList<JdsPrintBean> getPrintJdsContent(VioViolation v,
                                                             Activity resolver) {
        JSONObject vioObj = ParserJson.objToJson(v);
        Log.e(TAG, vioObj.toString());
        ArrayList<JdsPrintBean> jds = new ArrayList<JdsPrintBean>();
        if (v == null)
            return null;
        // 公共变量初始化
        int ryfl = Integer.valueOf(v.getRyfl());
        // 决定书编号
        String jszOrSfz = "居民身份证号码：";
        if (ryfl == 4)
            jszOrSfz = "机动车驾驶证号码：";
        StringBuilder jdsbh = new StringBuilder(v.getJdsbh());
        int jyw = Integer.valueOf(jdsbh.substring(6)) % 7;
        jdsbh.insert(6, " ").append(jyw);

        String wfdd = v.getWfdz();
        String jtfs = GlobalMethod.getStringFromKVListByKey(
                GlobalData.jtfsList, v.getJtfs());
        String gzxm = v.getGzxm();
        String[] temp = ViolationDAO.getJsonStrs(gzxm, "zzmm", "zyxx");
        String zzmm = GlobalMethod.getStringFromKVListByKey(GlobalData.zzmmList, temp[0]);
        String zyxx = GlobalMethod.getStringFromKVListByKey(GlobalData.zyxxList, temp[1]);
        WfxwBzz[] wfxws = null;
        try {
            wfxws = ParserJson.parseJsonToArray(v.getWfxw(), WfxwBzz.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VioWfdmCode[] codes = new VioWfdmCode[wfxws.length];
        for (int i = 0; i < wfxws.length; i++) {
            codes[i] = WfdmDao.queryWfxwByWfdm(wfxws[i].getWfxw(), GlobalMethod.getBoxStore(resolver));
        }
        if (GlobalData.grxx == null)
            GlobalData.grxx = GlobalMethod.getSavedMjInfo(resolver);
        String wfsj = "";
        String cfrq = "";
        String fzjg_ch = v.getFzjg();
        if (ryfl == 4)
            fzjg_ch = ViolationDAO.getFzjgChinaName(v.getFzjg(), GlobalMethod.getBoxStore(resolver));
        String jkyh = GlobalData.grxx.get(GlobalConstant.JKYH);
        String fyjg = GlobalData.grxx.get(GlobalConstant.FYJG);
        String ssjg = GlobalData.grxx.get(GlobalConstant.SSJG);
        String mjxm = GlobalData.grxx.get(GlobalConstant.XM);
        String fxjgqc = GlobalData.grxx.get(GlobalConstant.BMMC);
        String qzcslx = WfdmDao.getQzcslxMs(v.getQzcslx());
        try {
            wfsj = sdfFull.format(sdfNoSec.parse(v.getWfsj()));
            cfrq = sdfShort.format(sdfNoSec.parse(v.getClsj()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 所有的文书都有标题
        int jIndex = fxjgqc.indexOf("局") + 1;
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(0, jIndex)));
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(jIndex)));

        // 简易程序处罚或是警告
        if (Integer.valueOf(v.getWslb()) == 1) {
            jds.add(new JdsPrintBean(Gravity.CENTER, "公安交通管理简易程序处罚决定书"));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "编号: " + jdsbh));
            jds.add(new JdsPrintBean(Gravity.LEFT, "被处罚人:" + v.getDsr()));
            jds.add(new JdsPrintBean(Gravity.LEFT, jszOrSfz + v.getJszh()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证档案编号:" + v.getDabh()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "发证机关:" + fzjg_ch));
            jds.add(new JdsPrintBean(Gravity.LEFT, "准驾车型:" + v.getZjcx()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "电话:" + v.getDh()));
            if (!TextUtils.isEmpty(zzmm))
                jds.add(new JdsPrintBean(Gravity.LEFT, "政治面貌:" + zzmm));
            if (!TextUtils.isEmpty(zyxx))
                jds.add(new JdsPrintBean(Gravity.LEFT, "职业:" + zyxx));
            jds.add(new JdsPrintBean(Gravity.LEFT, "车辆类型:" + jtfs));
            jds.add(new JdsPrintBean(Gravity.LEFT, "车辆牌号:" + v.getHphm()));

            String bzzScz = "";
            if (GlobalMethod.isInBzzScz(wfxws[0].getWfxw())) {
                bzzScz = "标准值：" + wfxws[0].getBzz() + "，实测值：" + wfxws[0].getScz();
            }
            jds.add(new JdsPrintBean(Gravity.LEFT, "    被处罚人于" + wfsj + "，在"
                    + wfdd + "实施" + codes[0].getWfms() + "违法行为(代码"
                    + codes[0].getWfxw() + bzzScz + ")。"));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    根据" + codes[0].getFltw()
                    + "之规定，决定处以："));
            // 区别警告\不罚款\当场缴款
            if (TextUtils.equals(v.getCfzl(), "2")) {
                jds.add(new JdsPrintBean(Gravity.LEFT, "    罚款" + v.getFkje()
                        + "元"));
                // 当场交款
                if (TextUtils.equals(v.getJkfs(), "1")) {
                    jds.add(new JdsPrintBean(Gravity.LEFT, "    当场缴纳罚款"));
                } else {
                    jds.add(new JdsPrintBean(Gravity.LEFT, "    持本决定书在15日内到"
                            + jkyh + "缴纳。逾期不缴纳的，每日按罚款数额的3%加处罚款。"));
                }
            } else {
                jds.add(new JdsPrintBean(Gravity.LEFT, "    警告"));
            }
            jds.add(new JdsPrintBean(Gravity.LEFT,
                    "    如不服本决定的，可以在收到本决定书之日起六十日内向" + fyjg
                            + "申请行政复议；或者依照《中华人民共和国行政诉讼法》在六个月内向" + ssjg
                            + "提起行政诉讼。"
            ));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
            jds.add(new JdsPrintBean(Gravity.LEFT, "处罚地点：" + wfdd));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "被处罚人签名：__________________"));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "交通警察：" + mjxm));
            jds.add(new JdsPrintBean(Gravity.RIGHT, cfrq));
            if (!TextUtils.isEmpty(codes[0].getWfxw())
                    && GlobalMethod.getJfs(codes[0].getWfxw()) > 0 && ryfl == 4) {
                jds.add(new JdsPrintBean(Gravity.LEFT, "根据《机动车驾驶证申领和使用规定》记"
                        + GlobalMethod.getJfs(codes[0].getWfxw()) + "分"));
            }
            if (v.getWfjfs() >= 9 && ryfl == 4) {
                jds.add(new JdsPrintBean(Gravity.LEFT, "临界告知：不计本次处罚，您目前累计记分已达"
                        + v.getWfjfs() + "分"));
            }
        } else if (Integer.valueOf(v.getWslb()) == 3
                || Integer.valueOf(v.getWslb()) == 6) {
            // 强制措施
            if (Integer.valueOf(v.getWslb()) == 3)
                jds.add(new JdsPrintBean(Gravity.CENTER, "公安交通管理行政强制措施凭证"));
            else
                jds.add(new JdsPrintBean(Gravity.CENTER, "道路交通安全违法行为处理通知书"));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "编号: " + jdsbh));
            jds.add(new JdsPrintBean(Gravity.LEFT, "当事人:" + v.getDsr()));
            jds.add(new JdsPrintBean(Gravity.LEFT, jszOrSfz + v.getJszh()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证档案编号:" + v.getDabh()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "发证机关:" + fzjg_ch));
            jds.add(new JdsPrintBean(Gravity.LEFT, "准驾车型:" + v.getZjcx()));
            jds.add(new JdsPrintBean(Gravity.LEFT, "电话:" + v.getDh()));
            if (!TextUtils.isEmpty(zzmm))
                jds.add(new JdsPrintBean(Gravity.LEFT, "政治面貌:" + zzmm));
            if (!TextUtils.isEmpty(zyxx))
                jds.add(new JdsPrintBean(Gravity.LEFT, "职业:" + zyxx));
            jds.add(new JdsPrintBean(Gravity.LEFT, "车辆类型:" + jtfs));
            jds.add(new JdsPrintBean(Gravity.LEFT, "车辆牌号:" + v.getHphm()));
            String wfxwms = "";
            for (int i = 0; i < wfxws.length; i++) {
                String bzzScz = "";
                WfxwBzz wfxw = wfxws[i];
                if (!TextUtils.isEmpty(wfxw.getBzz()) && !TextUtils.isEmpty(wfxw.getScz())) {
                    bzzScz = "标准值：" + wfxw.getBzz() + "，实测值：" + wfxw.getScz();
                }
                wfxwms += codes[i] == null ? "" : codes[i].getWfms() + "违法行为(代码"
                        + codes[i].getWfxw() + "，" + bzzScz + ");";
            }
            if (!TextUtils.isEmpty(wfxwms)) {
                wfxwms = wfxwms.substring(0, wfxwms.length() - 1);
            }
            jds.add(new JdsPrintBean(Gravity.LEFT, "    当事人于" + wfsj + "，在"
                    + wfdd + "实施" + wfxwms));
            // 法律条文，强制措施为单独的强制依据，违法通知为违法规定
            String fltk = "";
            for (int i = 0; i < codes.length; i++) {
                if (Integer.valueOf(v.getWslb()) == 6) {
                    fltk += codes[i] == null ? "" : codes[i].getWfgd() + ";";
                } else if (Integer.valueOf(v.getWslb()) == 3) {
                    if (codes[i] != null
                            && !TextUtils.isEmpty(codes[i].getQzcslx())) {
                        String qzyj = WfdmDao.queryQzcsYj(
                                codes[i].getWfxw(), GlobalMethod.getBoxStore(resolver));
                        if (!TextUtils.isEmpty(qzyj)) {
                            fltk += qzyj;
                        } else
                            fltk += codes[i].getWfgd();
                    }
                }
            }
            if (!TextUtils.isEmpty(fltk) && fltk.endsWith(";"))
                fltk = fltk.substring(0, fltk.length() - 1);
            if (Integer.valueOf(v.getWslb()) == 3) {
                jds.add(new JdsPrintBean(Gravity.LEFT, "    根据" + fltk
                        + "之规定，采取行政强制措施："));
                // 有强制措施类型，即不是通知书
                if (!TextUtils.isEmpty(qzcslx)) {
                    if (qzcslx.endsWith(","))
                        qzcslx = qzcslx.substring(0, qzcslx.length() - 1);
                    jds.add(new JdsPrintBean(Gravity.LEFT, "    " + qzcslx));
                }
                jds.add(new JdsPrintBean(Gravity.LEFT, "    请持本凭证在十五日内到"
                        + fxjgqc + "接受处理，逾期不处理的，依法承担法律责任。"));
                jds.add(new JdsPrintBean(Gravity.LEFT,
                        "    如不服本决定的，可以在收到本凭证之日起六十日内向" + fyjg
                                + "申请行政复议；或者依照《中华人民共和国行政诉讼法》在六个月内向" + ssjg
                                + "提起行政诉讼。"
                ));
            } else {
                jds.add(new JdsPrintBean(Gravity.LEFT, "法律依据："));
                jds.add(new JdsPrintBean(Gravity.LEFT, fltk));
                jds.add(new JdsPrintBean(Gravity.LEFT,
                        "    请于十五日内携带本通知书、机动车行驶证、机动车驾驶证，到" + fxjgqc + "接受处理。"));
            }

            jds.add(new JdsPrintBean(Gravity.LEFT, "对上述内容有无异议_______________"));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "当事人签名：__________________"));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "备注：__________________"));
            jds.add(new JdsPrintBean(Gravity.RIGHT, "交通警察：" + mjxm));
            jds.add(new JdsPrintBean(Gravity.RIGHT, cfrq));
            jds.add(new JdsPrintBean(Gravity.LEFT, "本通知书同时作为现场笔录"));
            if (Integer.valueOf(v.getWslb()) == 3) {
                jds.add(new JdsPrintBean(Gravity.LEFT, "一式二份，一份送达当事人，一份附卷"));
            }
        }
        // else if (Integer.valueOf(v.getWslb()) == 1
        // && TextUtils.equals(v.getCfzl(), "1")) {
        // jds.add(new JdsPrintBean(Gravity.CENTER, "轻微道路交通安全违法行为记录单"));
        // jds.add(new JdsPrintBean(Gravity.RIGHT, "编号: " + jdsbh));
        // jds.add(new JdsPrintBean(Gravity.LEFT, "当事人:" + v.getDsr()));
        // jds.add(new JdsPrintBean(Gravity.LEFT, "车辆类型:" + jtfs));
        // jds.add(new JdsPrintBean(Gravity.LEFT, "车辆牌号:" + v.getHphm()));
        // // 根据人员分类决定打印内容
        // if (TextUtils.equals(v.getRyfl(), "4"))
        // jds
        // .add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证:"
        // + v.getJszh()));
        // else
        // jds
        // .add(new JdsPrintBean(Gravity.LEFT, "居民身份证号码:"
        // + v.getJszh()));
        // jds
        // .add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证档案编号:"
        // + v.getDabh()));
        // jds.add(new JdsPrintBean(Gravity.LEFT, "发证机关:" + fzjg_ch));
        //
        // jds.add(new JdsPrintBean(Gravity.LEFT, "    当事人于" + wfsj + "，在"
        // + wfdd + "实施" + wfxw[0].getWfms() + "违法行为(代码"
        // + wfxw[0].getWfxw() + ")。"));
        // jds.add(new JdsPrintBean(Gravity.LEFT, "    根据《中华人民共和国道路交通安全法》"
        // + "第八十七条第二款的规定：决定对当事人给予口头警告后放行。"));
        // jds.add(new JdsPrintBean(Gravity.RIGHT, "交通警察：" + mjxm));
        // jds.add(new JdsPrintBean(Gravity.RIGHT, "备注：__________________"));
        // jds.add(new JdsPrintBean(Gravity.RIGHT, cfrq));
        // if (!TextUtils.isEmpty(v.getWfjfs())
        // && Integer.valueOf(v.getWfjfs()) > 0)
        // jds.add(new JdsPrintBean(Gravity.LEFT, "根据《机动车驾驶证申领和使用规定》记"
        // + v.getWfjfs() + "分"));
        // }

        return jds;
    }

    public static ArrayList<JdsPrintBean> getPrintUnJkJdsContent(
            JdsUnjkPrintBean v, Activity resolver) {
        ArrayList<JdsPrintBean> jds = new ArrayList<JdsPrintBean>();
        if (v == null)
            return null;
        // 公共变量初始化
        // 决定书编号
        StringBuilder jdsbh = new StringBuilder(v.getJdsbh());
        int jyw = Integer.valueOf(jdsbh.substring(6)) % 7;
        jdsbh.insert(6, " ").append(jyw);

        String wfdd = v.getWfdz();
        String jtfs = GlobalMethod.getStringFromKVListByKey(
                GlobalData.jtfsList, v.getJtfs());
        VioWfdmCode wfxw = WfdmDao.queryWfxwByWfdm(v.getWfxw(), GlobalMethod.getBoxStore(resolver));
        if (GlobalData.grxx == null)
            GlobalData.grxx = GlobalMethod.getSavedMjInfo(resolver);
        String wfsj = "";
        String cfrq = "";
        String fzjg_ch = ViolationDAO.getFzjgChinaName(v.getFzjg(), GlobalMethod.getBoxStore(resolver));
        String jkyh = GlobalData.grxx.get(GlobalConstant.JKYH);
        String fyjg = GlobalData.grxx.get(GlobalConstant.FYJG);
        String ssjg = GlobalData.grxx.get(GlobalConstant.SSJG);
        String mjxm = GlobalData.grxx.get(GlobalConstant.XM);
        String fxjgqc = GlobalData.grxx.get(GlobalConstant.BMMC);
        try {
            wfsj = sdfFull.format(sdfNoSec.parse(v.getWfsj()));
            cfrq = sdfShort.format(sdfNoSec.parse(v.getWfsj()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 所有的文书都有标题
        int jIndex = fxjgqc.indexOf("局") + 1;
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(0, jIndex)));
        jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(jIndex)));

        // 简易程序处罚或是警告
        jds.add(new JdsPrintBean(Gravity.CENTER, "公安交通管理简易程序处罚决定书"));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "编号: " + jdsbh));
        jds.add(new JdsPrintBean(Gravity.LEFT, "被处罚人:" + v.getDsr()));
        // 根据人员分类决定打印内容
        jds.add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证/居民身份证号码:"
                + GlobalMethod.ifNull(v.getJszh())));
        jds.add(new JdsPrintBean(Gravity.LEFT, "机动车驾驶证档案编号:"
                + GlobalMethod.ifNull(v.getDabh())));
        jds.add(new JdsPrintBean(Gravity.LEFT, "发证机关:"
                + GlobalMethod.ifNull(fzjg_ch)));
        jds.add(new JdsPrintBean(Gravity.LEFT, "准驾车型:"
                + GlobalMethod.ifNull(v.getZjcx())));
        jds.add(new JdsPrintBean(Gravity.LEFT, "电话:"
                + GlobalMethod.ifNull(v.getDh())));
        jds.add(new JdsPrintBean(Gravity.LEFT, "车辆类型:"
                + GlobalMethod.ifNull(jtfs)));
        jds.add(new JdsPrintBean(Gravity.LEFT, "车辆牌号:"
                + GlobalMethod.ifNull(v.getHphm())));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    被处罚人于" + wfsj + "，在" + wfdd
                + "实施" + wfxw.getWfms() + "违法行为(代码" + wfxw.getWfxw() + ")。"));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    根据" + wfxw.getFltw()
                + "之规定，决定处以："));
        // 区别警告\不罚款\当场缴款
        if (!TextUtils.isEmpty(v.getFkje())
                && !TextUtils.equals(v.getFkje(), "0")) {
            jds.add(new JdsPrintBean(Gravity.LEFT, "    罚款" + v.getFkje() + "元"));
            jds.add(new JdsPrintBean(Gravity.LEFT, "    持本决定书在15日内到" + jkyh
                    + "缴纳。逾期不缴纳的，每日按罚款数额的3%加处罚款。"));
        } else {
            jds.add(new JdsPrintBean(Gravity.LEFT, "    警告"));
        }
        jds.add(new JdsPrintBean(Gravity.LEFT, "    如不服本决定的，可以在收到本决定书之日起六十日内向"
                + fyjg + "申请行政复议；或者依照《中华人民共和国行政诉讼法》在六个月内向" + ssjg + "提起行政诉讼。"));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.LEFT, "处罚地点：" + wfdd));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "被处罚人签名：__________________"));
        jds.add(new JdsPrintBean(Gravity.LEFT, "    "));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "处罚民警：" + v.getZqmj()));
        jds.add(new JdsPrintBean(Gravity.RIGHT, "补打印民警：" + mjxm));
        jds.add(new JdsPrintBean(Gravity.RIGHT, cfrq));
        if (!TextUtils.isEmpty(v.getWfjfs())
                && Integer.valueOf(v.getWfjfs()) > 0)
            jds.add(new JdsPrintBean(Gravity.LEFT, "根据《机动车驾驶证申领和使用规定》记"
                    + v.getWfjfs() + "分"));

        return jds;
    }

}
