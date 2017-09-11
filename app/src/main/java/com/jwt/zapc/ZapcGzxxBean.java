package com.jwt.zapc;

import java.io.Serializable;

import android.text.TextUtils;

import com.jwt.bean.CommTwoRowSelUpInf;


public class ZapcGzxxBean implements Serializable, CommTwoRowSelUpInf {

    private String id;
    private String gzxxbh;
    private String xffs;
    private String djdw;
    private String jybh;
    private String xlmc;
    private String gzdd;
    private String fjrs;
    private String kssj;
    private String jssj;
    private String csbj;
    private String zqmj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGzxxbh() {
        return gzxxbh;
    }

    public void setGzxxbh(String gzxxbh) {
        this.gzxxbh = gzxxbh;
    }

    public String getXffs() {
        return xffs;
    }

    public void setXffs(String xffs) {
        this.xffs = xffs;
    }

    public String getDjdw() {
        return djdw;
    }

    public void setDjdw(String djdw) {
        this.djdw = djdw;
    }

    public String getJybh() {
        return jybh;
    }

    public void setJybh(String jybh) {
        this.jybh = jybh;
    }

    public String getXlmc() {
        return xlmc;
    }

    public void setXlmc(String xlmc) {
        this.xlmc = xlmc;
    }

    public String getGzdd() {
        return gzdd;
    }

    public void setGzdd(String gzdd) {
        this.gzdd = gzdd;
    }

    public String getFjrs() {
        return fjrs;
    }

    public void setFjrs(String fjrs) {
        this.fjrs = fjrs;
    }

    public String getKssj() {
        return kssj;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public String getCsbj() {
        return csbj;
    }

    public void setCsbj(String csbj) {
        this.csbj = csbj;
    }

    public String getZqmj() {
        return zqmj;
    }

    public void setZqmj(String zqmj) {
        this.zqmj = zqmj;
    }

    @Override
    public String toString() {

//		return "开始时间："
//				+ ZaPcdjDao.changeDptModNor(kssj).substring(0, 16)
//				+ "\n"
//				+ (TextUtils.isEmpty(jssj) ? "工作未结束" : "结束时间："
//						+ ZaPcdjDao.changeDptModNor(jssj).substring(0, 16));
        return "";
    }

    @Override
    public String getUpText() {
        //return "开始时间：" + ZaPcdjDao.changeDptModNor(kssj).substring(0, 16);
        return "";
    }

    @Override
    public String getDownText() {
        //return (TextUtils.isEmpty(jssj) ? "工作未结束" : "结束时间："
        //		+ ZaPcdjDao.changeDptModNor(jssj).substring(0, 16));
        return "";
    }

    @Override
    public boolean isUploaded() {
        return TextUtils.equals("1", csbj);
    }

}
