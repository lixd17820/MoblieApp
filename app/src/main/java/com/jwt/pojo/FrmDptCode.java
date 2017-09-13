package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/9/13.
 */

@Entity
public class FrmDptCode {

    @Id
    long id;
    private String xtlb;
    private String dmlb;
    private String dmz;
    private String dmsm1;
    private String dmsm2;
    private String dmlbsm;
    private String zt;

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

    public String getDmlbsm() {
        return dmlbsm;
    }

    public void setDmlbsm(String dmlbsm) {
        this.dmlbsm = dmlbsm;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }
}
