package com.jwt.bean;

public class WfddBean {
	private String xzqh;
	private String dldm;
	private String lddm;
	private String ms;
	private String ldmc;
	private boolean isGsd;

	public WfddBean() {

	}

	public WfddBean(String xzqh, String dldm, String lddm, String ms,
			String ldmc, boolean isGsd) {
		this.xzqh = xzqh;
		this.dldm = dldm;
		this.lddm = lddm;
		this.ms = ms;
		this.ldmc = ldmc;
		this.isGsd = isGsd;
	}

	public String getXzqh() {
		return xzqh;
	}

	public void setXzqh(String xzqh) {
		this.xzqh = xzqh;
	}

	public String getDldm() {
		return dldm;
	}

	public void setDldm(String dldm) {
		this.dldm = dldm;
	}

	public String getLddm() {
		return lddm;
	}

	public void setLddm(String lddm) {
		this.lddm = lddm;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getLdmc() {
		return ldmc;
	}

	public void setLdmc(String ldmc) {
		this.ldmc = ldmc;
	}

	public boolean isGsd() {
		return isGsd;
	}

	public void setGsd(boolean isGsd) {
		this.isGsd = isGsd;
	}

//	@Override
//	public String toString() {
//		return "WfddBean{" +
//				"xzqh='" + xzqh + '\'' +
//				", dldm='" + dldm + '\'' +
//				", lddm='" + lddm + '\'' +
//				", ms='" + ms + '\'' +
//				", ldmc='" + ldmc + '\'' +
//				", isGsd=" + isGsd +
//				'}';
//	}
}
