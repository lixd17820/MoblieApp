package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MenuOptionBean implements Serializable {
	private int id;
	private String menuName;
	private String img;
	private int qx;
	private String pck;
	private String className;
	private String dataName;
	private String data;
	private String catalog;
	private boolean isBadge;
	private String badgeText;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getQx() {
		return qx;
	}

	public void setQx(int qx) {
		this.qx = qx;
	}

	public String getPck() {
		return pck;
	}

	public void setPck(String pck) {
		this.pck = pck;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public boolean isBadge() {
		return isBadge;
	}

	public void setBadge(boolean badge) {
		isBadge = badge;
	}

	public String getBadgeText() {
		return badgeText;
	}

	public void setBadgeText(String badgeText) {
		this.badgeText = badgeText;
	}
}
