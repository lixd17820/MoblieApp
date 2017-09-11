package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GcmBbddBean implements Serializable {
	private String id;
	private String mc;
	private String gl4;
	private String gl5;
	private String gxdw;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public String getGl4() {
		return gl4;
	}

	public void setGl4(String gl4) {
		this.gl4 = gl4;
	}

	public String getGl5() {
		return gl5;
	}

	public void setGl5(String gl5) {
		this.gl5 = gl5;
	}

	public String getGxdw() {
		return gxdw;
	}

	public void setGxdw(String gxdw) {
		this.gxdw = gxdw;
	}

}
