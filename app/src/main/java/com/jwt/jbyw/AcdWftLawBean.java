package com.jwt.jbyw;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AcdWftLawBean implements Serializable {

	private String xh;
	private String flmc;
	private String tkmc;
	private String tknr;

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getFlmc() {
		return flmc;
	}

	public void setFlmc(String flmc) {
		this.flmc = flmc;
	}

	public String getTkmc() {
		return tkmc;
	}

	public void setTkmc(String tkmc) {
		this.tkmc = tkmc;
	}

	public String getTknr() {
		return tknr;
	}

	public void setTknr(String tknr) {
		this.tknr = tknr;
	}

}
