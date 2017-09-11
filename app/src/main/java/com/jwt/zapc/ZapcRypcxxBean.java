package com.jwt.zapc;

import java.io.Serializable;


public class ZapcRypcxxBean implements Serializable, Zapcxx {

    private String pcrybh; // 盘查人员编号
    private String gzbh; // 工作编号
    private String rycljg;// 人员处理结果
    private String rypcyy;// 人员盘查原因
    private String rypcdd;// 人员盘查地点
    private String rybdfs;// 人员比对方式
    private String rybdjg;// 人员比对结果
    private String rypcsj;// 人员盘查时间
    private String jccfx;// 进出城方向
    private String scbj;
    private ZapcRyjbxxBean ryjbxx;// 人员基本信息

    public String getPcrybh() {
        return pcrybh;
    }

    public void setPcrybh(String pcrybh) {
        this.pcrybh = pcrybh;
    }

    public String getGzbh() {
        return gzbh;
    }

    public void setGzbh(String gzbh) {
        this.gzbh = gzbh;
    }

    public String getRycljg() {
        return rycljg;
    }

    public void setRycljg(String rycljg) {
        this.rycljg = rycljg;
    }

    public String getRypcyy() {
        return rypcyy;
    }

    public void setRypcyy(String rypcyy) {
        this.rypcyy = rypcyy;
    }

    public String getRypcdd() {
        return rypcdd;
    }

    public void setRypcdd(String rypcdd) {
        this.rypcdd = rypcdd;
    }

    public String getRybdfs() {
        return rybdfs;
    }

    public void setRybdfs(String rybdfs) {
        this.rybdfs = rybdfs;
    }

    public String getRybdjg() {
        return rybdjg;
    }

    public void setRybdjg(String rybdjg) {
        this.rybdjg = rybdjg;
    }

    public String getRypcsj() {
        return rypcsj;
    }

    public void setRypcsj(String rypcsj) {
        this.rypcsj = rypcsj;
    }

    public String getJccfx() {
        return jccfx;
    }

    public void setJccfx(String jccfx) {
        this.jccfx = jccfx;
    }

    public ZapcRyjbxxBean getRyjbxx() {
        if (ryjbxx == null)
            ryjbxx = new ZapcRyjbxxBean();
        return ryjbxx;
    }

    public void setRyjbxx(ZapcRyjbxxBean ryjbxx) {
        this.ryjbxx = ryjbxx;
    }

    public String getScbj() {
        return scbj;
    }

    public void setScbj(String scbj) {
        this.scbj = scbj;
    }

    @Override
    public int getId() {
        return pcrybh == null ? -1 : Integer.valueOf(pcrybh);
    }

    @Override
    public int getPcZl() {
        return PCRYXXZL;
    }

    @Override
    public String getPczlMs() {
        return "人员";
    }

    @Override
    public String getXxms() {
//		return "盘查时间：" + ZaPcdjDao.changeDptModNor(rypcsj) + "\n"
//				+ ryjbxx.getGmsfhm() + " " + ryjbxx.getXm();
        return "";
    }

    @Override
    public String getGlgzbh() {
        return gzbh;
    }

    @Override
    public String getPcdd() {
        return rypcdd;
    }

}
