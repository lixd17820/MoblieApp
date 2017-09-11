package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TruckDriverBean implements Serializable {

	private String id;
	private String vehId;
	private String xm;
	private String dabh;
	private String jszh;
	private String fzjg;
	private String zjcx;
	private String sjhm;
	private String lxdz;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVehId() {
		return vehId;
	}

	public void setVehId(String vehId) {
		this.vehId = vehId;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getDabh() {
		return dabh;
	}

	public void setDabh(String dabh) {
		this.dabh = dabh;
	}

	public String getJszh() {
		return jszh;
	}

	public void setJszh(String jszh) {
		this.jszh = jszh;
	}

	public String getFzjg() {
		return fzjg;
	}

	public void setFzjg(String fzjg) {
		this.fzjg = fzjg;
	}

	public String getZjcx() {
		return zjcx;
	}

	public void setZjcx(String zjcx) {
		this.zjcx = zjcx;
	}

	public String getSjhm() {
		return sjhm;
	}

	public void setSjhm(String sjhm) {
		this.sjhm = sjhm;
	}

	public String getLxdz() {
		return lxdz;
	}

	public void setLxdz(String lxdz) {
		this.lxdz = lxdz;
	}
}
