package com.jwt.zapc;

import java.io.Serializable;


public class ZapcWppcxxBean implements Serializable, Zapcxx {

	private String xlpcwpbh; // '巡逻盘查物品编号';
	private String bpcwpgzqkbh; // '盘查物品工作情况编号';
	private String bpcwprybh; // '盘查物品人员编号';
	private String bpcwplx; // '被盘查物品类型';
	private String bpcwpmc; // '被盘查物品名称';
	private String bpcwpcp; // '被盘查物品厂牌';
	private String bpcwpxh; // '被盘查物品型号';
	private String clxh; // '车辆型号';
	private String clhpzl; // '车辆号牌种类';
	private String bhy; // 车辆号牌，原名编号一
	private String bhe; // 发动机号
	private String bhs; // 车架号
	private String bpcwppcsj; // '被盘查物品盘查时间';
	private String bpcwppcdd; // '被盘查物品盘查地点';
	private String pcyy; // '盘查物品原因';
	private String bpcwpcljg; // '被盘查物品处理结果';
	private String bpcwplb; // 被盘查物品类别
	private String clpp;
	private String syr;
	private String sfzmhm;
	private String jccfx;// 进出城方向
	private String scbj;

	public String getXlpcwpbh() {
		return xlpcwpbh;
	}

	public void setXlpcwpbh(String xlpcwpbh) {
		this.xlpcwpbh = xlpcwpbh;
	}

	public String getBpcwpgzqkbh() {
		return bpcwpgzqkbh;
	}

	public void setBpcwpgzqkbh(String bpcwpgzqkbh) {
		this.bpcwpgzqkbh = bpcwpgzqkbh;
	}

	public String getBpcwprybh() {
		return bpcwprybh;
	}

	public void setBpcwprybh(String bpcwprybh) {
		this.bpcwprybh = bpcwprybh;
	}

	public String getBpcwplx() {
		return bpcwplx;
	}

	public void setBpcwplx(String bpcwplx) {
		this.bpcwplx = bpcwplx;
	}

	public String getBpcwpmc() {
		return bpcwpmc;
	}

	public void setBpcwpmc(String bpcwpmc) {
		this.bpcwpmc = bpcwpmc;
	}

	public String getBpcwpcp() {
		return bpcwpcp;
	}

	public void setBpcwpcp(String bpcwpcp) {
		this.bpcwpcp = bpcwpcp;
	}

	public String getBpcwpxh() {
		return bpcwpxh;
	}

	public void setBpcwpxh(String bpcwpxh) {
		this.bpcwpxh = bpcwpxh;
	}

	public String getClxh() {
		return clxh;
	}

	public void setClxh(String clxh) {
		this.clxh = clxh;
	}

	public String getClhpzl() {
		return clhpzl;
	}

	public void setClhpzl(String clhpzl) {
		this.clhpzl = clhpzl;
	}

	public String getBhy() {
		return bhy;
	}

	public void setBhy(String bhy) {
		this.bhy = bhy;
	}

	public String getBhe() {
		return bhe;
	}

	public void setBhe(String bhe) {
		this.bhe = bhe;
	}

	public String getBhs() {
		return bhs;
	}

	public void setBhs(String bhs) {
		this.bhs = bhs;
	}

	public String getBpcwppcsj() {
		return bpcwppcsj;
	}

	public void setBpcwppcsj(String bpcwppcsj) {
		this.bpcwppcsj = bpcwppcsj;
	}

	public String getBpcwppcdd() {
		return bpcwppcdd;
	}

	public void setBpcwppcdd(String bpcwppcdd) {
		this.bpcwppcdd = bpcwppcdd;
	}

	public String getPcyy() {
		return pcyy;
	}

	public void setPcyy(String pcyy) {
		this.pcyy = pcyy;
	}

	// public String getDjsj() {
	// return djsj;
	// }
	//
	// public void setDjsj(String djsj) {
	// this.djsj = djsj;
	// }

	public String getBpcwpcljg() {
		return bpcwpcljg;
	}

	public void setBpcwpcljg(String bpcwpcljg) {
		this.bpcwpcljg = bpcwpcljg;
	}

	// public String getBpcwpclqj() {
	// return bpcwpclqj;
	// }
	//
	// public void setBpcwpclqj(String bpcwpclqj) {
	// this.bpcwpclqj = bpcwpclqj;
	// }

	public String getBpcwplb() {
		return bpcwplb;
	}

	public void setBpcwplb(String bpcwplb) {
		this.bpcwplb = bpcwplb;
	}

	public String getJccfx() {
		return jccfx;
	}

	public void setJccfx(String jccfx) {
		this.jccfx = jccfx;
	}

	@Override
	public String getGlgzbh() {
		return bpcwpgzqkbh;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return xlpcwpbh == null ? -1 : Integer.valueOf(xlpcwpbh);
	}

	@Override
	public int getPcZl() {
		return PCWPXXZL;
	}

	@Override
	public String getPczlMs() {
		return "机动车";
	}

	@Override
	public String getXxms() {
//		return "盘查时间："
//				+ ZaPcdjDao.changeDptModNor(bpcwppcsj)
//				+ "\n"
//				+ GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList,
//						clhpzl) + "\t车号：" + bhy;
		return "";
	}

	public String getScbj() {
		return scbj;
	}

	public void setScbj(String scbj) {
		this.scbj = scbj;
	}

	@Override
	public String getPcdd() {
		return bpcwppcdd;
	}

	public String getClpp() {
		return clpp;
	}

	public void setClpp(String clpp) {
		this.clpp = clpp;
	}

	public String getSyr() {
		return syr;
	}

	public void setSyr(String syr) {
		this.syr = syr;
	}

	public String getSfzmhm() {
		return sfzmhm;
	}

	public void setSfzmhm(String sfzmhm) {
		this.sfzmhm = sfzmhm;
	}

}
