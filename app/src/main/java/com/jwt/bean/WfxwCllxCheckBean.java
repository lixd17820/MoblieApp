package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WfxwCllxCheckBean implements Serializable{

	private String id;
	private String wfxw;
	private String alCllx;
	private String deCllx;
	private String ms;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWfxw() {
		return wfxw;
	}

	public void setWfxw(String wfxw) {
		this.wfxw = wfxw;
	}

	public String getAlCllx() {
		return alCllx;
	}

	public void setAlCllx(String alCllx) {
		this.alCllx = alCllx;
	}

	public String getDeCllx() {
		return deCllx;
	}

	public void setDeCllx(String deCllx) {
		this.deCllx = deCllx;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

}
