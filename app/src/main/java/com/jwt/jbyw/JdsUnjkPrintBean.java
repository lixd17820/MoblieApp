package com.jwt.jbyw;

import java.io.Serializable;

import android.text.TextUtils;

import com.jwt.bean.CommTwoRowSelUpInf;

@SuppressWarnings("serial")
public class JdsUnjkPrintBean implements Serializable, CommTwoRowSelUpInf {

	private String jdsbh;
	private String jszh;
	private String dabh;
	private String dsr;
	private String fzjg;
	private String zjcx;
	private String dh;
	private String hpzl;
	private String hphm;
	private String jtfs;
	private String wfsj;
	private String wfdz;
	private String wfxw;
	private String fkje;
	private String wfjfs;
	private String zqmj;

	public String getJdsbh() {
		return jdsbh;
	}

	public void setJdsbh(String jdsbh) {
		this.jdsbh = jdsbh;
	}

	public String getJszh() {
		return jszh;
	}

	public void setJszh(String jszh) {
		this.jszh = jszh;
	}

	public String getDabh() {
		return dabh;
	}

	public void setDabh(String dabh) {
		this.dabh = dabh;
	}

	public String getDsr() {
		return dsr;
	}

	public void setDsr(String dsr) {
		this.dsr = dsr;
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

	public String getDh() {
		return dh;
	}

	public void setDh(String dh) {
		this.dh = dh;
	}

	public String getHpzl() {
		return hpzl;
	}

	public void setHpzl(String hpzl) {
		this.hpzl = hpzl;
	}

	public String getHphm() {
		return hphm;
	}

	public void setHphm(String hphm) {
		this.hphm = hphm;
	}

	public String getJtfs() {
		return jtfs;
	}

	public void setJtfs(String jtfs) {
		this.jtfs = jtfs;
	}

	public String getWfsj() {
		return wfsj;
	}

	public void setWfsj(String wfsj) {
		this.wfsj = wfsj;
	}

	public String getWfdz() {
		return wfdz;
	}

	public void setWfdz(String wfdz) {
		this.wfdz = wfdz;
	}

	public String getWfxw() {
		return wfxw;
	}

	public void setWfxw(String wfxw) {
		this.wfxw = wfxw;
	}

	public String getFkje() {
		return fkje;
	}

	public void setFkje(String fkje) {
		this.fkje = fkje;
	}

	public String getWfjfs() {
		return wfjfs;
	}

	public void setWfjfs(String wfjfs) {
		this.wfjfs = wfjfs;
	}

	public String getZqmj() {
		return zqmj;
	}

	public void setZqmj(String zqmj) {
		this.zqmj = zqmj;
	}

	@Override
	public String getUpText() {
		return jdsbh
				+ " | "
				+ fkje
				+ "元"
				+ ((!TextUtils.isEmpty(wfjfs) && !TextUtils.equals(wfjfs, "0")) ? (" 记"
						+ wfjfs + "分")
						: "");
	}

	@Override
	public String getDownText() {
		return dsr + " | " + wfsj;
	}

	@Override
	public boolean isUploaded() {
		return false;
	}

}
