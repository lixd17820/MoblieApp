package com.jwt.jbyw;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AcdWfxwBean implements Serializable {

	private String wfxwdm;
	private String wfnr;
	private String rdyy;
	private String wffl;

	public String getWfxwdm() {
		return wfxwdm;
	}

	public void setWfxwdm(String wfxwdm) {
		this.wfxwdm = wfxwdm;
	}

	public String getWfnr() {
		return wfnr;
	}

	public void setWfnr(String wfnr) {
		this.wfnr = wfnr;
	}

	public String getRdyy() {
		return rdyy;
	}

	public void setRdyy(String rdyy) {
		this.rdyy = rdyy;
	}

	public String getWffl() {
		return wffl;
	}

	public void setWffl(String wffl) {
		this.wffl = wffl;
	}

}
