package com.jwt.bean;

import java.io.Serializable;

import android.text.TextUtils;

@SuppressWarnings("serial")
public class GcmBbInfoBean implements Serializable, CommTwoRowSelUpInf {
	private String id;
	private String jybh;
	private String gpsId;
	private String bbmc;
	private String fjrs;
	private String kssj;
	private String lxfs;
	private String lxhm;
	private String djsj;
	private String scbj;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJybh() {
		return jybh;
	}

	public void setJybh(String jybh) {
		this.jybh = jybh;
	}

	public String getGpsId() {
		return gpsId;
	}

	public void setGpsId(String gpsId) {
		this.gpsId = gpsId;
	}

	public String getBbmc() {
		return bbmc;
	}

	public void setBbmc(String bbmc) {
		this.bbmc = bbmc;
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

	public String getLxfs() {
		return lxfs;
	}

	public void setLxfs(String lxfs) {
		this.lxfs = lxfs;
	}

	public String getLxhm() {
		return lxhm;
	}

	public void setLxhm(String lxhm) {
		this.lxhm = lxhm;
	}

	public String getDjsj() {
		return djsj;
	}

	public void setDjsj(String djsj) {
		this.djsj = djsj;
	}

	public String getScbj() {
		return scbj;
	}

	public void setScbj(String scbj) {
		this.scbj = scbj;
	}

	@Override
	public String getUpText() {

		return bbmc;
	}

	@Override
	public String getDownText() {
		return kssj + "|" + lxhm;
	}

	@Override
	public boolean isUploaded() {
		return TextUtils.equals("1", scbj);
	}

}
