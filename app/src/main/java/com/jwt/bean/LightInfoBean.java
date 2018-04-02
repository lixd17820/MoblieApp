package com.jwt.bean;

import org.json.JSONArray;

/**
 * Created by lixiaodong on 2018/1/26.
 */

public class LightInfoBean {

    private String _id;
    private String crossId;
    private String crossName;
    private String dldm;
    private String dlmc;
    private String lkfx;
    private String light;
    private String azfs;
    private String dssx;
    private String lwfs;
    private int zdsl;
    private int fdsl;
    private String city;
    private String jybh;
    private String gxsj;
    private int picNum;
    private JSONArray pics;
    private JSONArray localPics;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getCrossName() {
        return crossName;
    }

    public void setCrossName(String crossName) {
        this.crossName = crossName;
    }

    public String getDldm() {
        return dldm;
    }

    public void setDldm(String dldm) {
        this.dldm = dldm;
    }

    public String getDlmc() {
        return dlmc;
    }

    public void setDlmc(String dlmc) {
        this.dlmc = dlmc;
    }

    public String getLkfx() {
        return lkfx;
    }

    public void setLkfx(String lkfx) {
        this.lkfx = lkfx;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getAzfs() {
        return azfs;
    }

    public void setAzfs(String azfs) {
        this.azfs = azfs;
    }

    public String getDssx() {
        return dssx;
    }

    public void setDssx(String dssx) {
        this.dssx = dssx;
    }

    public String getLwfs() {
        return lwfs;
    }

    public void setLwfs(String lwfs) {
        this.lwfs = lwfs;
    }

    public int getZdsl() {
        return zdsl;
    }

    public void setZdsl(int zdsl) {
        this.zdsl = zdsl;
    }

    public int getFdsl() {
        return fdsl;
    }

    public void setFdsl(int fdsl) {
        this.fdsl = fdsl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJybh() {
        return jybh;
    }

    public void setJybh(String jybh) {
        this.jybh = jybh;
    }

    public String getGxsj() {
        return gxsj;
    }

    public void setGxsj(String gxsj) {
        this.gxsj = gxsj;
    }

    public int getPicNum() {
        return picNum;
    }

    public void setPicNum(int picNum) {
        this.picNum = picNum;
    }

    public JSONArray getPics() {
        return pics;
    }

    public void setPics(JSONArray pics) {
        this.pics = pics;
    }

    public JSONArray getLocalPics() {
        return localPics;
    }

    public void setLocalPics(JSONArray localPics) {
        this.localPics = localPics;
    }
}
