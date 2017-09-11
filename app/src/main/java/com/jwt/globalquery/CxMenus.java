package com.jwt.globalquery;

import java.io.Serializable;

public class CxMenus implements Serializable {

	/**
	 * 顺序号
	 */
	private String xh;

	/**
	 * 查询的中文名称
	 */
	private String cxMenuName;

	/**
	 * 查询的代码
	 */
	private String cxId;

	/**
	 * 图标
	 */
	private String img;

	/**
	 * 具体的查询项目
	 */
	private CxItem[] cxItems;

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getCxMenuName() {
		return cxMenuName;
	}

	public void setCxMenuName(String cxMenuName) {
		this.cxMenuName = cxMenuName;
	}

	public String getCxId() {
		return cxId;
	}

	public void setCxId(String cxId) {
		this.cxId = cxId;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public CxItem[] getCxItems() {
		return cxItems;
	}

	public void setCxItems(CxItem[] cxItems) {
		this.cxItems = cxItems;
	}

}
