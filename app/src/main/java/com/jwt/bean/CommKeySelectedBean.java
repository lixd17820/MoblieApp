package com.jwt.bean;

public class CommKeySelectedBean {

	private CommTwoRowSelUpInf key;
	private boolean isSelected;

	public CommKeySelectedBean(CommTwoRowSelUpInf key, boolean isSelected) {
		this.key = key;
		this.isSelected = isSelected;
	}

	public CommTwoRowSelUpInf getKey() {
		return key;
	}

	public void setKey(CommTwoRowSelUpInf key) {
		this.key = key;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
