package com.jwt.zapc;

import java.io.Serializable;

public class ZapcReturn implements Serializable {

	private String[] pcbh;
	private String cgbj;
	private String scms;

	public String[] getPcbh() {
		return pcbh;
	}

	public void setPcbh(String[] pcbh) {
		this.pcbh = pcbh;
	}

	public String getCgbj() {
		return cgbj;
	}

	public void setCgbj(String cgbj) {
		this.cgbj = cgbj;
	}

	public String getScms() {
		return scms;
	}

	public void setScms(String scms) {
		this.scms = scms;
	}

}
