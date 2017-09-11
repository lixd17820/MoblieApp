package com.jwt.globalquery;

import java.io.Serializable;

/**
 * 这个类用来表示一个查询的项目，例如：身份证号：【EditText】
 * 
 * @author lixd
 * 
 */


public class CxItem implements Serializable {

	/**
	 * 字段的名称，用来拼接查询条件
	 */
	private String itemMc;
	/**
	 * 是编辑框或是下拉框
	 */
	private String itemLx;
	/**
	 * 中文标签
	 */
	private String itemLabel;
	/**
	 * 数组的名称
	 */
	private String itemArray;

	/**
	 * 比较关系
	 */
	private String itemBjgx;

	/**
	 * 默认值
	 */
	private String itemDeValue;

	/**
	 * 输入法名称
	 */
	private String itemInMethod;

	public String getItemMc() {
		return itemMc;
	}

	public void setItemMc(String itemMc) {
		this.itemMc = itemMc;
	}

	public String getItemLx() {
		return itemLx;
	}

	public void setItemLx(String itemLx) {
		this.itemLx = itemLx;
	}

	public String getItemLabel() {
		return itemLabel;
	}

	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	public String getItemArray() {
		return itemArray;
	}

	public void setItemArray(String itemArray) {
		this.itemArray = itemArray;
	}

	public String getItemBjgx() {
		return itemBjgx;
	}

	public void setItemBjgx(String itemBjgx) {
		this.itemBjgx = itemBjgx;
	}

	public String getItemDeValue() {
		return itemDeValue;
	}

	public void setItemDeValue(String itemDeValue) {
		this.itemDeValue = itemDeValue;
	}

	public String getItemInMethod() {
		return itemInMethod;
	}

	public void setItemInMethod(String itemInMethod) {
		this.itemInMethod = itemInMethod;
	}

}
