package com.jwt.bean;

public class TwoColTwoSelectBean {
	private String leftText;
	private String rightText;
	private boolean isSelectUp;
	private boolean isSelectDown;

	public TwoColTwoSelectBean() {
		this.isSelectUp = false;
		this.isSelectDown = false;
	}

	public TwoColTwoSelectBean(String leftText, String rightText) {
		this.leftText = leftText;
		this.rightText = rightText;
		this.isSelectUp = false;
		this.isSelectDown = false;
	}

	public TwoColTwoSelectBean(String leftText, String rightText,
			boolean isSelectUp, boolean isSelectDown) {
		this.leftText = leftText;
		this.rightText = rightText;
		this.isSelectUp = isSelectUp;
		this.isSelectDown = isSelectDown;
	}

	public String getLeftText() {
		return leftText;
	}

	public void setLeftText(String leftText) {
		this.leftText = leftText;
	}

	public String getRightText() {
		return rightText;
	}

	public void setRightText(String rightText) {
		this.rightText = rightText;
	}

	public boolean isSelectUp() {
		return isSelectUp;
	}

	public void setSelectUp(boolean isSelectUp) {
		this.isSelectUp = isSelectUp;
	}

	public boolean isSelectDown() {
		return isSelectDown;
	}

	public void setSelectDown(boolean isSelectDown) {
		this.isSelectDown = isSelectDown;
	}

}
