package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by lixiaodong on 2017/8/30.
 */

@Entity
public class SysParaValue {
    @Id
    long id;
    private String xtlb;
    private String glbm;
    private String gjz;
    private String csz;
    private String csbj;
    private String bjcsbj;

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

    public String getGlbm() {
        return glbm;
    }

    public void setGlbm(String glbm) {
        this.glbm = glbm;
    }

    public String getGjz() {
        return gjz;
    }

    public void setGjz(String gjz) {
        this.gjz = gjz;
    }

    public String getCsz() {
        return csz;
    }

    public void setCsz(String csz) {
        this.csz = csz;
    }

    public String getCsbj() {
        return csbj;
    }

    public void setCsbj(String csbj) {
        this.csbj = csbj;
    }

    public String getBjcsbj() {
        return bjcsbj;
    }

    public void setBjcsbj(String bjcsbj) {
        this.bjcsbj = bjcsbj;
    }
}
