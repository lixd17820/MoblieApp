package com.jwt.pojo;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class WfxwForce {
	@Id
	 long id;
	 String wfdm;
	 String wfxw;
	 String qzyj;
	 String zt;
	 String gxsj;
	 String bz;

	public String getWfdm() {
		return wfdm;
	}

	public void setWfdm(String wfdm) {
		this.wfdm = wfdm;
	}

	public String getWfxw() {
		return wfxw;
	}

	public void setWfxw(String wfxw) {
		this.wfxw = wfxw;
	}

	public String getQzyj() {
		return qzyj;
	}

	public void setQzyj(String qzyj) {
		this.qzyj = qzyj;
	}

	public String getZt() {
		return zt;
	}

	public void setZt(String zt) {
		this.zt = zt;
	}

	public String getGxsj() {
		return gxsj;
	}

	public void setGxsj(String gxsj) {
		this.gxsj = gxsj;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

}
