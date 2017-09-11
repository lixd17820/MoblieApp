package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/26.
 */
@Entity
public class FrmCode {
    @Id
    long id;

    private String xtlb;
    private String dmlb;
    private String dmz;
    private String dmsm1;
    private String dmsm2;
    private String dmsm3;
    private String dmsm4;
    private String dmsx;
    private String sxh;
    private String ywdx;
    private String zt;
    private String scbj;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getXtlb() {
        return xtlb;
    }

    public void setXtlb(String xtlb) {
        this.xtlb = xtlb;
    }

    public String getDmlb() {
        return dmlb;
    }

    public void setDmlb(String dmlb) {
        this.dmlb = dmlb;
    }

    public String getDmz() {
        return dmz;
    }

    public void setDmz(String dmz) {
        this.dmz = dmz;
    }

    public String getDmsm1() {
        return dmsm1;
    }

    public void setDmsm1(String dmsm1) {
        this.dmsm1 = dmsm1;
    }

    public String getDmsm2() {
        return dmsm2;
    }

    public void setDmsm2(String dmsm2) {
        this.dmsm2 = dmsm2;
    }

    public String getDmsm3() {
        return dmsm3;
    }

    public void setDmsm3(String dmsm3) {
        this.dmsm3 = dmsm3;
    }

    public String getDmsm4() {
        return dmsm4;
    }

    public void setDmsm4(String dmsm4) {
        this.dmsm4 = dmsm4;
    }

    public String getDmsx() {
        return dmsx;
    }

    public void setDmsx(String dmsx) {
        this.dmsx = dmsx;
    }

    public String getSxh() {
        return sxh;
    }

    public void setSxh(String sxh) {
        this.sxh = sxh;
    }

    public String getYwdx() {
        return ywdx;
    }

    public void setYwdx(String ywdx) {
        this.ywdx = ywdx;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getScbj() {
        return scbj;
    }

    public void setScbj(String scbj) {
        this.scbj = scbj;
    }
}
