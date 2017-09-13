package com.jwt.pojo;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ZapcRyjbxxBean implements Serializable {
	@Id
	long id;
	private String rybh;// 人员编号
	private String gmsfhm;// 公民身份号码
	private String xm;// 姓名
	private String zjzl;// 证件种类
	private String zjhm;// 证件号码
	private String xb;// 性别
	private String mz;// 民族
	private String csrq;// 出生日期
	private String jgxz;// 籍贯详址
	private String zjxy;// 宗教信仰
	private String zzmm;// 政治面貌
	private String whcd;// 文化程度
	private String hyzk;// 婚姻状况
	private String byzk;// 兵役状况－兵役状况
	private String sg;// 身高
	private String sf;// 身份
	private String zylb;// 职业类别
	private String fwcs;// 服务处所
	private String lxdh;// 联系电话
	private String hjqh;// 户籍区划
	private String hjxz;// 户籍详址
	private String xxjb;// 信息级别
	private String rylb;// 人员类别
	private String rysx;// 人员属性
	private String xzzqh;// 现住址区划
	private String xzzxz;// 现住址详址
	private String scbj;

	public long getId() {
		return id;
	}

	public String getRybh() {
		return rybh;
	}

	public void setRybh(String rybh) {
		this.rybh = rybh;
	}

	public String getGmsfhm() {
		return gmsfhm;
	}

	public void setGmsfhm(String gmsfhm) {
		this.gmsfhm = gmsfhm;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getZjzl() {
		return zjzl;
	}

	public void setZjzl(String zjzl) {
		this.zjzl = zjzl;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getXb() {
		return xb;
	}

	public void setXb(String xb) {
		this.xb = xb;
	}

	public String getMz() {
		return mz;
	}

	public void setMz(String mz) {
		this.mz = mz;
	}

	public String getCsrq() {
		return csrq;
	}

	public void setCsrq(String csrq) {
		this.csrq = csrq;
	}

	public String getJgxz() {
		return jgxz;
	}

	public void setJgxz(String jgxz) {
		this.jgxz = jgxz;
	}

	public String getZjxy() {
		return zjxy;
	}

	public void setZjxy(String zjxy) {
		this.zjxy = zjxy;
	}

	public String getZzmm() {
		return zzmm;
	}

	public void setZzmm(String zzmm) {
		this.zzmm = zzmm;
	}

	public String getWhcd() {
		return whcd;
	}

	public void setWhcd(String whcd) {
		this.whcd = whcd;
	}

	public String getHyzk() {
		return hyzk;
	}

	public void setHyzk(String hyzk) {
		this.hyzk = hyzk;
	}

	public String getByzk() {
		return byzk;
	}

	public void setByzk(String byzk) {
		this.byzk = byzk;
	}

	public String getSg() {
		return sg;
	}

	public void setSg(String sg) {
		this.sg = sg;
	}

	public String getSf() {
		return sf;
	}

	public void setSf(String sf) {
		this.sf = sf;
	}

	public String getZylb() {
		return zylb;
	}

	public void setZylb(String zylb) {
		this.zylb = zylb;
	}

	public String getFwcs() {
		return fwcs;
	}

	public void setFwcs(String fwcs) {
		this.fwcs = fwcs;
	}

	public String getLxdh() {
		return lxdh;
	}

	public void setLxdh(String lxdh) {
		this.lxdh = lxdh;
	}

	public String getHjqh() {
		return hjqh;
	}

	public void setHjqh(String hjqh) {
		this.hjqh = hjqh;
	}

	public String getHjxz() {
		return hjxz;
	}

	public void setHjxz(String hjxz) {
		this.hjxz = hjxz;
	}

	public String getXxjb() {
		return xxjb;
	}

	public void setXxjb(String xxjb) {
		this.xxjb = xxjb;
	}

	public String getRylb() {
		return rylb;
	}

	public void setRylb(String rylb) {
		this.rylb = rylb;
	}

	public String getRysx() {
		return rysx;
	}

	public void setRysx(String rysx) {
		this.rysx = rysx;
	}

	public String getXzzqh() {
		return xzzqh;
	}

	public void setXzzqh(String xzzqh) {
		this.xzzqh = xzzqh;
	}

	public String getXzzxz() {
		return xzzxz;
	}

	public void setXzzxz(String xzzxz) {
		this.xzzxz = xzzxz;
	}

	public String getScbj() {
		return scbj;
	}

	public void setScbj(String scbj) {
		this.scbj = scbj;
	}

}
