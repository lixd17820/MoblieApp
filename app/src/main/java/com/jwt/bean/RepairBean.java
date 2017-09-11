package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RepairBean implements Serializable {

	private long id;
	private String xtbh;
	private String item;
	private String xzqh;
	private String bxdd;
	private String side;
	private String bxsj;
	private String bxnr;
	private String pic;
	private long scbj;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getXtbh() {
		return xtbh;
	}

	public void setXtbh(String xtbh) {
		this.xtbh = xtbh;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getXzqh() {
		return xzqh;
	}

	public void setXzqh(String xzqh) {
		this.xzqh = xzqh;
	}

	public String getBxdd() {
		return bxdd;
	}

	public void setBxdd(String bxdd) {
		this.bxdd = bxdd;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String getBxsj() {
		return bxsj;
	}

	public void setBxsj(String bxsj) {
		this.bxsj = bxsj;
	}

	public String getBxnr() {
		return bxnr;
	}

	public void setBxnr(String bxnr) {
		this.bxnr = bxnr;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public long getScbj() {
		return scbj;
	}

	public void setScbj(long scbj) {
		this.scbj = scbj;
	}

}
