package com.jwt.pojo;

import com.jwt.dao.ZaPcdjDao;
import com.jwt.utils.GlobalMethod;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ZapcRypcxxBean implements Serializable, Zapcxx {

    @Id
    long id;
    private String pcrybh; // 盘查人员编号
    private long gzbh; // 工作编号
    private String rycljg;// 人员处理结果
    private String rypcyy;// 人员盘查原因
    private String rypcdd;// 人员盘查地点
    private String rybdfs;// 人员比对方式
    private String rybdjg;// 人员比对结果
    private String rypcsj;// 人员盘查时间
    private String jccfx;// 进出城方向
    private String scbj;
    private long ryjbxxId;// 人员基本信息
    private String gmsfzh;
    private String xm;

    public String getPcrybh() {
        return pcrybh;
    }

    public void setPcrybh(String pcrybh) {
        this.pcrybh = pcrybh;
    }

    public long getGzbh() {
        return gzbh;
    }

    public void setGzbh(long gzbh) {
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

    public long getRyjbxxId() {
        return ryjbxxId;
    }

    public void setRyjbxxId(long ryjbxxId) {
        this.ryjbxxId = ryjbxxId;
    }

    public String getScbj() {
        return scbj;
    }

    public void setScbj(String scbj) {
        this.scbj = scbj;
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
        return "盘查时间：" + ZaPcdjDao.changeDptModNor(rypcsj) + "\n"
                + GlobalMethod.ifNull(xm) + "\t" + GlobalMethod.ifNull(getGmsfzh());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGmsfzh() {
        return gmsfzh;
    }

    public void setGmsfzh(String gmsfzh) {
        this.gmsfzh = gmsfzh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    @Override
    public String getGlgzbh() {
        return gzbh + "";
    }

    @Override
    public String getPcdd() {
        return rypcdd;
    }

}
