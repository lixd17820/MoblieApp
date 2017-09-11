package com.jwt.jbyw;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.jwt.bean.KeyValueBean;
import com.jwt.dao.ViolationDAO;
import com.jwt.dao.WfdmDao;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.IDCard;
import com.jwt.utils.ParserJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VerifyData {


    /**
     * 验证简易事故人员的完整性和合法性
     *
     * @param jbqk
     * @return
     */
    public static String verifyAcdHuman(AcdSimpleHumanBean jbqk) {
        if (TextUtils.isEmpty(jbqk.getXm()))
            return "姓名不能为空！";
        if (!GlobalMethod.hasChina(jbqk.getXm()))
            return "当事人姓名必须有中文";
        if (TextUtils.isEmpty(jbqk.getNl()))
            return "年龄不能为空！";
        if (!TextUtils.isDigitsOnly(jbqk.getNl())
                || Integer.valueOf(jbqk.getNl()) < 1
                || Integer.valueOf(jbqk.getNl()) > 150)
            return "年龄必须在1-150之间！";
        if (TextUtils.isEmpty(jbqk.getXb()))
            return "性别不能为空！";
        if (TextUtils.isEmpty(jbqk.getJtfs()))
            return "交通方式不能为空！";
        if (TextUtils.isEmpty(jbqk.getSgzr()))
            return "认定责任不能为空！";
        if (jbqk.getRybh() == 0 && TextUtils.isEmpty(jbqk.getWfxw1()))
            return "第一当事人必须要有违法行为";
        // 交通方式验证
        String sJtfs = jbqk.getJtfs().substring(0, 1);
        // 非机动车不包括自行车和电动车
        if ("ABCDEF".indexOf(sJtfs) > -1 && "F1,F6".indexOf(jbqk.getJtfs()) < 0) {
            if (!TextUtils.isEmpty(jbqk.getBxgs())
                    || !TextUtils.isEmpty(jbqk.getHpzl())
                    || !TextUtils.isEmpty(jbqk.getHphm())) {
                return "交通方式为非机动车，保险公司，号牌种类，车牌号无需填写！";
            }
            if (!TextUtils.isEmpty(jbqk.getJszzl())
                    || !TextUtils.isEmpty(jbqk.getJl())) {
                return "交通方式为非机动车，驾驶证种类，驾龄无需填写！";
            }
        }
        if ("F1,F6".indexOf(jbqk.getJtfs()) > -1
                && (!TextUtils.isEmpty(jbqk.getHpzl())
                || !TextUtils.isEmpty(jbqk.getJszzl()) || !TextUtils
                .isEmpty(jbqk.getJl()))) {
            return "交通方式为自行车或电动自行车，号牌种类，驾驶证种类，驾龄无需填写！";
        }
        if ("KHNQJ".indexOf(sJtfs) > -1
                && !TextUtils.isEmpty(jbqk.getHpzl())
                && ((Integer.valueOf(jbqk.getHpzl()) >= 7 && Integer
                .valueOf(jbqk.getHpzl()) <= 12) || "14,17,19,20"
                .indexOf(jbqk.getHpzl()) > -1)) {
            return "交通方式为[驾驶汽车]，号牌种类不能是[摩托车]和[拖拉机]！";
        }
        if ("KH".indexOf(sJtfs) > -1 && !TextUtils.isEmpty(jbqk.getHpzl())
                && Integer.valueOf(jbqk.getHpzl()) >= 13) {
            return "交通方式为[客车]或[货车]，号牌种类不能是[农用运输车号]！";
        }
        if ("M1,M2".indexOf(jbqk.getJtfs()) > -1
                && !TextUtils.isEmpty(jbqk.getHpzl())
                && !((Integer.valueOf(jbqk.getHpzl()) >= 7 && Integer
                .valueOf(jbqk.getHpzl()) <= 12)
                || "17,19,21".indexOf(jbqk.getHpzl()) > -1 || Integer
                .valueOf(jbqk.getHpzl()) >= 22)) {
            return "交通方式为[驾驶摩托车]，号牌种类只能是摩托车号牌！";
        }

        if (TextUtils.equals(jbqk.getJtfs(), "T1")
                && !TextUtils.isEmpty(jbqk.getHpzl())
                && !(Integer.valueOf(jbqk.getHpzl()) == 14 || Integer
                .valueOf(jbqk.getHpzl()) >= 22))
            return "交通方式为[驾驶拖拉机]，号牌种类只能是拖拉机号牌！";

        if (TextUtils.equals(jbqk.getJtfs(), "G1")
                && !TextUtils.isEmpty(jbqk.getHpzl())
                && "01,41,42,43".indexOf(jbqk.getHpzl()) < 0)
            return "交通方式为[汽车列车]，号牌种类必为[大车号牌]或者[无号牌]、[假号牌]、[挪用号牌]！";

        if ("F1,F6".indexOf(jbqk.getJtfs()) > -1
                && (!TextUtils.isEmpty(jbqk.getHpzl())
                || !TextUtils.isEmpty(jbqk.getJl()) || !TextUtils
                .isEmpty(jbqk.getJl())))
            return "交通方式为[自行车]或[电动自行车]，号牌种类,驾驶证种类，驾龄,无须填写！";
        if (!TextUtils.isEmpty(jbqk.getHpzl())
                && Integer.valueOf(jbqk.getHpzl()) > 0
                && Integer.valueOf(jbqk.getHpzl()) < 12
                && !TextUtils.isEmpty(jbqk.getHphm())
                && !TextUtils.equals(jbqk.getHphm(), "无")
                && (TextUtils.isEmpty(jbqk.getClxh()) || TextUtils.isEmpty(jbqk
                .getClpp()))) {
            return "车辆型号、车辆品牌不能为空，可点查询车辆或手工录入";
        }
        return null;
    }

    /**
     * 验证事故基本情况逻辑
     *
     * @param acdJbqk
     * @return
     */
    public static String checkAcdSimple(AcdSimpleBean acdJbqk,
                                        boolean isCheckSsTj) {
        if (TextUtils.isEmpty(acdJbqk.getSgdd())) {
            return "事故发生地点不能为空";
        }
        if (TextUtils.isEmpty(acdJbqk.getSsrs())
                || !TextUtils.isDigitsOnly(acdJbqk.getSsrs())
                || acdJbqk.getSsrs().length() > 3) {
            return "受伤人数不能为空或不是数字或超过3位，可以为0";
        }
        if (TextUtils.isEmpty(acdJbqk.getZjccss())
                || !TextUtils.isDigitsOnly(acdJbqk.getZjccss())
                || acdJbqk.getZjccss().length() > 9) {
            return "直接财产损失不能为空或不是数字或超过9位，可以为0";
        }
        if (isCheckSsTj) {
            if (TextUtils.isEmpty(acdJbqk.getSgss())) {
                return "交通事故基本事实及责任不能为空！";
            }
            if (TextUtils.isEmpty(acdJbqk.getZrtjjg())
                    && TextUtils.equals("1", acdJbqk.getJafs())) {
                return "结案方式为调解结案，损害赔偿调解结果不能为空！";
            }
        }
        if (TextUtils.isEmpty(acdJbqk.getSgrdyy())) {
            return "事故认定原因不能为空！";
        }
        if ((TextUtils.equals("11", acdJbqk.getSgxt()) || TextUtils.equals(
                "22", acdJbqk.getSgxt()))
                && TextUtils.isEmpty(acdJbqk.getCljsg())) {
            return "事故形态选择碰撞运动车辆或碰撞静止车辆，车辆间事故不可为空";
        }

        if ((TextUtils.equals("35", acdJbqk.getSgxt()) || TextUtils.equals(
                "36", acdJbqk.getSgxt()))
                && TextUtils.isEmpty(acdJbqk.getDcsg())) {
            return "事故形态选择撞固定物或撞非固定物，单车事故不可为空";
        }

        if (TextUtils.isEmpty(acdJbqk.getJafs())) {
            return "结案方式不能为空！";
        }
        if (TextUtils.equals(acdJbqk.getJafs(), "1")
                && TextUtils.isEmpty(acdJbqk.getTjfs())) {
            return "结案方式为调解结案，调解方式不能为空！";
        }
        if (!TextUtils.equals(acdJbqk.getJafs(), "1")
                && !TextUtils.isEmpty(acdJbqk.getTjfs())) {
            return "结案方式为非调解结案，调解方式必须为空！";
        }
        return null;
    }

    /**
     * 验证基本情况以及人员责任等情况的共同逻辑
     *
     * @param acdJbqk    事故基本情况
     * @param ryjbqkList 人员情况列表
     * @return 空为通过验证，否则返回错误字符描述
     */
    public static String verifyAcdJbqkAndHuman(AcdSimpleBean acdJbqk,
                                               ArrayList<AcdSimpleHumanBean> ryjbqkList) {
        if (ryjbqkList == null || ryjbqkList.size() == 0) {
            return "事故当事人不能为空";
        }
        int iTz = 0;
        int iQz = 0;
        int iZz = 0;
        int iCz = 0;
        int iWz = 0;
        int iWfz = 0;
        int iJdcFjdc = 0;
        int iXr = 0;
        for (AcdSimpleHumanBean hu : ryjbqkList) {
            if (hu.getRybh() == 0 && TextUtils.equals(hu.getSgzr(), "4"))
                return "第一当事人责任必须是同责以上！";
            int sgzr = Integer.valueOf(hu.getSgzr());
            switch (sgzr) {
                case 1:
                    iQz++;
                    break;
                case 2:
                    iZz++;
                    break;
                case 3:
                    iTz++;
                    break;
                case 4:
                    iCz++;
                    break;
                case 5:
                    iWz++;
                    break;
                case 6:
                    iWfz++;
                    break;
                default:
                    break;
            }
            if (!(hu.getJtfs().startsWith("A") || hu.getJtfs().startsWith("C") || hu
                    .getJtfs().startsWith("X"))) {
                iJdcFjdc++;
            }
            if (TextUtils.equals(hu.getJtfs(), "A1"))
                iXr++;
        }
        if (iTz == 1)
            return "负同等责任必须是两人！";
        if (iZz >= 2)
            return "负主要责任的只能是一人！";
        if (iQz >= 2)
            return "负全部责任的只能是一人！";
        if (iQz == 1 && (iZz > 0 || iCz > 0 || iWfz > 0))
            return "选了全部责任其他人应无责！";
        if (iZz == 1 && iCz == 0)
            return "选了主要责任就应该选次要责任！";
        if (iCz == 1 && iZz == 0)
            return "选了次要责任就应该有主要责任！";
        if (iWfz > 0 && (iQz > 0 || iZz > 0 || iCz > 0 || iWz > 0 || iTz > 0))
            return "选了无法认定后,其他当事人责任都应该为无法认定！";
        int xt = Integer.valueOf(acdJbqk.getSgxt());
        if (xt >= 31 && xt <= 38) {
            if (iJdcFjdc != 1)
                return "事故形态为单方事故,只能有一个当事人交通方式为驾驶机动车或非机动车！";
            if (iXr > 0)
                return "事故形态为单方事故,当事人交通方式不能为行人！";
        }
        if (xt >= 21 && xt <= 29) {
            if (iJdcFjdc == 0)
                return "事故形态为车辆与人,当事人交通方式必须存在驾驶机动车或非机动车！";
            if (iXr == 0)
                return "事故形态为车辆与人,当事人交通方式必须存在行人！";
        }
        if (xt >= 11 && xt <= 12 && iJdcFjdc < 2) {
            return "事故形态为车辆间事故,当事人交通方式必须2个及以上驾驶机动车或非机动车！";
        }

        return null;
    }

    /**
     * 所有公共验证
     *
     * @param v
     * @return
     */
    public static String verifyCommVio(VioViolation v, Context context) {
        if (v == null)
            return "对象不能为空";
        int ryfl = Integer.valueOf(v.getRyfl());
        int clfl = Integer.valueOf(v.getClfl());
        //int len = TextUtils.getTrimmedLength(v.getJszh());

        if (TextUtils.isEmpty(v.getWfdd()) || TextUtils.isEmpty(v.getWfdz())) {
            return "违法地点不能为空";
        }
        if (!GlobalMethod.isChinaOrAz(v.getWfdz())) {
            return "违法地点包括非正常字符";
        }
        // 身份证号不符合标准
        if ("A".equals(v.getZjlx()) && !TextUtils.isEmpty(v.getJszh()) && v.getJszh().length() == 15)
            return "请填入18位身份证号，15位已停止使用";

        if ("A".equals(v.getZjlx()) && TextUtils.isEmpty(v.getJszh())
                || !IDCard.Verify(v.getJszh().trim().toUpperCase())) {
            // 未通过身份证难证的，进一步确认
            // 人员分类是行人、非机动车且验证规则为非严格模式时允许通过
            // 不是 （非机动车且宽松模式且长度为0）不能通过
            if (!(ryfl <= 3 && !GlobalSystemParam.isCheckFjdcSfzm && TextUtils
                    .isEmpty(v.getJszh())))
                return "身份证号不符合标准";
        }

        // 验证当事人姓名
        if (TextUtils.isEmpty(v.getDsr()))
            return "当事人姓名不能为空";

        if (!GlobalMethod.hasChina(v.getDsr()))
            return "当事人姓名必须包含中文";
        if (!GlobalMethod.isChinaOrAz(v.getDsr()))
            return "当事人姓名包含非法字符";
        // 非机动车人员对应非机动车
        if (ryfl == 0 && clfl != 1)
            return "非机动车人员分类应对应非机动车车辆分类";

        if ((TextUtils.isEmpty(v.getDabh()) || TextUtils.getTrimmedLength(v
                .getDabh()) != 12) && (ryfl == 4 || ryfl == 7)) {
            return "公安或农机档案编号录入不符合标准";
        }

        if (TextUtils.equals(v.getRyfl(), "4")) {
            if (TextUtils.isEmpty(v.getFzjg()) || v.getFzjg().length() != 2)
                return "公安驾驶证发证机关长度不是2位";
            if (!TextUtils.equals(v.getDabh().substring(0, 4), "3206")
                    && TextUtils.equals(v.getFzjg(), "苏F"))
                return "非南通驾驶员需选择发证机关";
        }
        if (TextUtils.isEmpty(v.getJtfs())) {
            return "车辆类型即交通方式不能为空";
        }

        if (TextUtils.equals(v.getClfl(), "6")
                && "13,14,25".indexOf(v.getHpzl()) < 0) {
            return "农用车的号牌种类不正确";
        }

        // 车辆分类为公安牌证
        if (clfl == 3) {
            int len = TextUtils.getTrimmedLength(v.getHphm());
            if (len < 6 || len > 8) {
                return "号牌号码长度录入不符合标准";
            }
            String hm = v.getHphm().substring(1);
            if (!GlobalMethod.isNumberOrAZ(hm)) {
                return "号牌号码包含非法字符";
            }
            String[] syxz = ViolationDAO.getJsonStrs(v.getGzxm(), "syxz");
            if (syxz == null || syxz.length == 0 || TextUtils.isEmpty(syxz[0]))
                return "公安号牌车辆使用性质不能为空";
        }
        if (clfl == 2
                && (!TextUtils.isEmpty(v.getHpzl()) || !TextUtils.isEmpty(v
                .getHphm()))) {
            return "无牌无证机动车不可选号牌种类或号牌号码，请选择适当的交通方式";
        }
        if (clfl == 1 && !TextUtils.isEmpty(v.getHpzl())) {
            return "非机动车不可选号牌种类或号牌号码，请选择适当的交通方式";
        }
        // 验证联系方式和准驾车型是否包含非法字符
        if (!TextUtils.isEmpty(v.getLxfs())
                && !GlobalMethod.isChinaOrAz(v.getLxfs())) {
            return "联系地址包含非法字符";
        }
        if (!TextUtils.isEmpty(v.getZjcx())
                && ryfl == 4
                && (!GlobalMethod.isNumberOrAZ(v.getZjcx()) || !checkZjcx(v
                .getZjcx()))) {
            return "准驾车型包含非法字符";
        }
        if (!TextUtils.isEmpty(v.getDh())
                && !GlobalMethod.isNumberOrAZ(v.getDh())) {
            return "联系电话包含非法字符";
        }
        if (TextUtils.isEmpty(v.getWfsj())) {
            return "违法时间不能为空";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date wfsj = sdf.parse(v.getWfsj());
            Date d = new Date();
            if (wfsj.getTime() > d.getTime())
                return "违法时间不能大于当前时间";
            if (d.getTime() - wfsj.getTime() > 48 * 60 * 60 * 1000) {
                return "违法时间不能早于当前四十八小时";
            }
        } catch (ParseException e) {
            return "违法时间格式错误";
        }
        if (clfl == 3) {
            String err = verifyHpzlJtfs(v.getHpzl(), v.getJtfs(), clfl);
            if (!TextUtils.isEmpty(err))
                return err;
        }

        return null;
    }

    /**
     * 标准值和实测值的验证
     *
     * @param wfxw
     * @param bzz
     * @param scz
     * @return
     */
    private static String verifyScz(String wfxw, int bzz, int scz) {
        if (TextUtils.isEmpty(wfxw) || wfxw.length() < 4)
            return null;
        if (bzz > 999999 || scz > 999999) {
            return "标准值或实测值不能大于六位数";
        }
        Log.e("标准值，实测值：", bzz + "," + scz);
        String fourWfxw = wfxw.substring(0, 4);
        if (GlobalConstant.sczCode.indexOf(fourWfxw) > -1) {
            if (bzz == 0 || scz == 0)
                return "标准值或实测值不能为0或空";

            //处罚在三超和酒之间
            if (GlobalConstant.jhjsCodes.indexOf(fourWfxw) > -1 && (bzz != 20 || scz < 20 || scz >= 80)) {
                //酒后驾驶
                return "酒后驾驶标准值为20，实测值应在20至80之间";
            }
            if (GlobalConstant.cjjsCodes.indexOf(fourWfxw) > -1 && (bzz != 80 || scz < 80))
                return "醉酒后驾驶标准值为80，实测值应大于等于80";
            double cgbl = (double) (scz - bzz) / ((double) bzz);
            if (GlobalConstant.kccyLargeCodes.indexOf(fourWfxw) > -1) {
                if (cgbl < 0.2) {
                    return "超员人数不足20%";
                }
            }
            if (GlobalConstant.kccySmallCodes.indexOf(fourWfxw) > -1) {
                if (scz <= bzz)
                    return "客车超员实测超不能小于等于标准值";
                if (cgbl >= 0.2)
                    return "客车超员代码错误，超过20%";
            }
            if (GlobalConstant.hcczLargeCodes.indexOf(fourWfxw) > -1) {
                if (cgbl < 0.3)
                    return "超载比例不足30%";
            }
            if (GlobalConstant.hcczSmallCodes.indexOf(fourWfxw) > -1) {
                if (scz <= bzz)
                    return "货车超载实测超不能小于等于标准值";
                if (cgbl >= 0.3)
                    return "货车超载代码错误，超过30%";
            }
        }
        return null;
    }

    private static String verifyHpzlJtfs(String hpzl, String jtfs, int clfl) {

        if (TextUtils.isEmpty(jtfs) || TextUtils.isEmpty(hpzl))
            return "公安号牌，号牌种类和交通方式为必填项";
        return null;
    }

    /**
     * 验证简易程序处罚的正确性
     *
     * @param v
     * @param context
     * @return 错误描述
     */
    public static String verifyJycxVio(VioViolation v, Context context) {
        int ryfl = Integer.valueOf(v.getRyfl());
        int clfl = Integer.valueOf(v.getClfl());

        if (ryfl == 3)
            return "无证驾驶人员类型不能用简易程序处罚";

        if (!TextUtils.isEmpty(v.getWfxw()) && v.getWfxw().contains("1005"))
            return "无证驾驶不能用简易程序处罚";
        // 检验当场处罚决定书的人员分类和违法代码的关系
        if (!ViolationDAO.checkWfxwAndRyfl(v.getWfxw(), ryfl,
                GlobalMethod.getBoxStore(context)))
            return "人员分类和违法代码不匹配";
        // 验证交款方式
        // 简易程序处罚 公安机动车不能选不缴款或当场缴款
        if (TextUtils.isEmpty(v.getJkfs()))
            return "缴款方式为必填项";
        if (clfl == 3
                && (TextUtils.equals("0", v.getJkfs()) || TextUtils.equals("1",
                v.getJkfs()))) {
            return "当场处罚中公安机动车不能选择当场缴款或不缴款";
        }

        // 简易程序处罚 公安驾驶证不能选不缴款或当场缴款
        if (ryfl == 4
                && (TextUtils.equals("0", v.getJkfs()) || TextUtils.equals("1",
                v.getJkfs()))) {
            return "当场处罚中公安驾驶证不能选择当场缴款或不缴款";
        }

        // 当场处罚罚款不得超过200元
        if (Integer.valueOf(v.getFkje()) > 200)
            return "简易程序处罚罚款金额不得大于200元";
        // 当场处罚罚款金额大于50元不能当场收款
        if (Integer.valueOf(v.getFkje()) > 50
                && TextUtils.equals("1", v.getJkfs()))
            return "当场处罚罚款金额大于50元不能当场收款";
        // 当场处罚,罚款金额不为零,不能不罚款
        if (v.getFkje() == 0)
            return "当场处罚,罚款金额不为零,不能不罚款";

        if (clfl == 3 && !TextUtils.isEmpty(v.getWfxw())
                && !TextUtils.isEmpty(v.getJtfs())) {
            String err = WfdmDao.checkWfxwCllx(v.getWfxw(), v.getJtfs());
            if (!TextUtils.isEmpty(err))
                return err;
        }
        return verifyWfxw(v);
    }

    public static String verifyWfxw(VioViolation v){
        JSONArray wfxws = ParserJson.getJsonArray(v.getWfxw());
        for (int i = 0; i < wfxws.length(); i++) {
            JSONObject wfxw = wfxws.optJSONObject(i);
            if (TextUtils.isEmpty(wfxw.optString("wfxw")))
                continue;
            String err = verifyScz(wfxw.optString("wfxw"), wfxw.optInt("bzz"), wfxw.optInt("scz"));
            if (!TextUtils.isEmpty(err))
                return err;
        }
        return null;
    }

    /**
     * 验证微警告
     *
     * @param v
     * @return
     */
    public static String verifyQwjgVio(VioViolation v) {
        return verifyWfxw(v);
    }

    /**
     * 验证强制措施
     *
     * @param v
     * @return
     */
    public static String verifyQzcsVio(VioViolation v) {
        return verifyWfxw(v);
    }

    /**
     * 验证违法通知
     *
     * @param v
     * @param context
     * @return
     */
    public static String verifyWftzVio(VioViolation v, Context context) {
        return null;
    }

    public static String verifyViolation(VioViolation v, int cfCatalog,
                                         Context context) {
        String errInfo = null;
        return errInfo;
    }

    private static boolean checkZjcx(String zjcx) {
        if (!TextUtils.equals(zjcx, "无") && !TextUtils.isEmpty(zjcx)) {
            for (KeyValueBean kv : GlobalData.zjcxList) {
                String k = kv.getKey();
                zjcx = zjcx.replace(k, "");
            }
            return TextUtils.isEmpty(zjcx);
        }
        return true;
    }
}
