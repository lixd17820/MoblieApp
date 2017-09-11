package com.jwt.utils;

/**
 * Created by lixiaodong on 2017/8/25.
 */

public enum DictName {
    FRM_XB("性别", "00/0035"),
    SHENFEN("省份", "00/0032"),
    QGXZQH("全国行政区域", "00/0033"),
    CHENSHI("城市", "00/0034"),
    JTFS("交通方式", "04/0001"),
    HPZL("号牌种类", "00/1007"),
    JKFS("缴款方式", "04/0008"),
    JKBJ("缴款标记", "04/0029"),
    HPQZ("号牌前辍", "00/3140"),
    CLFL("车辆分类", "04/0081"),
    CFZL("处罚种类", "04/0002"),
    RYFL("人员分类", "04/0080"),
    WSLB("文书类别", "04/0015"),
    QZCSLX("强制措施类型", "04/0011"),
    SJXM("收缴项目", "04/0012"),
    KLXM("扣留项目", "04/0016"),
    XZQH("行政区划", "00/0050"),
    ZJCX("准驾车型", "00/2001"),
    SYXZ("使用性质", "00/1003"),
    ZZMM("政治面貌", "00/6131"),
    ZJLX("证件类型", "00/2019"),
    ZYXX("职业信息", "04/0101"),
    ACD_TQ("天气", "03/0111"),
    ACD_SGXT("事故形态", "03/0112"),
    ACD_CLJPZ("车辆间碰撞", "03/0116"),
    ACD_DLPZ("单车碰撞", "03/0138"),
    ACD_JAFS("结案方式", "03/0167"),
    ACD_TJFS("调解方式", "03/0166"),
    ACD_RYLX("人员类型", "03/0135"),
    ACD_JSZZL("驾驶证种类", "03/0136"),
    ACD_SGZR("事故责任", "00/3138"),
    ACD_JTFS("事故交通方式", "03/0130"),
    ACD_WFXW("交通事故违法行为", "03/0160");

    private String name;
    private String code;

    private DictName(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
