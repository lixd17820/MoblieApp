package com.jwt.globalquery;

import java.io.Serializable;

public class Glcx implements Serializable {
	private String glcxid;
	private String glcxms;
	private String destField;
	private String sourField;

	public String getGlcxid() {
		return glcxid;
	}

	public void setGlcxid(String glcxid) {
		this.glcxid = glcxid;
	}

	public String getGlcxms() {
		return glcxms;
	}

	public void setGlcxms(String glcxms) {
		this.glcxms = glcxms;
	}

	public String getDestField() {
		return destField;
	}

	public void setDestField(String destField) {
		this.destField = destField;
	}

	public String getSourField() {
		return sourField;
	}

	public void setSourField(String sourField) {
		this.sourField = sourField;
	}

}
