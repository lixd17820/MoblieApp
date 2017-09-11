package com.jwt.globalquery;

import java.io.Serializable;

public class GlobalQueryResult implements Serializable {

	private String cxid;
	private String cxms;
	private String[] names;
	private String[] comments;
	private String[][] contents;
	private Glcx[] glcxs;
	private String bdxx;

	public String getCxid() {
		return cxid;
	}

	public void setCxid(String cxid) {
		this.cxid = cxid;
	}

	public String getCxms() {
		return cxms;
	}

	public void setCxms(String cxms) {
		this.cxms = cxms;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

	public String[][] getContents() {
		return contents;
	}

	public void setContents(String[][] contents) {
		this.contents = contents;
	}

	public Glcx[] getGlcxs() {
		return glcxs;
	}

	public void setGlcxs(Glcx[] glcxs) {
		this.glcxs = glcxs;
	}

	public String getBdxx() {
		return bdxx;
	}

	public void setBdxx(String bdxx) {
		this.bdxx = bdxx;
	}

}
