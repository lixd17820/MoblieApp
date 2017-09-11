package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginResultBean implements Serializable {
	private String code;
	private String cwms;
	private UpdateFile[] ufs;
	private LoginMjxxBean mj;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCwms() {
		return cwms;
	}

	public void setCwms(String cwms) {
		this.cwms = cwms;
	}

	public UpdateFile[] getUfs() {
		return ufs;
	}

	public void setUfs(UpdateFile[] ufs) {
		this.ufs = ufs;
	}

	public LoginMjxxBean getMj() {
		return mj;
	}

	public void setMj(LoginMjxxBean mj) {
		this.mj = mj;
	}

}
